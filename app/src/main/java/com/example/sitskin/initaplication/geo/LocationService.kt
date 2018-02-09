package com.example.sitskin.initaplication.geo

import android.content.Context
import android.location.Location
import android.net.wifi.WifiManager
import android.text.format.Formatter
import com.example.hopapilibrary.api.GeoLocationApi
import com.example.hopbaselibrary.android.util.prefs.PrefUtils
import com.example.sitskin.initaplication.App
import com.example.sitskin.initaplication.util.LocationUtils
import com.example.sitskin.initaplication.util.LocationUtils.isLocationSettingsSuccessful
import com.example.sitskin.initaplication.util.LocationUtils.isLocationValid
import com.example.sitskin.initaplication.util.prefs.LaunchPrefUtils.Companion.shouldSkipRetrievingLocation
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResult
import pl.charmas.android.reactivelocation.ReactiveLocationProvider
import rx.Observable
import rx.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service to manage tracking a users location
 */
@Singleton
class LocationService @Inject constructor(private val geoLocationApi: GeoLocationApi) {

    private val provider: ReactiveLocationProvider = ReactiveLocationProvider( App.context )

    /**
     * Gets the user's [Location].
     *
     * Will attempt to get the user's last known location from local cache,
     * the last known location and an updated location from the Google Play Services API.
     * Whichever location is received first will be returned to the Observable subscriber.
     *
     * Also updates the cached last known location whenever a new location is returned
     *
     *
     * @return first available location
     */
    val location: Observable<Location>
        get() {

            val request = LocationRequest.create()
                .setPriority( LocationRequest.PRIORITY_HIGH_ACCURACY )
                .setNumUpdates( 1 )

            // user entered location
            val userEnteredLocation = PrefUtils.userEnteredLocation

            if( isLocationValid( userEnteredLocation ) && PrefUtils.shouldUseUserEnteredLocation()) {
                return Observable.just( userEnteredLocation )
            }

            // last known location from Google
            val getLastKnownLocationWithCaching = provider.lastKnownLocation
                .doOnNext { location -> PrefUtils.lastKnownLocation = location }
                .timeout( 1, TimeUnit.SECONDS)
                .onErrorResumeNext( Observable.just<Location>( null ) )

            // updated location from Google
            val getUpdatedLocationWithCaching = provider.getUpdatedLocation( request )
                .doOnNext { location -> PrefUtils.lastKnownLocation = location }
                .timeout( ( if( shouldSkipRetrievingLocation() ) 1 else 3 ).toLong(), TimeUnit.SECONDS)
                .onErrorResumeNext( Observable.just<Location>( null ) )

            // cached location from Google
            val getCachedLocation = Observable.just<Location>(PrefUtils.lastKnownLocation)

            // geocoded location
            val getGeocodedLocation = Observable.defer { geocodedLocation }
                .timeout( 5, TimeUnit.SECONDS)
                .onErrorResumeNext( Observable.just( fallbackLocation ) )

            return Observable
                .concat(
                    getLastKnownLocationWithCaching,
                    getUpdatedLocationWithCaching,
                    getCachedLocation,
                    getGeocodedLocation
                )
                .subscribeOn( Schedulers.io() )
                .filter( { isLocationValid( it ) } )
                .first()
        }

    val highAccuracyLocation: Observable<Location>
        get() {

            val request = LocationRequest.create()
                .setPriority( LocationRequest.PRIORITY_HIGH_ACCURACY )
                .setNumUpdates( 1 )

            // last known location from Google
            val getLastKnownLocationWithCaching = provider.lastKnownLocation
                .doOnNext { location -> PrefUtils.lastKnownLocation = location }
                .timeout( 1, TimeUnit.SECONDS)
                .onErrorResumeNext( Observable.just< Location? >( null ) )

            // updated location from Google
            val getUpdatedLocationWithCaching = provider.getUpdatedLocation( request )
                .doOnNext { location -> PrefUtils.lastKnownLocation = location }
                .timeout( 3, TimeUnit.SECONDS)
                .onErrorResumeNext( Observable.just< Location? >( null ) )

            return Observable
                .concat(
                    getLastKnownLocationWithCaching,
                    getUpdatedLocationWithCaching
                )
                .subscribeOn( Schedulers.io() )
                .filter( { LocationUtils.isLocationValid( it ) } )
                .first()
                .onErrorResumeNext { Observable.error( LocationUnavailableException( it ) ) }
        }

    val geocodedLocation: Observable<Location>
        get() = geoLocationApi.geoLocationGet( null )
            .map { output ->

                val ( lat, lng ) = output

                Timber.i( "Geocoded location - Lat: $lat, Lng: $lng" )

                val location = Location( "Geolocation" )

                location.latitude = lat
                location.longitude = lng
                location.accuracy = 0.0f

                location
            }
            .doOnNext { location -> PrefUtils.setGeocodedLocation(null, location) }

    private val ipAddress: String
        get() {
            val wm = App.context.applicationContext.getSystemService( Context.WIFI_SERVICE ) as WifiManager
            return Formatter.formatIpAddress( wm.connectionInfo.ipAddress )
        }

    val isLocationAvailable: Observable<Boolean>
        get() = if( isLocationValid(PrefUtils.userEnteredLocation) || isLocationValid(PrefUtils.lastGeocodedLocation) )
            Observable.just( true )
        else
            checkLocationSettings().map { s -> isLocationSettingsSuccessful( s ) }

    private val fallbackLocation: Location
        get() {
            val fallbackLocation = Location( "Fallback" )
            fallbackLocation.latitude = 30.267153
            fallbackLocation.longitude = - 97.743061
            return fallbackLocation
        }

    fun checkLocationSettings(): Observable<LocationSettingsResult> {

        val locationRequest = LocationRequest.create()
            .setPriority( LocationRequest.PRIORITY_HIGH_ACCURACY )
            .setNumUpdates( 1 )

        return provider.checkLocationSettings(
            LocationSettingsRequest.Builder()
                .addLocationRequest( locationRequest )
                .setAlwaysShow( true )
                .build()
        )
    }

    class LocationUnavailableException internal constructor( throwable: Throwable ) : Exception( throwable )

    companion object {

        const val REQUEST_ENABLE_LOCATION = 411

        fun isLocationAccurate( location: Location? ): Boolean {
            return location != null &&
                location.accuracy > 0.0f &&
                location.accuracy < 1000.0f &&
                location.latitude != 0.0 &&
                location.longitude != 0.0
        }
    }

}
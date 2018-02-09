package com.example.sitskin.initaplication.api

import android.location.Location
import com.example.hopbaselibrary.android.util.prefs.PrefUtils
import com.example.sitskin.initaplication.App
import com.example.sitskin.initaplication.R
import com.example.sitskin.initaplication.geo.LocationService
import com.example.sitskin.initaplication.util.AppUtils.versionName
import com.example.sitskin.initaplication.util.LocationUtils.isLocationValid
import okhttp3.Interceptor
import okhttp3.Request
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

abstract class BaseInterceptor : Interceptor {

    @Inject
    lateinit var deviceHandler: DeviceRegistrationHandler
    @Inject
    lateinit var locationService: LocationService

    private val location: Location?
        get() {

            var location = PrefUtils.lastKnownLocation

            if( ! isLocationValid( location ) ) {

                location = locationService.location
                    .timeout( 5, TimeUnit.SECONDS)
                    .onErrorReturn { null }
                    .toBlocking()
                    .firstOrDefault( null )
            }

            return location
        }

    private val deviceId: String
        get() {
            val deviceId = deviceHandler.deviceId

            return if( deviceId?.isEmpty() == false ) deviceId else deviceHandler.register().onErrorReturn { null }.toBlocking().first()
        }

    init {
        App.component.inject( this )
    }

    fun addUserAgentHeader( builder: Request.Builder ): Request.Builder {

        val userAgent = App.context.getString( R.string.user_agent, versionName )

        return builder.header( HEADER_USER_AGENT, userAgent )
    }

    fun addOdeHeaders( requestBuilder: Request.Builder ): Request.Builder {

        val location = location

        location?.let {

            val latLng = if( isLocationValid( it ) ) java.lang.String.format(Locale.US, "%.6f,%.6f", it.latitude, it.longitude) else ""

            requestBuilder
                .header( HEADER_ODE_LOCATION, latLng )
                .header( HEADER_ODE_LOCATION_ACCURACY, it.accuracy.toString() )
        }

        requestBuilder
            .header( HEADER_ODE_APP_INSTALL_ID, deviceId )

        return requestBuilder
    }

    companion object {
        private const val HEADER_USER_AGENT = "User-Agent"
        private const val HEADER_ODE_LOCATION = "ODE-Location"
        private const val HEADER_ODE_LOCATION_ACCURACY = "ODE-Location-Accuracy"
        private const val HEADER_ODE_APP_INSTALL_ID = "ODE-AppInstall-Id"
    }
}

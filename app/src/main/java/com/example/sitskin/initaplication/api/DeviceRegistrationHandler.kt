package com.example.sitskin.initaplication.api

import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Build
import com.example.hopapilibrary.api.DeviceApi
import com.example.hopapilibrary.model.AppInstallInputModel
import com.example.sitskin.initaplication.App
import com.example.sitskin.initaplication.util.prefs.PrefUtils
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.gms.iid.InstanceID
import com.segment.analytics.Analytics
import rx.Observable
import rx.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class DeviceRegistrationHandler @Inject constructor(private val deviceApi: DeviceApi) {

    private val context: Context = App.context

    private var deviceIdObservable: Observable<String>? = null

    private val inputModel: Observable<AppInstallInputModel>
        get() = Observable
            .combineLatest(
                buildBaseInputModel(),
                getAnalyticsId( context ),
                getAdvertisingId( context )
            ) { model, analyticsId, advertisingId ->
                if( ! analyticsId.isNullOrEmpty() ) {
                    model.currentAnalyticsId = analyticsId
                }
                if( ! advertisingId.isNullOrEmpty() ) {
                    model.advertisingId = advertisingId
                }
                model
            }

    private val pushNotificationToken: String?
        get() = PrefUtils.gcmId

    private val appInstallId: String
        get() {
            var appInstallId = PrefUtils.instanceId

            if( appInstallId != null ) {
                return appInstallId
            }

            appInstallId = InstanceID.getInstance( context ).id

            PrefUtils.instanceId = appInstallId

            return appInstallId
        }

    private val deviceResolution: String
        get() {
            val displayMetrics = context.resources.displayMetrics

            return "${ displayMetrics.widthPixels }x${ displayMetrics.heightPixels }"
        }

    private val appVersion: String
        get() {

            try {
                val packageName = context.packageName
                val packageInfo = context.packageManager.getPackageInfo( packageName, 0 )

                return packageInfo.versionName.toString()

            } catch( e: PackageManager.NameNotFoundException ) {
                e.printStackTrace()
            }

            return ""
        }

    private val osVersion: String
        get() = Build.VERSION.RELEASE

    private val osApiVersion: String
        get() = Build.VERSION.SDK_INT.toString()

    private val deviceMake: String
        get() = Build.MANUFACTURER

    private val deviceModel: String
        get() = Build.MODEL

    private val macAddress: String
        get() {
            val manager = context.getSystemService( Context.WIFI_SERVICE ) as WifiManager
            val info = manager.connectionInfo
            return info.macAddress
        }

    var deviceId: String? = null
        get() {
            if( field == null ) {
                field = PrefUtils.deviceId
            }
            return field
        }
        private set( id ) {

            field = id

            PrefUtils.deviceId = id

            Timber.i( "Received device id: " + id )
        }
    fun register(): Observable<String> {

        Timber.i( "Registering device" )

        if( deviceId != null ) {
            return Observable.just( deviceId )
        }

        return deviceIdObservable ?: createDeviceIdObservable()
    }

    private fun createDeviceIdObservable(): Observable<String> {
        val observable = inputModel
            .subscribeOn( Schedulers.io() )
            .flatMap { model -> deviceApi.registerDevice( model ) }
            .cache()

        observable?.subscribe( { id -> deviceId = id } ) { Timber.e( it, "Failed to register device" ) }

        deviceIdObservable = observable

        return observable
    }

    private fun buildBaseInputModel(): Observable<AppInstallInputModel> {
        return Observable.just(
            AppInstallInputModel(
                osType = AppInstallInputModel.OsTypeEnum.Android,
                odeAppInstallId = appInstallId,
                resolution = deviceResolution,
                appVersion = appVersion,
                osVersion = osVersion,
                osAPIVersion = osApiVersion,
                deviceMake = deviceMake,
                deviceModel = deviceModel,
                macAddressWifi = macAddress,
                pushNotificationToken = pushNotificationToken
            )
        )
    }

    private fun getAnalyticsId( context: Context): Observable<String> {
        return Observable.just( Analytics.with( context ).analyticsContext.traits().currentId() )
    }

    private fun getAdvertisingId( context: Context): Observable<String> {

        return Observable
            .defer {
                return@defer try {
                    Observable.just(AdvertisingIdClient.getAdvertisingIdInfo(context))
                } catch( e: Exception ) {
                    Observable.error< AdvertisingIdClient.Info >( e )
                }
            }
            .onErrorReturn { AdvertisingIdClient.Info( "", true ) }
            .subscribeOn( Schedulers.io() )
            .map { adInfo -> adInfo.id }
    }

}

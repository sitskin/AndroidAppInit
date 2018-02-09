package com.example.sitskin.initaplication.util

import android.location.Location
import com.google.android.gms.common.api.CommonStatusCodes.SUCCESS
import com.google.android.gms.location.LocationSettingsResult
import com.google.android.gms.maps.model.LatLng
import com.example.hopapilibrary.model.LocationInputModel
import com.example.hopapilibrary.model.LocationOutputModel
import java.lang.Double.parseDouble

object LocationUtils {

    fun toLocation( latLng: LatLng ): LocationOutputModel {
        return LocationOutputModel(
            latitude = latLng.latitude,
            longitude = latLng.longitude
        )
    }

    fun toLocation( locationOutput: LocationOutputModel): Location {
        val location = Location( "" )
        location.latitude = locationOutput.latitude
        location.longitude = locationOutput.longitude
        return location
    }

    fun toLocation( lat: Double, lng: Double ): Location {
        val location = Location( "" )
        location.latitude = lat
        location.longitude = lng
        return location
    }

    fun toLatLng( location: Location ): LatLng {
        return LatLng( location.latitude, location.longitude )
    }

    fun toLatLng( location: LocationOutputModel): LatLng {
        return LatLng( location.latitude, location.longitude )
    }

    fun toLatLng( latLngString: String ): LatLng? {
        if( latLngString.isEmpty() ) return null

        val latLng = latLngString.split( ",".toRegex() ).dropLastWhile { it.isEmpty() }.toTypedArray()

        try {
            return LatLng( parseDouble( latLng[ 0 ] ), parseDouble( latLng[ 1 ] ) )
        } catch( e: NumberFormatException ) {
            e.printStackTrace()
        }

        return null
    }

    fun toLocationInputModel( location: Location ): LocationInputModel {
        return LocationInputModel(
            latitude = location.latitude,
            longitude = location.longitude,
            horizontalAccuracy = location.accuracy.toDouble()
        )
    }

    fun toLocationInputModel( latLng: LatLng? ): LocationInputModel? {
        return latLng?.let {
            LocationInputModel(
                latitude = it.latitude,
                longitude = it.longitude
            )
        }
    }

    fun isLocationValid( location: Location? ): Boolean {
        return location != null && location.latitude != 0.0 && location.longitude != 0.0
    }

    fun isLocationSettingsSuccessful( result: LocationSettingsResult? ): Boolean {
        return result?.status?.statusCode == SUCCESS
    }

    fun toString( latLng: LatLng? ): String? {
        return latLng?.let { "${ latLng.latitude }, ${ latLng.longitude }" }
    }
}

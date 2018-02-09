package com.example.sitskin.initaplication.util.prefs

import com.example.hopbaselibrary.android.util.prefs.BasePrefUtils
import com.example.hopapilibrary.model.MeUserOutputModel

/**
 * Utility methods and constants for managing app preferences
 */
class PrefUtils : BasePrefUtils() {
    companion object {

        private const val REFRESH_TOKEN = "pref_refresh_token"
        private const val BEARER_TOKEN = "pref_bearer_token"
        private const val PREF_TOKEN_EXPIRATION = "PREF_TOKEN_EXPIRATION"
        private const val PREF_GOOGLE_ACCESS_TOKEN = "PREF_GOOGLE_ACCESS_TOKEN"
        /** Unique ID provided by Google, which is registered with GCM for Push Notifications  */
        private const val GCM_ID = "PREF_GCM_ID"
        /** Device ID provided by Google, which is the prefix for the [.GCM_ID]  */
        private const val DEVICE_ID = "PREF_DEVICE_ID"
        /** Install ID used to register a device with Hop. Current the same as the [.DEVICE_ID]  */
        private const val INSTANCE_ID = "PREF_INSTANCE_ID"
        private const val REFERRAL_CODE = "REFERRAL_CODE"
        private const val REFERRAL_CODE_REGISTERED = "REFERRAL_CODE_REGISTERED"
        /** The last navigation drawer tab chosen by the user  */
        private const val LAST_DRAWER_TAB_SELECTED = "LAST_DRAWER_TAB_SELECTED"
        /** The last perk dashboard screen chosen by the user  */
        private const val LAST_PERK_DASHBOARD_SELECTED = "LAST_PERK_DASHBOARD_SELECTED"
        /** Boolean set if the app should display dark/night mode  */
        const val USE_DARK_THEME = "USE_DARK_THEME"
        /** Boolean set when a user has been registered with our analytics provider (Segment.io)  */
        /** The [MeUserOutputModel] for the currently logged in user  */
        private const val LOGGED_IN_USER_PROFILE = "LOGGED_IN_USER_PROFILE"
        /** The User ID of the currently logged in user  */
        private const val LOGGED_IN_USER_ID = "LOGGED_IN_USER_ID"
        private const val LAST_KNOWN_LATITUDE = "LAST_KNOWN_LATITUDE"
        private const val LAST_KNOWN_LONGITUDE = "LAST_KNOWN_LONGITUDE"
        private const val LAST_KNOWN_ACCURACY = "LAST_KNOWN_ACCURACY"
        private const val LAST_GEOCODED_LOCATION = "LAST_GEOCODED_LOCATION"
        private const val LAST_GEOCODED_IP_ADDRESS = "LAST_GEOCODED_IP_ADDRESS"
        private const val USER_ENTERED_LOCATION = "USER_ENTERED_LOCATION"
        private const val SEND_LOCATION_ENABLED = "SEND_LOCATION_ENABLED"
        private const val NOTIFICATION_TOKEN_REGISTERED = "NOTIFICATION_TOKEN_REGISTERED"
        private const val RUNSCOPE_ENABLED = "RUNSCOPE_ENABLED"

        var refreshToken: String?
            get() = prefs.getString( REFRESH_TOKEN, null )
            set( refreshToken ) =  putString( REFRESH_TOKEN, refreshToken )

        var bearerToken: String?
            get() = prefs.getString( BEARER_TOKEN, null )
            set( refreshToken ) = putString( BEARER_TOKEN, refreshToken )

        var tokenExpiration: Long
            get() = prefs.getLong( PREF_TOKEN_EXPIRATION, 0 )
            set( tokenExpiration ) = putLong( PREF_TOKEN_EXPIRATION, tokenExpiration )

        fun setTokenExpiration( tokenExpiration: Long? ) {
            tokenExpiration?.let { Companion.tokenExpiration = it }
        }

        var deviceId: String?
            get() = prefs.getString( DEVICE_ID, null )
            set( deviceId ) = putString( DEVICE_ID, deviceId )

        var gcmId: String?
            get() = prefs.getString( GCM_ID, null )
            set( gcmId ) = putString( GCM_ID, gcmId )

        var instanceId: String?
            get() = prefs.getString( INSTANCE_ID, null )
            set( instanceId ) = putString( INSTANCE_ID, instanceId )

        var googleAccessToken: String?
            get() = prefs.getString( PREF_GOOGLE_ACCESS_TOKEN, null )
            set( token ) = putString( PREF_GOOGLE_ACCESS_TOKEN, token )

        var lastPushNotificationTokenRegistered: String
            get() = prefs.getString( NOTIFICATION_TOKEN_REGISTERED, "" )
            set( token ) = putString( NOTIFICATION_TOKEN_REGISTERED, token )

        fun markNotificationTokenRegistered( token: String ) {
            lastPushNotificationTokenRegistered = token
        }

        fun markReferralCodeRegistered( referralCode: String ) {
            putString( REFERRAL_CODE, referralCode )
            putBoolean( REFERRAL_CODE_REGISTERED, true )
        }

        val isReferralCodeRegistered: Boolean
            get() = prefs.getBoolean( REFERRAL_CODE_REGISTERED, false )

        val loggedInUserProfile: MeUserOutputModel?
            get() = fromJson<MeUserOutputModel>( prefs.getString( LOGGED_IN_USER_PROFILE, null ), MeUserOutputModel::class.java )

        val loggedInUserId: String?
            get() = prefs.getString( LOGGED_IN_USER_ID, null )

        fun setLoggedInUser( loggedInUser: MeUserOutputModel? ) {
            loggedInUser ?: return

            editor
                .putString( LOGGED_IN_USER_PROFILE, toJson( loggedInUser ) )
                .putString( LOGGED_IN_USER_ID, loggedInUser.id )
                .apply()
        }

        val lastSentLocationEnabledValue: Boolean?
            get() = when( prefs.getInt( SEND_LOCATION_ENABLED, - 1 ) ) {
                0 -> false
                1 -> true
                else -> null
            }

        fun setRunscopeEnabled( enabled: Boolean? ) {
            enabled?.let{ isRunscopeEnabled = it }
        }

        var isRunscopeEnabled: Boolean
            get() = prefs.getBoolean( RUNSCOPE_ENABLED, true )
            set( enabled ) = putBoolean( RUNSCOPE_ENABLED, enabled )
    }
}

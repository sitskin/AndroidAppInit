package com.example.sitskin.initaplication.util.prefs

import com.example.hopbaselibrary.android.ui.data.WelcomeMessage
import com.example.hopbaselibrary.android.util.prefs.BasePrefUtils
import com.google.gson.Gson

class LaunchPrefUtils : BasePrefUtils() {
    companion object {

        private const val PREF_LAUNCH_SEEN_APP_INTRO = "pref_launch_seen_app_onboarding"
        private const val PREF_LAUNCH_TRACKED_APP_LAUNCH = "pref_launch_tracked_app_launch"
        private const val PREF_LAUNCH_TRACKED_APP_OPEN = "pref_launch_tracked_app_open"
        private const val PREF_LAUNCH_SEEN_SHARED_ITEM = "pref_launch_seen_shared_item"
        private const val PREF_LAUNCH_SKIP_LOCATION = "pref_launch_skip_location"
        private const val PREF_LAUNCH_GROUP_INVITE_ID = "pref_launch_group_invite_id"
        private const val PREF_LAUNCH_GROUP_INVITE_ACCEPTED = "pref_launch_group_invite_accepted"
        private const val PREF_LAUNCH_WELCOME_MESSAGE = "pref_launch_welcome_message"

        fun markUserSeenAppIntro() {
            putBoolean( PREF_LAUNCH_SEEN_APP_INTRO, true )
        }

        fun hasSeenAppIntro(): Boolean {
            return prefs.getBoolean( PREF_LAUNCH_SEEN_APP_INTRO, false )
        }

        fun shouldSkipRetrievingLocation(): Boolean {
            return prefs.getBoolean( PREF_LAUNCH_SKIP_LOCATION, false )
        }

        fun markShouldSkipRetrievingLocation( shouldSkip: Boolean ) {
            putBoolean( PREF_LAUNCH_SKIP_LOCATION, shouldSkip )
        }

        fun markHasTrackedAppLaunch() {
            putBoolean( PREF_LAUNCH_TRACKED_APP_LAUNCH, true )
        }

        fun hasTrackedAppLaunch(): Boolean {
            return prefs.getBoolean( PREF_LAUNCH_TRACKED_APP_LAUNCH, false )
        }

        fun markHasTrackedAppOpen() {
            putBoolean( PREF_LAUNCH_TRACKED_APP_OPEN, true )
        }

        fun hasTrackedAppOpen(): Boolean {
            return prefs.getBoolean( PREF_LAUNCH_TRACKED_APP_OPEN, false )
        }

        fun hasSeenSharedItem(): Boolean {
            return prefs.getBoolean( PREF_LAUNCH_SEEN_SHARED_ITEM, false )
        }

        fun markUserSeenSharedItem( seenItem: Boolean ) {
            putBoolean( PREF_LAUNCH_SEEN_SHARED_ITEM, seenItem )
        }

        fun markPendingGroupInvite( groupId: String, accepted: Boolean ) {
            putString( PREF_LAUNCH_GROUP_INVITE_ID, groupId )
            putBoolean( PREF_LAUNCH_GROUP_INVITE_ACCEPTED, accepted )
        }

        val pendingGroupInviteId: String?
            get() = prefs.getString( PREF_LAUNCH_GROUP_INVITE_ID, null )

        fun resetLaunchFlags() {
            putBoolean( PREF_LAUNCH_TRACKED_APP_LAUNCH, false )
            putBoolean( PREF_LAUNCH_TRACKED_APP_OPEN, false )
            putBoolean( PREF_LAUNCH_SEEN_SHARED_ITEM, false )
        }

        fun markPendingWelcomeMessage( welcomeMessage: WelcomeMessage? ) {
            Companion.welcomeMessage = welcomeMessage
        }

        var welcomeMessage: WelcomeMessage?
            get() = Gson().fromJson( prefs.getString( PREF_LAUNCH_WELCOME_MESSAGE, null ), WelcomeMessage::class.java )
            set( welcomeMessage ) = putJson( PREF_LAUNCH_WELCOME_MESSAGE, welcomeMessage )
    }
}

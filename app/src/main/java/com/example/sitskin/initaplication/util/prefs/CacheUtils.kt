package com.example.sitskin.initaplication.util.prefs

import com.example.hopbaselibrary.android.util.prefs.BasePrefUtils
import com.example.hopapilibrary.model.ConversationSummaryOutputModel
import com.example.hopapilibrary.model.GroupOutputModel
import com.example.hopapilibrary.model.UserNotificationOutputModel
import com.example.sitskin.initaplication.R

class CacheUtils : BasePrefUtils() {
    companion object {

        /**
         * Caches the provided list of notifications for a user
         */
        fun cacheMyNotifications( notifications: List<UserNotificationOutputModel> ) {
            myNotifications = notifications
        }

        /**
         * Returns the last cached list of notifictions for a user
         */
        var myNotifications: List<UserNotificationOutputModel>
            get() = parseJsonList( getKey( R.string.pref_cache_my_notifications ), Array<UserNotificationOutputModel>::class.java )
            set( notifications ) = putJson( getKey( R.string.pref_cache_my_notifications ), notifications )

        /**
         * Caches the provided list of groups for a user
         * @param groups
         */
        fun cacheMyGroups( groups: List<GroupOutputModel> ) {
            myGroups = groups
        }

        /**
         * Returns the last cached list of groups for a user
         */
        var myGroups: List<GroupOutputModel>
            get() = parseJsonList( getKey( R.string.pref_cache_my_groups ), Array<GroupOutputModel>::class.java )
            set( groups ) = putJson( getKey( R.string.pref_cache_my_groups ), groups )

        /**
         * Caches the provided list of conversations for a user
         * @param conversations
         */
        fun cacheMyConversations( conversations: List<ConversationSummaryOutputModel> ) {
            myConversations = conversations
        }

        /**
         * Returns the last cached list of conversations for a user
         */
        var myConversations: List<ConversationSummaryOutputModel>
            get() = parseJsonList( getKey( R.string.pref_cache_my_conversations ), Array<ConversationSummaryOutputModel>::class.java )
            set( conversations ) = putJson( getKey( R.string.pref_cache_my_conversations ), conversations )

        /**
         * Caches the provided list of suggested groups to join
         */
        fun cacheSuggestedGroups( groups: List<GroupOutputModel> ) {
            cachedSuggestedGroups = groups
        }

        /**
         * Returns the last cached list of suggested groups to join
         */
        var cachedSuggestedGroups: List<GroupOutputModel>
            get() = parseJsonList( getKey( R.string.pref_cache_suggested_groups ), Array<GroupOutputModel>::class.java )
            set( groups ) = putJson( getKey( R.string.pref_cache_suggested_groups ), groups )
    }

}

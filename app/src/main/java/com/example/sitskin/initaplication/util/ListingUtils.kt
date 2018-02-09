package com.example.sitskin.initaplication.util

import com.example.sitskin.initaplication.util.prefs.PrefUtils.Companion.loggedInUserId
import com.example.hopapilibrary.model.UserOutputModel
import java.util.*

/**
 * Utility methods for handling Listings
 */
object ListingUtils {

    /**
     * Determines whether or not the provided user is the currently logged in user, by matching IDs/
     * @param user the user to compare to the logged in user against
     * @return true if the user is the person logged in, false in all other circumstances
     */
    fun isLoggedInUser( user: UserOutputModel? ): Boolean {
        return ( ! user?.id.isNullOrEmpty() ) && user?.id == loggedInUserId
    }

    fun formatUpdateDate( updateDate: Date? ): String {

        updateDate ?: return ""

        val now = Calendar.getInstance()

        val nowTime = now.timeInMillis
        val updateTime = updateDate.time + now.get( Calendar.ZONE_OFFSET ).toLong() + now.get( Calendar.DST_OFFSET ).toLong()

        val milliseconds = nowTime - updateTime
        val seconds = milliseconds / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        if( seconds < 60 ) return "Just Now"
        if( minutes < 2 ) return "1 min ago"
        if( minutes < 60 ) return formatTime( minutes, "%d mins ago" )
        if( hours < 2 ) return "1 hour ago"
        if( hours < 24 ) return formatTime( hours, "%d hours ago" )
        return if( days < 2 ) "1 day ago" else formatTime( days, "%d days ago" )

    }

    private fun formatTime( time: Long, format: String ): String {
        return String.format( format, Math.floor( time.toDouble() ).toInt() )
    }
}

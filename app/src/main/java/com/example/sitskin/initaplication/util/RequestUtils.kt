package com.example.sitskin.initaplication.util

import java.util.*

/**
 * Utility methods for handling Requests
 */
object RequestUtils {

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

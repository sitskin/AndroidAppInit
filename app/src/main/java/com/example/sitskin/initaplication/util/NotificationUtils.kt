package com.example.sitskin.initaplication.util

import android.app.NotificationManager
import android.content.Context
import android.content.IntentFilter
import android.graphics.Bitmap
import com.example.sitskin.initaplication.gcm.HopGcmListenerService
import com.example.sitskin.initaplication.ui.base.GlideApp
import timber.log.Timber
import java.util.concurrent.ExecutionException

object NotificationUtils {

    const val ARG_NOTIFICATION_ID = "notificationId"

    fun getThumbnail( context: Context, thumbnailUrl: String ): Bitmap? {
        if( ! thumbnailUrl.isEmpty() ) {
            try {
                val res = context.resources

                val height = res.getDimensionPixelSize( android.R.dimen.notification_large_icon_height )
                val width = res.getDimensionPixelSize( android.R.dimen.notification_large_icon_width )

                return GlideApp.with( context )
                    .asBitmap()
                    .load( thumbnailUrl )
                    .submit( width, height )
                    .get()

            } catch( e: InterruptedException ) {
                Timber.e( e, "Notification thumbnail load failed" )
                e.printStackTrace()
            } catch( e: ExecutionException ) {
                Timber.e( e, "Notification thumbnail load failed" )
                e.printStackTrace()
            }
        }
        return null
    }

    fun cancel( context: Context, notificationId: Int ) {
        ( context.getSystemService( Context.NOTIFICATION_SERVICE ) as NotificationManager ).cancel( notificationId )
    }

    fun filterForNotificationTypes( vararg types: HopGcmListenerService.NotificationType): IntentFilter {

        val filter = IntentFilter()

        for( type in types ) {
            filter.addAction( type.name )
        }
        return filter
    }
}

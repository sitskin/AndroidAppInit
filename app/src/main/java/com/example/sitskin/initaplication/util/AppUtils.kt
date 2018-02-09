package com.example.sitskin.initaplication.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.transition.TransitionInflater
import android.view.View
import android.view.Window
import com.example.hopbaselibrary.android.util.prefs.PrefUtils.Companion.isDarkThemeEnabled
import com.example.sitskin.initaplication.App
import com.example.sitskin.initaplication.R

object AppUtils {

    val versionName: String?
        get() {

            val m = App.context.packageManager

            return try {
                m.getPackageInfo( App.context.packageName, 0 ).versionName
            } catch( e: PackageManager.NameNotFoundException ) {
                e.printStackTrace()
                null
            }

        }

    fun startActivityAnimation( activity: Activity, intent: Intent, sharedElement: View ) {

        val transitionName = sharedElement.transitionName

        ActivityCompat.startActivity(
            activity,
            intent,
            ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity,
                sharedElement,
                transitionName
            ).toBundle()
        )
    }

    fun setupTransitions( window: Window ) {
            val fade = TransitionInflater.from( window.context )
                .inflateTransition( R.transition.window_fade )

            window.exitTransition = fade
            window.enterTransition = fade
    }

    fun setupTheme( context: Context? ) {
        context ?: return

        context.setTheme( if( isDarkThemeEnabled ) R.style.AppTheme_Dark else R.style.AppTheme )
    }

    fun relaunch( context: Context ) {
        val intent = App.context
            .packageManager
            .getLaunchIntentForPackage( App.context.packageName )

        intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP )

        context.startActivity( intent )
        System.exit( 0 )
    }

}

package com.example.sitskin.initaplication.ui.update

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.Theme
import com.example.sitskin.initaplication.R
import com.example.sitskin.initaplication.ui.base.BaseActivity
import timber.log.Timber
import javax.inject.Inject

class UpdateActivity : BaseActivity<Update.Presenter, Update.View>(), Update.View {

    @Inject
    lateinit var updatePresenter: Update.Presenter

    override val presenter: Update.Presenter
        get() = updatePresenter

    override val mvpView: Update.View
        get() = this

    override fun onCreate( savedInstanceState: Bundle? ) {
        super.onCreate( savedInstanceState )
        setTheme( R.style.AppTheme_Launch )
        setContentView( R.layout.fragment_launch )
        component.inject( this )
    }

    override fun onResume() {
        super.onResume()
        presenter.initialize()
    }

    override fun showUpdateRequiredDialog() {
        MaterialDialog.Builder( this )
            .theme( Theme.LIGHT )
            .iconRes( R.mipmap.ic_launcher )
            .title( "Update Required" )
            .content( "There is a required update available. Click update to get the lastest version" )
            .positiveText( "Update" )
            .positiveColorRes( R.color.hop_orange )
            .onPositive { _, _ -> presenter.updateApp() }
            .cancelable( false )
            .show()
    }

    override fun openApplicationUpdate() {
        try {
            startActivity( Intent( Intent.ACTION_VIEW, Uri.parse( "market://details?id=com.hopgrade.android" ) ) )
        } catch( e: ActivityNotFoundException) {
            Timber.e( "Failed to get update." )
        }

    }
    companion object {
        fun showForceUpdate( context: Context): Intent {
            return Intent( context, UpdateActivity::class.java )
        }
    }
}

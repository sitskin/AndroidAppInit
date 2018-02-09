package com.example.sitskin.initaplication.ui.update

import com.example.sitskin.initaplication.api.ConfigHandler
import com.example.sitskin.initaplication.ui.base.BasePresenter
import rx.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class UpdatePresenter @Inject constructor(private val configHandler: ConfigHandler) : BasePresenter<Update.View>(), Update.Presenter {

    override fun initialize() {
        configHandler.isUpdateRequired
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( { required ->
                view?.let { view ->
                    if( required ) {
                        view.showUpdateRequiredDialog()
                    } else {
                        view.showSuccessMessage( "App is up to date" )
                        view.finish()
                    }
                }
            } ) {
                handleError( it, "Failed to check updates" )
                view?.finish()
            }
    }

    override fun updateApp() {
        view?.openApplicationUpdate()
    }
}
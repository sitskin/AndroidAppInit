package com.example.sitskin.initaplication.ui.base

import android.content.Intent
import android.view.View
import com.example.hopapilibrary.model.UserNotificationOutputModel
import com.example.hopbaselibrary.android.ui.base.BaseMvpFragment
import com.example.hopbaselibrary.android.ui.base.Presenter
import com.example.sitskin.initaplication.App
import com.example.sitskin.initaplication.AppComponent
import com.example.sitskin.initaplication.util.AppUtils

abstract class BaseMvpFragment< out P : Presenter< V >, V : MvpView > : BaseMvpFragment< P, V >(), MvpView {
    protected val subscribedNotifications: List< UserNotificationOutputModel.NotificationTypeEnum >
        get() = emptyList()

    override val component: AppComponent
        get() = App.component

    override fun startActivityAnimation(intent: Intent, sharedElement: View) {
        activity?.let { activity -> AppUtils.startActivityAnimation( activity, intent, sharedElement ) }
    }
}
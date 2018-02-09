package com.example.sitskin.initaplication.ui.base

import com.example.hopapilibrary.model.MeUserOutputModel
import com.example.hopbaselibrary.android.ui.base.BasePresenter
import com.example.sitskin.initaplication.App
import com.example.sitskin.initaplication.AppComponent
import com.example.sitskin.initaplication.util.prefs.PrefUtils

abstract class BasePresenter< V : MvpView > : BasePresenter<V>() {

    protected val component: AppComponent
        get() = App.component

    protected val userProfile: MeUserOutputModel?
        get() = PrefUtils.loggedInUserProfile

    override val userId: String?
        get() = PrefUtils.loggedInUserId

}
package com.example.sitskin.initaplication.ui.base

import com.example.hopapilibrary.api.MeApi
import com.example.hopbaselibrary.android.ui.base.BaseActivity
import com.example.hopbaselibrary.android.ui.base.Presenter
import com.example.sitskin.initaplication.App
import com.example.sitskin.initaplication.AppComponent
import javax.inject.Inject

abstract class BaseActivity< out P : Presenter< V >, V : MvpView > : BaseActivity< P, V >(), MvpView {

    @Inject lateinit var meApi: MeApi
    override val component: AppComponent = App.component
}
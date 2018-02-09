package com.example.sitskin.initaplication.ui.base

import com.example.hopbaselibrary.android.ui.base.MvpView
import com.example.sitskin.initaplication.AppComponent

interface MvpView : MvpView {
    val component: AppComponent
}
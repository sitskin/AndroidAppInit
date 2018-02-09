package com.example.sitskin.initaplication.ui.main

import com.example.hopbaselibrary.android.ui.base.MvpPresenter
import com.example.sitskin.initaplication.ui.base.MvpView

interface Main {
    interface View : MvpView {}
    interface Presenter : MvpPresenter< View > {
        fun initialize()
    }
}
package com.example.sitskin.initaplication.ui.update

import com.example.hopbaselibrary.android.ui.base.MvpPresenter
import com.example.sitskin.initaplication.ui.base.MvpView

interface Update {
    interface View : MvpView {
        fun showUpdateRequiredDialog()
        fun openApplicationUpdate()
    }
    interface Presenter : MvpPresenter< View > {
        fun initialize()
        fun updateApp()
    }
}

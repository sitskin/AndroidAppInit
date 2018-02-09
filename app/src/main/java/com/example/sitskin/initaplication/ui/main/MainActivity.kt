package com.example.sitskin.initaplication.ui.main

import android.content.Context
import android.content.Intent
import android.support.annotation.IdRes
import com.example.sitskin.initaplication.AppComponent
import com.example.sitskin.initaplication.R
import com.example.sitskin.initaplication.ui.base.BaseActivity
import javax.inject.Inject

class MainActivity : BaseActivity<Main.Presenter, Main.View>(), Main.View {

    @Inject lateinit var mainPresenter: Main.Presenter

    override val mvpView: Main.View
        get() = this

    override val presenter: Main.Presenter?
        get() = mainPresenter

    enum class Message {
        AccountVerified,
        AccountBanned
    }

    companion object MainIntentFactory {
//        @IdRes
//        val FRAGMENT_CONTENT_ID = R.id.content
        private const val EXTRA_TAB = "tab"
        private const val EXTRA_MESSAGE = "message"
        fun homeIntent( context: Context): Intent {
            return Intent( context, MainActivity::class.java )
//                .putExtra( EXTRA_TAB, R.id.drawer_groups )
        }
        fun messageIntent(context: Context, message: Message ): Intent {
            return Intent( context, MainActivity::class.java )
//                .putExtra( EXTRA_TAB, R.id.drawer_groups )
                .putExtra( EXTRA_MESSAGE, message.name )
        }
    }

}
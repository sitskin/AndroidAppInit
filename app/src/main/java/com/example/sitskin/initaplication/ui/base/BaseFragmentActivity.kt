package com.example.sitskin.initaplication.ui.base

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.widget.Toolbar
import com.example.hopbaselibrary.android.ui.base.BaseFragmentActivity
import com.example.sitskin.initaplication.R
import com.example.sitskin.initaplication.util.AppUtils
import com.example.sitskin.initaplication.util.AppUtils.setupTheme

abstract class BaseFragmentActivity : BaseFragmentActivity() {

    override fun onCreate( savedInstanceState: Bundle? ) {
        setupTheme( this )
        super.onCreate( savedInstanceState )
    }

    override fun setupActionBar() {
        val toolbar = findViewById< Toolbar >( R.id.toolbar ) ?: return

        setSupportActionBar( toolbar )

        supportActionBar?.setDisplayHomeAsUpEnabled( true )
    }

    override fun setupFlatActionBar() {
        val toolbar = findViewById< Toolbar >( R.id.toolbar )

        setSupportActionBar( toolbar )

        supportActionBar?.setDisplayHomeAsUpEnabled( true )
        supportActionBar?.setDisplayShowTitleEnabled( false )
    }

    override fun setupTransitions() {
        AppUtils.setupTransitions( window )
    }
}
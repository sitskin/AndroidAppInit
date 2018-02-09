package com.example.sitskin.initaplication

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import com.example.hopbaselibrary.android.HopBase
import dagger.internal.DaggerCollections

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        context = this

        MultiDex.install( this )

        HopBase.initialize( this )

        initializeDependencyInjector()
    }

    private fun initializeDependencyInjector() {
        component = DaggerAppComponent.builder()
            .appModule( AppModule( this ) )
            .build()
        component.inject( this )
    }

    companion object {
        lateinit var context: Context
        lateinit var component: AppComponent
    }
}
package com.example.sitskin.initaplication

import com.example.sitskin.initaplication.api.ApiModule
import com.example.sitskin.initaplication.api.BaseInterceptor
import com.example.sitskin.initaplication.gcm.HopGcmListenerService
import com.example.sitskin.initaplication.ui.main.MainActivity
import com.example.sitskin.initaplication.ui.main.MainModule
import com.example.sitskin.initaplication.ui.update.UpdateActivity
import com.example.sitskin.initaplication.ui.update.UpdateModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component( modules = arrayOf( AppModule::class, MainModule::class, ApiModule::class,
    UpdateModule::class ) )
interface AppComponent {
    fun inject( application: App )
    fun inject( mainActivity: MainActivity )
    fun inject( baseInterceptor: BaseInterceptor )
    fun inject( hopGcmListenerService: HopGcmListenerService )
    fun inject( updateActivity: UpdateActivity )
}
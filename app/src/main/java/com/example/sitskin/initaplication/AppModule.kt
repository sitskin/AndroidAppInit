package com.example.sitskin.initaplication

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import java.io.File
import javax.inject.Singleton

@Module
class AppModule( private val application: Application ) {

    @Provides
    @Singleton
    fun application(): Application {
        return application
    }

    @Provides
    @Singleton
    fun provideContext(): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun provideClientCache( application: Application ): Cache {
        val cacheDir = File( application.cacheDir, CACHE_DIR )
        val cacheSize = ( 24 * 1024 * 1024 ).toLong() // 24 MB

        return Cache( cacheDir, cacheSize )
    }

    companion object {
        const val CACHE_DIR = "initial-app-cache"
    }
}
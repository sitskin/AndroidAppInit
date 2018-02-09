package com.example.sitskin.initaplication.api

import com.example.hopapilibrary.api.ConfigurationApi
import com.example.hopapilibrary.model.ConfigurationOutputModel
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles retrieving the config settings for the current app install
 */
@Singleton
class ConfigHandler @Inject constructor(private val configurationApi: ConfigurationApi) {

    private var config: Observable<ConfigurationOutputModel>

    /**
     * Retrieves whether or not an update is required for the app to function properly
     *
     * @return true if update is required, false otherwise
     */
    val isUpdateRequired: Observable<Boolean>
        get() = config.map { config -> config.forceUpdate }
            .onErrorResumeNext( Observable.just( false ) )

    /**
     * Retrieves whether or not the app should proxy all outgoing requests through Runscope for
     * debugging purposes.
     *
     * @return true if requests should be proxied, false otherwise
     */
    val isNetworkDebugEnabled: Observable<Boolean>
        get() = config.map { config -> config.enableNetworkDebug }
            .onErrorResumeNext( Observable.just( true ) )

    /**
     * Retrieves whether or not the app should disable the Start Group button
     *
     * @return true if requests should be disabled, false otherwise
     */
    val isCreateGroupEnabled: Observable<Boolean>
        get() = config.map { config -> ! config.disableStartGroup }
            .onErrorResumeNext( Observable.just( false ) )

    init {
        this.config = configurationApi.configurationGet().cache()
    }

    fun refreshConfig() {
        config = configurationApi.configurationGet().cache()
    }

}

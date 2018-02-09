package com.example.sitskin.initaplication.api

import com.example.hopapilibrary.api.OAuthApi
import com.example.hopapilibrary.model.Oauth2TokenOutputModel
import com.example.sitskin.initaplication.util.prefs.PrefUtils
import rx.Observable
import rx.schedulers.Schedulers
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named

class AuthenticationHandler @Inject constructor(private val oAuthApi: OAuthApi, @param:Named( "client_id" ) private val clientId: String ) {

    private var refreshToken: String? = null
    private var accessToken: String? = null
    private var tokenExpiration: Long? = null

    /**
     * Returns the locally cached user refresh token.
     *
     * @return refresh token string if available, null otherwise
     */
    private val cachedRefreshToken: String?
        get() {
            refreshToken = if( refreshToken?.isEmpty() == false ) refreshToken else PrefUtils.refreshToken
            return refreshToken
        }

    /**
     * Returns the locally cached user hop access token.
     *
     * @return hop access token string if available, null otherwise
     */
    val cachedAccessToken: String?
        get() {
            accessToken = if( accessToken?.isEmpty() == false ) accessToken else PrefUtils.bearerToken
            return accessToken
        }

    val cachedTokenExpiration: Long
        get() {
            tokenExpiration = tokenExpiration ?: PrefUtils.tokenExpiration
            return tokenExpiration ?: PrefUtils.tokenExpiration
        }

    private val googleAccessToken: String?
        get() = PrefUtils.googleAccessToken

    /**
     * Attempts to create a new user given the supplied information.
     *
     * @param username the user's contact information, either email address or phone number
     * @param pin the verification code sent to the user
     * @return true if account created, false otherwise
     */
    fun register( username: String, pin: String ): Observable<Boolean> {
        return usePasswordGrant( username, pin )
    }

    /**
     * Attempts to authenticate the user using a refresh token or access token
     *
     * @return true if authentication is successful, false otherwise
     */
    fun authenticate(): Observable<Boolean> {

        Timber.i( "Authenticating" )

        // REFRESH TOKEN

        if( cachedRefreshToken?.isEmpty() == false ) {
            return useRefreshTokenGrant()
        }

        // GOOGLE TOKEN

        val googleToken = googleAccessToken

        return if( googleToken?.isEmpty() == false ) {
            usePasswordGrant( USER_GOOGLE_TOKEN, googleToken )
        } else Observable.just( false )

        // NO AUTH FOR YOU

    }

    /**
     * Returns a fresh access token, using the cached user refresh token.
     *
     * @return access token string, null if refresh token is not available
     */
    fun getAccessToken(): Observable<String> {
        refreshToken = cachedRefreshToken

        return if( refreshToken.isNullOrEmpty() ) Observable.just( null ) else oAuthApi.postRefreshToken( GRANT_SCOPE, GRANT_REFRESH_TOKEN, clientId, refreshToken )
            .subscribeOn( Schedulers.io() )
            .map { tokenResponse ->
                updateTokens( tokenResponse )
                tokenResponse.accessToken
            }

    }

    /**
     * Authenticate using refresh token
     *
     * @return true if authentication is successful, false otherwise
     */
    private fun useRefreshTokenGrant(): Observable<Boolean> {

        return if( refreshToken.isNullOrEmpty() ) Observable.just( false ) else oAuthApi.postRefreshToken( GRANT_SCOPE, GRANT_REFRESH_TOKEN, clientId, refreshToken )
            .map { tokenResponse ->
                updateTokens( tokenResponse )
                tokenResponse != null
            }

    }

    /**
     * Authenticate using access token
     *
     * @param username the username to authenticate as, currently only [.USER_FACEBOOK_TOKEN] is valid
     * @param password the 3rd party access token to exchange for a hop access token
     * @return true if authentication is successful, false otherwise
     */
    private fun usePasswordGrant( username: String, password: String ): Observable<Boolean> {

        return if( username.isEmpty() || password.isEmpty() ) Observable.just( false ) else oAuthApi.postAccessToken( GRANT_SCOPE, clientId, GRANT_PASSWORD, username, password )
            .map { tokenResponse ->
                updateTokens( tokenResponse )
                tokenResponse != null
            }

    }

    /**
     * Cache the refresh and access tokens in memory and in shared preferences
     *
     * @param tokens the response containing the refresh and access tokens
     */
    private fun updateTokens( tokens: Oauth2TokenOutputModel? ) {

        val now = Calendar.getInstance().timeInMillis

        refreshToken = tokens?.refreshToken
        accessToken = tokens?.accessToken
        tokenExpiration = tokens?.expiresIn?.let { TimeUnit.SECONDS.toMillis( it.toLong() ) + now }

        PrefUtils.refreshToken = refreshToken
        PrefUtils.bearerToken = accessToken
        PrefUtils.setTokenExpiration( tokenExpiration )
    }
    companion object {
        private const val GRANT_REFRESH_TOKEN = "refresh_token"
        private const val GRANT_PASSWORD = "password"
        private const val GRANT_SCOPE = "app"
        private const val USER_FACEBOOK_TOKEN = "FacebookAccessToken"
        private const val USER_GOOGLE_TOKEN = "GoogleAccessToken"
    }
}
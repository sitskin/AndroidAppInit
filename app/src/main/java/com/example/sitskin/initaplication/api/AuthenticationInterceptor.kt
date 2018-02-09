package com.example.sitskin.initaplication.api

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import timber.log.Timber
import java.io.IOException
import java.net.HttpURLConnection
import java.util.*
import javax.inject.Inject

class AuthenticationInterceptor @Inject constructor(private val authenticationHandler: AuthenticationHandler ) : BaseInterceptor(), Interceptor {

    private val isTokenExpired: Boolean
        get() = authenticationHandler.cachedTokenExpiration < Calendar.getInstance().timeInMillis

    @Throws( IOException::class )
    override fun intercept( chain: Interceptor.Chain ): Response {

        var request = chain.request()

        Timber.v( "Requesting ${ request.url() }" )

        val bearerToken = authenticationHandler.cachedAccessToken

        val builder = request.newBuilder()

        addUserAgentHeader( builder )
        addOdeHeaders( builder )

        if( isTokenExpired ) {
            return refreshAuthAndRetry( chain, builder.build() )
        }

        if( bearerToken?.isEmpty() == false ) {
            builder.header( "Authorization", "Bearer " + bearerToken )
        }

        request = builder.build()

        val response = chain.proceed( request )

        if( bearerToken.isNullOrEmpty() ) return response

        return if( isUnauthorized( response.code() ) ) refreshAuthAndRetry( chain, request ) else response
    }

    @Throws( IOException::class )
    private fun refreshAuthAndRetry(chain: Interceptor.Chain, request: Request): Response {
        val freshToken = authenticationHandler.getAccessToken().onErrorReturn { null }.toBlocking().first()

        val newRequest = request.newBuilder()
            .header( "Authorization", "Bearer " + freshToken )
            .build()

        return chain.proceed( newRequest )
    }

    private fun isUnauthorized( code: Int ): Boolean = code == HttpURLConnection.HTTP_UNAUTHORIZED
}

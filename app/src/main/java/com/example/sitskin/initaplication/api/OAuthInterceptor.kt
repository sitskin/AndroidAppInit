package com.example.sitskin.initaplication.api

import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class OAuthInterceptor @Inject constructor() : BaseInterceptor() {

    @Throws( IOException::class )
    override fun intercept( chain: Interceptor.Chain ): Response {

        var request = chain.request()

        Timber.v( "Requesting ${ request.url() }" )

        val builder = request.newBuilder()

        addUserAgentHeader( builder )
        addOdeHeaders( builder )

        request = builder.build()

        return chain.proceed( request )
    }
}


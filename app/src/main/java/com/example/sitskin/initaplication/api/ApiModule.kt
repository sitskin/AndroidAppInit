package com.example.sitskin.initaplication.api

import android.content.Context
import com.example.hopapilibrary.api.*
import com.example.hopapilibrary.model.ItemOutputModel
import com.example.hopapilibrary.model.ItemOutputTypeAdapter
import com.example.sitskin.initaplication.R
import com.example.sitskin.initaplication.util.AppUtils
import com.example.sitskin.initaplication.util.prefs.PrefUtils.Companion.isRunscopeEnabled
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Singleton
@Module
class ApiModule {

    @Provides
    @Singleton
    @Named( "default" )
    fun provideDefaultBaseUrl( context: Context): HttpUrl {

        val endpoint = if( isRunscopeEnabled )
            context.getString( R.string.debug_api_endpoint )
        else
            context.getString( R.string.api_endpoint )

        Timber.i( "Default Endpoint: $endpoint" )

        return HttpUrl.parse( endpoint )
    }

    @Provides
    @Singleton
    @Named( "logged" )
    fun provideAuthBaseUrl( context: Context): HttpUrl {

        val endpoint = if( isRunscopeEnabled )
            context.getString(R.string.debug_api_endpoint)
        else
            context.getString(R.string.api_endpoint)

        Timber.i( "Auth Endpoint: $endpoint" )

        return HttpUrl.parse( endpoint )
    }

    @Provides
    @Singleton
    @Named( "image" )
    fun provideImageBaseUrl( context: Context): HttpUrl {

        val endpoint = context.getString(R.string.debug_api_endpoint)

        Timber.i( "Image Endpoint: $endpoint" )

        return HttpUrl.parse( endpoint )
    }

    @Provides
    @Singleton
    @Named( "geolocation" )
    fun provideGeolocationBaseUrl( context: Context): HttpUrl {

        val endpoint = context.getString(R.string.api_endpoint)

        Timber.i( "Geolocation Endpoint: $endpoint" )

        return HttpUrl.parse( endpoint )
    }

    @Provides
    @Singleton
    fun provideDeviceRegistrationHandler( deviceApi: DeviceApi): DeviceRegistrationHandler {
        return DeviceRegistrationHandler( deviceApi )
    }

    @Provides
    @Singleton
    fun provideAuthenticationHandler(context: Context, oAuthApi: OAuthApi): AuthenticationHandler {
        return AuthenticationHandler( oAuthApi, context.getString(R.string.oauth_client_id) )
    }

    @Provides
    @Singleton
    fun provideAuthenticationInterceptor( authenticationHandler: AuthenticationHandler ): AuthenticationInterceptor {
        return AuthenticationInterceptor( authenticationHandler )
    }

    @Provides
    @Singleton
    fun provideOkHttpClient( interceptor: AuthenticationInterceptor, cache: Cache): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor( interceptor )
            .cache( cache )
            .readTimeout( 30, TimeUnit.SECONDS )
            .build()
    }

    @Provides
    @Singleton
    fun provideConverter(): Converter.Factory {
        val gson = GsonBuilder()
            .registerTypeAdapter( ItemOutputModel::class.java, ItemOutputTypeAdapter.ItemOutputSerializer())
            .registerTypeAdapter( ItemOutputModel::class.java, ItemOutputTypeAdapter.ItemOutputDeserializer())
            .create()

        return GsonConverterFactory.create( gson )
    }

    @Provides
    @Singleton
    fun provideRetrofit(@Named( "default" ) baseUrl: HttpUrl, client: OkHttpClient, converterFactory: Converter.Factory ): Retrofit {
        return Retrofit.Builder()
            .baseUrl( baseUrl )
            .client( client )
            .addConverterFactory( converterFactory )
            .addCallAdapterFactory( RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()) )
            .build()
    }

    @Provides
    @Singleton
    @Named( "image" )
    fun providesImageUploadRetrofit(@Named( "image" ) baseUrl: HttpUrl, client: OkHttpClient, converterFactory: Converter.Factory ): Retrofit {
        return Retrofit.Builder()
            .baseUrl( baseUrl )
            .client( client )
            .addConverterFactory( converterFactory )
            .addCallAdapterFactory( RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()) )
            .build()
    }

    @Provides
    @Singleton
    @Named( "debug" )
    fun providesDebugRetrofit(@Named( "logged" ) baseUrl: HttpUrl, client: OkHttpClient, converterFactory: Converter.Factory ): Retrofit {
        return Retrofit.Builder()
            .baseUrl( baseUrl )
            .client( client )
            .addConverterFactory( converterFactory )
            .addCallAdapterFactory( RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()) )
            .build()
    }

    @Provides
    @Singleton
    @Named( "geolocation" )
    fun providesGeolocationRetrofit(context: Context, @Named( "geolocation" ) baseUrl: HttpUrl): Retrofit {

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val userAgent = context.getString(R.string.user_agent, AppUtils.versionName)

                var request = chain.request()

                request = request.newBuilder()
                    .header( "User-Agent", userAgent )
                    .build()

                chain.proceed( request )
            }
            .readTimeout( 30, TimeUnit.SECONDS )
            .build()

        return Retrofit.Builder()
            .baseUrl( baseUrl )
            .addConverterFactory( GsonConverterFactory.create() )
            .addCallAdapterFactory( RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()) )
            .client( okHttpClient )
            .build()
    }

    @Provides
    @Singleton
    @Named( "anonymous" )
    fun provideAnonymousRetrofit(context: Context, @Named( "logged" ) baseUrl: HttpUrl): Retrofit {

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val userAgent = context.getString(R.string.user_agent, AppUtils.versionName)

                var request = chain.request()

                request = request.newBuilder()
                    .header( "User-Agent", userAgent )
                    .build()

                chain.proceed( request )
            }
            .readTimeout( 30, TimeUnit.SECONDS )
            .build()

        return Retrofit.Builder()
            .baseUrl( baseUrl )
            .addConverterFactory( GsonConverterFactory.create() )
            .addCallAdapterFactory( RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()) )
            .client( okHttpClient )
            .build()
    }

    @Provides
    @Singleton
    fun provideDeviceApi( @Named( "anonymous" ) retrofit: Retrofit): DeviceApi {
        return retrofit.create( DeviceApi::class.java )
    }

    @Provides
    @Singleton
    fun provideVerifyApi( @Named( "anonymous" ) retrofit: Retrofit): VerifyApi {
        return retrofit.create( VerifyApi::class.java )
    }

    @Provides
    @Singleton
    fun provideOAuthApi( @Named( "logged" ) baseUrl: HttpUrl): OAuthApi {

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor( OAuthInterceptor() )
            .readTimeout( 30, TimeUnit.SECONDS )
            .build()

        return Retrofit.Builder()
            .baseUrl( baseUrl )
            .addConverterFactory( GsonConverterFactory.create() )
            .addCallAdapterFactory( RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()) )
            .client( okHttpClient )
            .build()
            .create( OAuthApi::class.java )
    }

    @Provides
    @Singleton
    fun provideRegisterApi( @Named( "logged" ) baseUrl: HttpUrl): RegisterApi {

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor( OAuthInterceptor() )
            .readTimeout( 30, TimeUnit.SECONDS )
            .build()

        return Retrofit.Builder()
            .baseUrl( baseUrl )
            .addConverterFactory( GsonConverterFactory.create() )
            .addCallAdapterFactory( RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()) )
            .client( okHttpClient )
            .build()
            .create( RegisterApi::class.java )
    }

    @Provides
    @Singleton
    fun provideAppInstallApi( @Named( "debug" ) retrofit: Retrofit): AppInstallApi {
        return retrofit.create( AppInstallApi::class.java )
    }

    @Provides
    @Singleton
    fun provideConfigurationApi( @Named( "anonymous" ) retrofit: Retrofit): ConfigurationApi {
        return retrofit.create( ConfigurationApi::class.java )
    }

    @Provides
    @Singleton
    fun provideGeoLocationApi( @Named( "geolocation" ) retrofit: Retrofit): GeoLocationApi {
        return retrofit.create( GeoLocationApi::class.java )
    }

    @Provides
    @Singleton
    fun provideImagesApi( @Named( "image" ) retrofit: Retrofit): ImagesApi {
        return retrofit.create( ImagesApi::class.java )
    }

    @Provides
    @Singleton
    fun provideItemApi( retrofit: Retrofit): ItemsApi {
        return retrofit.create( ItemsApi::class.java )
    }

    @Provides
    @Singleton
    fun provideConversationsApi( retrofit: Retrofit): ConversationsApi {
        return retrofit.create( ConversationsApi::class.java )
    }

    @Provides
    @Singleton
    fun provideGroupsApi( retrofit: Retrofit): GroupsApi {
        return retrofit.create( GroupsApi::class.java )
    }

    @Provides
    @Singleton
    fun provideMeApi( retrofit: Retrofit): MeApi {
        return retrofit.create( MeApi::class.java )
    }

    @Provides
    @Singleton
    fun provideSearchApi( retrofit: Retrofit): SearchApi {
        return retrofit.create( SearchApi::class.java )
    }

    @Provides
    @Singleton
    fun provideUsersApi( retrofit: Retrofit): UsersApi {
        return retrofit.create( UsersApi::class.java )
    }

}

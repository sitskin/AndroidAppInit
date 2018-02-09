package com.example.sitskin.initaplication.util

import android.content.Context
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.google.gson.Gson
import com.example.hopapilibrary.model.ImageOutputModel
import com.example.sitskin.initaplication.R
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.adapter.rxjava.HttpException
import java.io.File
import java.io.IOException

object NetworkUtils {

    const val HTTP_UNKNOWN = - 1

    // 400 Client Error
    const val HTTP_BAD_REQUEST = 400
    const val HTTP_UNAUTHORIZED = 401
    const val HTTP_PAYMENT_REQUIRED = 402
    const val HTTP_FORBIDDEN = 403
    const val HTTP_NOT_FOUND = 404
    const val HTTP_CONFLICT = 409
    const val HTTP_GONE = 410
    const val HTTP_TOO_MANY_REQUESTS = 429

    // 500 Server Error
    const val HTTP_INTERNAL_ERROR = 500
    const val HTTP_BAD_GATEWAY = 502

    fun getHttpErrorCode( e: Throwable ): Int {
        return ( e as? HttpException )?.code() ?: 0
    }

    fun getErrorBody( e: Throwable ): ErrorBody? {
        return if( e is HttpException ) {
            parseErrorBody( e.response() )
        } else {
            null
        }
    }

    fun parseErrorBody( response: Response< * > ): ErrorBody {
        val responseBody = response.errorBody()

        return try {
            val error = Gson().fromJson( responseBody.string(), ErrorBody::class.java )
            responseBody.close()
            error
        } catch( e: IOException ) {
            e.printStackTrace()
            ErrorBody()
        } catch( e: NullPointerException ) {
            e.printStackTrace()
            ErrorBody()
        }

    }

    class ErrorBody {
        var message: String? = null
            internal set
    }

    fun prepareFileUpload( file: File ): MultipartBody.Part {
        val body = RequestBody.create( MediaType.parse( "multipart/form-data" ), file )
        return MultipartBody.Part.createFormData( "file", file.name, body )
    }

    fun getThumbnailUrl( context: Context, image: ImageOutputModel): String {
        return context.getString(R.string.thumbnail_cdn_url, image.id)
    }

    fun getThumbnail( context: Context, image: ImageOutputModel): RequestBuilder< Drawable > {
        return Glide.with( context ).load( getThumbnailUrl( context, image ) )
    }
}

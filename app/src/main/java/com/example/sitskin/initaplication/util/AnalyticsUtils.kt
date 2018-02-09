package com.example.sitskin.initaplication.util

import android.content.Context

import com.example.hopapilibrary.model.MeUserOutputModel
import com.example.sitskin.initaplication.R
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.segment.analytics.Analytics
import com.segment.analytics.Traits

object AnalyticsUtils {

    fun identifyUser( context: Context, user: MeUserOutputModel? ) {

        user ?: return

        val userTraits = Traits()

        user.firstName?.let { userTraits.putFirstName( user.firstName ) }
        user.lastName?.let{ userTraits.putLastName( user.lastName ) }
        user.emailAddress?.let{ userTraits.putEmail( user.emailAddress ) }

        Analytics.with( context ).identify( user.id, userTraits, null )
        MixpanelAPI.getInstance( context, context.getString(R.string.mixpanel_token) ).identify( user.id )

    }
}

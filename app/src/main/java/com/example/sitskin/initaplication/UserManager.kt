package com.example.sitskin.initaplication

import android.app.Activity
import android.support.v4.app.Fragment
import com.example.hopapilibrary.api.MeApi
import com.example.hopapilibrary.api.UsersApi
import com.example.hopapilibrary.model.MeUserOutputModel
import com.example.sitskin.initaplication.util.prefs.PrefUtils
import com.example.sitskin.initaplication.util.prefs.PrefUtils.Companion.loggedInUserId
import com.example.sitskin.initaplication.util.prefs.PrefUtils.Companion.loggedInUserProfile
import com.example.sitskin.initaplication.util.prefs.PrefUtils.Companion.setLoggedInUser
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserManager @Inject constructor(private val meApi: MeApi, private val usersApi: UsersApi) {

    /**
     * Returns whether the current user is logged in.
     *
     * @return true if logged in, false otherwise
     */
    val isUserLoggedIn: Boolean
        get() = ! loggedInUserId.isNullOrEmpty()

    val isProfileComplete: Boolean
        get() {

            val profile = loggedInUserProfile

            val isNameValid = ! profile?.firstName.isNullOrEmpty() && ! profile?.lastName.isNullOrEmpty()
            val isImageValid = profile?.image?.let {  image -> ! image.url.isEmpty() && ! image.url.contains( "default" ) } ?: false

            return isNameValid && isImageValid
        }

    /**
     * Returns whether the current user is a member of any groups.
     *
     * @return true if a member of any groups, false otherwise
     */
    val isUserInGroups: Boolean
        get() = isUserLoggedIn && loggedInUserProfile?.groups?.isEmpty() == false

    /**
     * Returns the current user's id, if logged in.
     *
     * @return the user's id if logged in, null otherwise
     */
    val userId: String?
        get() = loggedInUserId

    val userEmail: String?
        get() = loggedInUserProfile?.emailAddress

    /**
     * Returns the current user's profile, if logged in.
     *
     * @return the user's profile if logged in, empty Observable otherwise.
     */
    val userProfile: Observable<MeUserOutputModel>
        get() {
            val cachedUser = Observable.just<MeUserOutputModel>( loggedInUserProfile )
            val updatedUser = meApi.meGet()
                .doOnNext { me -> PrefUtils.setLoggedInUser( me ) }

            return Observable.concat( cachedUser, updatedUser )
                .filter { me -> me != null }
        }

    fun updateUserProfile(): Observable<MeUserOutputModel> {
        return meApi.meGet().doOnNext { me -> setLoggedInUser( me ) }
    }

//    /**
//     * Shows the user a prompt to log in before continuing
//     *
//     * @param activity
//     */
//    fun showLoginActivity(activity: Activity, requestCode: Int ) {
//
//        activity.startActivityForResult( signupIntent( activity ), requestCode )
//
//        activity.overridePendingTransition( R.anim.slide_in_up, R.anim.do_nothing )
//    }
//
//    /**
//     * Shows the user a prompt to log in before continuing
//     *
//     * @param fragment
//     */
//    fun showLoginActivity(fragment: Fragment, requestCode: Int) {
//
//        fragment.context?.let { fragment.startActivityForResult( signupIntent( it ), requestCode ) }
//
//        fragment.activity?.overridePendingTransition( R.anim.slide_in_up, R.anim.do_nothing )
//    }

    class UserUnauthorizedException : Exception()

    companion object {
        val REQUEST_LOGIN = 40100

        /**
         * Returns the conversation id for a user to contact Hop Support.
         */
        val supportConversationId: String
            get() = ""
    }
}

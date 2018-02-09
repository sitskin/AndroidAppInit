package com.example.sitskin.initaplication.gcm

import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.app.TaskStackBuilder
import com.example.hopapilibrary.api.ConversationsApi
import com.example.hopapilibrary.api.GroupsApi
import com.example.hopapilibrary.model.ConversationOutputModel
import com.example.hopapilibrary.model.GroupOutputModel
import com.example.sitskin.initaplication.App
import com.example.sitskin.initaplication.BuildConfig
import com.example.sitskin.initaplication.R
import com.example.sitskin.initaplication.UserManager
import com.example.sitskin.initaplication.api.ConfigHandler
import com.example.sitskin.initaplication.ui.main.MainActivity
import com.example.sitskin.initaplication.ui.update.UpdateActivity.Companion.showForceUpdate
import com.example.sitskin.initaplication.util.NotificationUtils.getThumbnail
import com.example.sitskin.initaplication.util.ShareUtils
import com.example.sitskin.initaplication.util.ShareUtils.getSharedItemId
import com.example.sitskin.initaplication.util.ShareUtils.getSharedItemKind
import com.example.sitskin.initaplication.util.prefs.PrefUtils.Companion.loggedInUserProfile
import com.google.android.gms.gcm.GcmListenerService
import com.google.gson.Gson
import org.json.JSONObject
import rx.schedulers.Schedulers
import timber.log.Timber
import java.util.*
import javax.inject.Inject

/**
 * Service which listens for messages pushed from Google Cloud Messaging.
 */
class HopGcmListenerService : GcmListenerService() {

    @Inject
    lateinit var configHandler: ConfigHandler
    @Inject
    lateinit var groupsApi: GroupsApi
    @Inject
    lateinit var conversationsApi: ConversationsApi
    @Inject
    lateinit var userManager: UserManager

    private val accentColor: Int
        get() = when( BuildConfig.BUILD_TYPE ) {
            "debug" -> - 0x8a8a8b
            "beta" -> - 0xd16f25
            "release" -> - 0xba6e9
            else -> - 0xba6e9
        }

    enum class NotificationType {
        ListingPublicCommentCreated,
        ListingPublicImageCreated,
        ListingRequestPublicCommentCreated,
        ListingRequestPublicImageCreated,
        ListingRequestMessageCreated,
        ListingRequestMessageLocationCreated,
        ListingRequestMessageImageCreated,
        ListingRequestMessageOfferCreated,
        ListingRequestMessageOfferDeclined,
        ListingRequestMessageOfferCancelled,
        ListingRequestMessageOfferPriceUpdated,
        ListingExpired,
        ListingRequestFulfilled,
        ListingCreatedInMarket,
        ListingRequestCreatedInMarket,
        MarketingMessage,
        ConversationMessageListingRequest,
        ConversationMessageListing,
        ConversationMessageTextCreated,
        ConversationMessageLocationCreated,
        ConversationMessageImageCreated,
        ConversationMessageOfferCreated,
        ConversationMessageOfferAccepted,
        ConversationMessageOfferCancelled,
        ConversationMessageOfferDeclined,
        ConversationMessageListingRequestCanceled,
        ConversationMessageListingCanceled,
        ConversationMessageMatchedListing,
        ConversationMessagePaid,
        GroupMessageAdminBroadcast,
        Unknown
    }

    init {
        App.component.inject( this )
    }

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param bundle Data bundle containing message data as key/value pairs.
     * For Set of keys use data.keySet().
     */

    override fun onMessageReceived( from: String?, bundle: Bundle) {

        val data = NotificationData( bundle )

        configHandler.isUpdateRequired
            .observeOn(Schedulers.io())
            .subscribe( { required ->
                if( required ) {
                    handleRequiredUpdate( data )
                } else {
                    handleNotificationEvent( data )
                }
            } ) { Timber.e( it, "Failed to check if update is required." ) }
    }

    private fun handleRequiredUpdate( data: NotificationData ) {

        Timber.w( "Required update detected via Push Notification" )

        Timber.i( "Event: ${ data.type.name } \nListing ID: ${ data.listingId } \nData: ${ Gson().toJson( data ) }" )

        val intent = showForceUpdate( this ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val thumbnail = getThumbnail( this, data.thumbnail ?: "" )

        intent.putExtra( "notificationId", data.notificationId )

        sendNotification( 0, data.title, data.message, thumbnail, intent, Collections.emptyList())
    }

    private fun handleNotificationEvent( data: NotificationData ) {

        Timber.i( "Event: ${ data.type.name } \nListing ID: ${ data.listingId } \nData: ${ Gson().toJson( data ) }" )

        val intent: Intent

        val actions = listOf< NotificationAction >()

        val thumbnail = getThumbnail( this, data.thumbnail ?: "" )

        val notificationId = getId( getSharedItemId( data.uri ) + "-" + data.type.name )

        when( ShareUtils.getShareUrlType( data.uri ) ) {
//            ShareUtils.ShareUrlType.Item -> intent = ItemActivity.itemDetail( this, getSharedItemKind( data.uri ), getSharedItemId( data.uri ) ?: "" )
//            ShareUtils.ShareUrlType.Group -> {
//                val groupId = getSharedItemId( data.uri )
//                val group = getGroup( groupId )
//
//                intent = group?.let {
//                    if( isUserInGroup( groupId ) ) {
//                        GroupsActivity.viewGroupPosts( this, it )
//                    } else {
//                        GroupsActivity.viewGroupDetail( this, it )
//                    }
//                } ?: MainActivity.homeIntent( this )
//            }
//            ShareUtils.ShareUrlType.Conversation -> {
//                val conversationId = getSharedItemId( data.uri )
//                val recipient = ( data.payload?.get( "params" ) as? JSONObject)?.get( "senderId" ) as? String
//
//                // done to update "me" so that the badges appear correctly
//                userManager.userProfile
//
//                intent = recipient?.let {
//                    ConversationActivity.conversationMessagesIntent( this, conversationId, recipient )
//                } ?: MainActivity.homeIntent( this )
//            }
            ShareUtils.ShareUrlType.Unknown -> intent = MainActivity.homeIntent( this )
            else -> intent = MainActivity.homeIntent( this )
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra( "notificationId", data.notificationId )

        sendNotification( notificationId, data.title, data.message, thumbnail, intent, actions )
    }

    /**
     * Create and a simple notification containing a title, a content message and a list of actions.
     *
     * @param title the title of the notification
     * @param message the content of the notification
     * @param viewIntent the intent which is triggered when a user clicks the body of the notification
     * @param actions an optional list of actions that shows at the bottom of a notification
     */
    private fun sendNotification(notificationId: Int, title: String?, message: String?, thumbnail: Bitmap?, viewIntent: Intent, actions: List< NotificationAction > ) {

        val viewPendingIntent = TaskStackBuilder.create( this )
            .addNextIntentWithParentStack( viewIntent )
            .getPendingIntent( notificationId, PendingIntent.FLAG_ONE_SHOT )

        val defaultSoundUri = RingtoneManager.getDefaultUri( RingtoneManager.TYPE_NOTIFICATION )

        // Build the notification
        val notificationBuilder = NotificationCompat.Builder( this, "hop_notifications" )
            .setSmallIcon( R.drawable.hop_icon_white )
            .setColor( accentColor )
            .setContentTitle( title ?: "hOp" )
            .setContentText( message )
            .setContentIntent( viewPendingIntent )
            .setAutoCancel( true )
            .setPriority( NotificationCompat.PRIORITY_HIGH )
            .setStyle( NotificationCompat.BigTextStyle().bigText( message ) )
            .setSound( defaultSoundUri )

        for( action in actions ) {
            notificationBuilder.addAction( action.icon, action.title, action.intent )
        }

        thumbnail?.let { notificationBuilder.setLargeIcon( it ) }

        val notificationManager = NotificationManagerCompat.from( this )

        notificationManager.notify( notificationId, notificationBuilder.build() )

    }

    private fun getGroup( groupId: String? ): GroupOutputModel? {
        return try {
            groupsApi.groupsGet( groupId ).toBlocking().first()
        } catch( e: Exception ) {
            null
        }
    }

    private fun getConversation( conversationId: String? ): ConversationOutputModel? {
        return try {
            conversationsApi.conversationsGet( conversationId, null, 1, null ).toBlocking().first()
        } catch( e: Exception ) {
            null
        }
    }

    private fun isUserInGroup( groupId: String? ): Boolean {

        if( ! userManager.isUserLoggedIn ) return false

        return loggedInUserProfile?.groups?.any{ it.groupId == groupId } ?: false
    }

    private fun getId( notificationId: String ): Int {
        return notificationId.hashCode()
    }

    private inner class NotificationAction(@param:DrawableRes var icon: Int, var title: String, var intent: PendingIntent)

    private inner class NotificationData internal constructor( bundle: Bundle) {

        internal val type: NotificationType
        internal val uri: Uri
        internal val notificationId: String?
        internal val itemKind: String?
        internal val itemId: String?
        internal val listingId: String?
        internal val requestId: String?
        internal val conversationId: String?
        internal val transactionId: String?
        internal val requestConversationId: String?
        internal val requestMessageId: String?
        internal val groupId: String?
        internal val message: String?
        internal val title: String?
        internal val thumbnail: String?
        internal val payload: JSONObject?

        init {
            payload = parsePayload( bundle.getString( "payload" ) )
            type = parseNotificationEventType( bundle.getString( "notificationType" ) )
            uri = parseUri( bundle.getString( "contentUri" ) )
            notificationId = bundle.getString( "notificationId" )
            itemKind = bundle.getString( "kind" )
            itemId = bundle.getString( "itemId" )
            listingId = bundle.getString( "listingId" )
            requestId = bundle.getString( "listingRequestId" )
            conversationId = bundle.getString( "conversationId" )
            requestConversationId = bundle.getString( "listingRequestConversationId" )
            requestMessageId = bundle.getString( "listingRequestMessageId" )
            groupId = bundle.getString( "groupId" )
            transactionId = bundle.getString( "listingTransactionId" )
            message = bundle.getString( "message" )
            title = bundle.getString( "title" )
            thumbnail = bundle.getString( "thumbnailUrl" )
        }

        private fun parseNotificationEventType( event: String? ): NotificationType {

            if( event?.isEmpty() != false ) {
                Timber.e( "Push Notification event was empty" )
                return NotificationType.Unknown
            }

            return try {
                NotificationType.valueOf( event )
            } catch( e: IllegalArgumentException ) {
                Timber.e( e, "Could not parse Push Notification event: $event" )
                NotificationType.Unknown
            }
//            catch( e: NullPointerException ) {
//                Timber.e( e, "Could not parse Push Notification event: $event" )
//                NotificationType.Unknown
//            }
        }

        private fun parseUri( uriString: String? ): Uri {
            payload?.let {
                try {
                    return Uri.parse( it.getJSONObject( "params" ).getString( "uri" ) )
                } catch( e: Exception ) {
                    // I dont know what to do here yet
                }
            }
            return if( ! uriString.isNullOrEmpty() ) Uri.parse( uriString ) else Uri.EMPTY
        }

        private fun parsePayload( payload: String? ): JSONObject? {
            return try {
                JSONObject( payload )
            } catch( e: Exception ) {
                null
            }
        }
    }
}

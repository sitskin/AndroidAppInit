package com.example.sitskin.initaplication.util

import android.content.Intent
import android.net.Uri
import com.example.hopapilibrary.model.GroupOutputModel
import com.example.hopapilibrary.model.ItemOutputModel
import java.lang.String.format

object ShareUtils {

    private const val SHARE_GROUP_TITLE = "Share this group"
    private const val SHARE_GROUP_TEXT = "Join me in the %s group on hOp! It makes sharing between neighbors simpler.\n\n%s"

    private const val SHARE_REFERRAL_TITLE = "Share your referral code"

    private const val SHARE_URL_HOST = "hop.mk"
    private const val SHARE_URL_SEPARATOR = "-"

    private const val HOST_ITEM = "item"

    private const val HOST_GROUP = "group"
    private const val PATH_GROUP = "/g-"

    private const val HOST_CONVERSATION = "conversation"

    const val URL_PATTERN = "{{url}}"

    enum class ShareUrlType {
        Unknown,
        Item,
        Conversation,
        Group
    }

    fun isShareUrl( uri: Uri? ): Boolean {
        return getShareUrlType( uri ) != ShareUrlType.Unknown
    }

    fun getShareUrlType( shareUrl: Uri? ): ShareUrlType {

        shareUrl ?: return ShareUrlType.Unknown

        val host = shareUrl.host
        val path = shareUrl.path

        if( host.isNullOrEmpty() || path.isNullOrEmpty() ) return ShareUrlType.Unknown

        // hopmarket://item/{kind}/{id}
        if( host == HOST_ITEM ) return ShareUrlType.Item
        // hopmarket://group/{id}
        if( host == HOST_GROUP ) return ShareUrlType.Group
        // hopmarket://conversation/{id}
        if( host == HOST_CONVERSATION ) return ShareUrlType.Conversation

        // if the share url isn't the above, it must follow the pattern:
        // http://hop.mk/i-{id} or http://hop.mk/g-{id}
        if( ! host.endsWith( SHARE_URL_HOST ) ) return ShareUrlType.Unknown

        // http://hop.mk/g-{id}
        if( path.startsWith( PATH_GROUP ) ) return ShareUrlType.Group

        return ShareUrlType.Unknown

    }

    fun getSharedItemKind( shareUrl: Uri? ): ItemOutputModel.KindEnum? {

        shareUrl ?: return null

        val host = shareUrl.host
        val path = shareUrl.lastPathSegment

        if( host.isNullOrEmpty() || path.isNullOrEmpty() ) return null

        // hopmarket://item/{kind}/{id}
        return if( host == HOST_ITEM || host == HOST_GROUP ) ItemOutputModel.KindEnum.valueOf( path ) else null

    }

    fun getSharedItemId( shareUrl: Uri? ): String? {

        shareUrl ?: return null

        val host = shareUrl.host
        val path = shareUrl.lastPathSegment

        if( host.isNullOrEmpty() || path.isNullOrEmpty() ) return null

        // http://hop.mk/l-{id} or http://hop.mk/r-{id} or http://hop.mk/g-{id}
        if( host.endsWith( SHARE_URL_HOST ) ) {
            return if( path.contains( SHARE_URL_SEPARATOR ) ) path.split( SHARE_URL_SEPARATOR.toRegex() ).dropLastWhile { it.isEmpty() }.toTypedArray()[ 1 ] else null
        }

        // hopmarket://listing/{id} or hopmarket://request/{id} or hopmarket://group/{id} or hopmarket://conversation/{id}
        return if( host == HOST_ITEM || host == HOST_GROUP || host == HOST_CONVERSATION ) path else null

    }

    fun shareReferral( shareUrl: String, subject: String, message: String ): Intent {
        return Intent.createChooser(
            baseIntent()
                .putExtra( Intent.EXTRA_SUBJECT, subject )
                .putExtra( Intent.EXTRA_TEXT, message.replace( URL_PATTERN, shareUrl ) ),
            SHARE_REFERRAL_TITLE
        )
    }

    fun share( group: GroupOutputModel): Intent {
        return Intent.createChooser(
            baseIntent()
                .putExtra( Intent.EXTRA_TEXT, format( SHARE_GROUP_TEXT, group.name, group.shareUrl ) ),
            SHARE_GROUP_TITLE
        )
    }

    private fun baseIntent(): Intent {
        return Intent()
            .setAction( Intent.ACTION_SEND )
            .setType( "text/plain" )
    }
}

package com.example.sitskin.initaplication.util

import com.example.sitskin.initaplication.util.prefs.PrefUtils.Companion.loggedInUserId
import com.example.hopapilibrary.model.ItemOutputModel
import com.example.hopapilibrary.model.ItemOutputModel.KindEnum

object ItemUtils {

    fun isItemOwner( item: ItemOutputModel? ): Boolean {
        return item?.owner?.id == loggedInUserId
    }

    fun getKind( kind: String ): KindEnum? {
        return try {
            if( ! kind.isEmpty() ) KindEnum.valueOf( kind ) else null
        } catch( e: IllegalArgumentException ) {
            null
        }
    }
}

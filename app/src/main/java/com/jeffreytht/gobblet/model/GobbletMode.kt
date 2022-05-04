package com.jeffreytht.gobblet.model

import androidx.annotation.IntDef

class GobbletMode {
    companion object {
        const val SINGLE_PLAYER = 1
        const val TWO_PLAYERS = 2
    }

    @IntDef(SINGLE_PLAYER, TWO_PLAYERS)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Mode
}



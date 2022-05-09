package com.jeffreytht.gobblet.model

import androidx.annotation.IntDef
import com.jeffreytht.gobblet.model.Peace.Companion.NO_COLOR

data class Winner(
    @Peace.Color val color: Int,
    @Line val line: Int,
    val idx: Int
) {
    companion object {
        const val ROW = 0
        const val COL = 1
        const val LEFT_DIAGONAL = 2
        const val RIGHT_DIAGONAL = 3
        const val NO_LINE = 4

        @IntDef(ROW, COL, LEFT_DIAGONAL, RIGHT_DIAGONAL, NO_LINE)
        @Retention(AnnotationRetention.SOURCE)
        annotation class Line

        val NO_WINNER = Winner(
            color = NO_COLOR,
            line = NO_LINE,
            idx = -1
        )
    }
}

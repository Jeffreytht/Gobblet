package com.jeffreytht.gobblet.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.IntDef
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Peace(
    val id: Int,
    @Color val color: Int,
    @Size val size: Int,
    @DrawableRes var resId: Int
) : Parcelable {
    companion object {
        const val EXTRA_SMALL = 1
        const val SMALL = 10
        const val MEDIUM = 100
        const val LARGE = 1000
        const val NO_COLOR = 0
        const val GREEN = 1
        const val RED = 2
    }

    @IntDef(EXTRA_SMALL, SMALL, MEDIUM, LARGE)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Size

    @Target(AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.TYPE)
    @IntDef(GREEN, RED)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Color

    val scale: Float
        get() {
            return when (size) {
                EXTRA_SMALL -> 0.25f
                SMALL -> 0.5f
                MEDIUM -> 0.75f
                else -> 1f
            }
        }
}

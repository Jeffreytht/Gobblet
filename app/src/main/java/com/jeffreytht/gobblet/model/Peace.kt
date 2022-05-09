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
    val scale: Float,
    @DrawableRes var resId: Int
) : Parcelable {
    companion object {
        const val EXTRA_SMALL = 0
        const val SMALL = 1
        const val MEDIUM = 2
        const val LARGE = 3
        const val GREEN = 0
        const val RED = 1
        const val NO_COLOR = 2
    }

    @IntDef(EXTRA_SMALL, SMALL, MEDIUM, LARGE)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Size

    @Target(AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.TYPE)
    @IntDef(GREEN, RED)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Color
}

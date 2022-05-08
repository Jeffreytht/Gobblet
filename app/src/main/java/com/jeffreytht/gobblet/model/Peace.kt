package com.jeffreytht.gobblet.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.IntDef
import com.jeffreytht.gobblet.R
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Peace(
    val id: Int,
    @Color val color: Int,
    @Size val size: Int,
    val scale: Float
) : Parcelable {
    companion object {
        const val EXTRA_SMALL = 0
        const val SMALL = 1
        const val MEDIUM = 2
        const val LARGE = 3
        const val GREEN = 0
        const val RED = 1
    }

    @IntDef(EXTRA_SMALL, SMALL, MEDIUM, LARGE)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Size

    @IntDef(GREEN, RED)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Color

    @get: DrawableRes
    val resId: Int
        get() {
            return when (color) {
                RED -> R.drawable.ic_red_large_peace
                else -> R.drawable.ic_green_large_peace
            }
        }
}

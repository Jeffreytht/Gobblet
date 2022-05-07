package com.jeffreytht.gobblet.util

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat

class ResourcesProvider(private val context: Context) {
    fun getDrawable(@DrawableRes resId: Int): Drawable? {
        return ResourcesCompat.getDrawable(context.resources, resId, context.theme)
    }

    fun getAspectRatio(@DrawableRes resId: Int): Float {
        val drawable = getDrawable(resId)
        var aspectRatio = 1.0f
        drawable?.let { aspectRatio = it.intrinsicWidth.toFloat() / it.intrinsicHeight }
        return aspectRatio
    }
}
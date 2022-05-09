package com.jeffreytht.gobblet.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
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

    fun getString(@StringRes resId: Int, vararg formatArgs: Any): String {
        return context.getString(resId, *formatArgs)
    }

    fun makeToast(@StringRes resId: Int, duration: Int) {
        Toast.makeText(context, resId, duration).show()
    }

    fun makeToast(message: String, duration: Int) {
        Toast.makeText(context, message, duration).show()
    }
}
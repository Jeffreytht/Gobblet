package com.jeffreytht.gobblet.util

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

class DialogBuilder(private val context: Context) {
    fun showDialog(
        @StringRes title: Int,
        @StringRes message: Int,
        @DrawableRes icon: Int,
        @StringRes positiveButtonText: Int,
        @StringRes negativeButtonText: Int,
        onClickListener: DialogInterface.OnClickListener
    ) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setIcon(icon)
            .setPositiveButton(positiveButtonText, onClickListener)
            .setNegativeButton(negativeButtonText, onClickListener)
            .show()
    }
}
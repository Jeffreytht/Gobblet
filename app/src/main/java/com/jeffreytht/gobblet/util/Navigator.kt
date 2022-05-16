package com.jeffreytht.gobblet.util

import android.app.Activity
import android.content.Intent

class Navigator(private val activity: Activity) {
    fun startActivity(intent: Intent) {
        activity.startActivity(intent)
    }

    fun finish() {
        activity.finish()
    }
}
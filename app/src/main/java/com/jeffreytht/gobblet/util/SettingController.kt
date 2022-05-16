package com.jeffreytht.gobblet.util

import android.content.Intent
import com.jeffreytht.gobblet.ui.HomeActivity
import com.jeffreytht.gobblet.ui.SettingActivity

class SettingController(private val activity: HomeActivity) {
    fun update() {
        val intent = Intent(activity, SettingActivity::class.java)
        activity.startActivity(intent)
    }
}
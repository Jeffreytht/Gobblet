package com.jeffreytht.gobblet.util

import android.content.Intent
import com.jeffreytht.gobblet.model.GobbletMode
import com.jeffreytht.gobblet.ui.GobbletActivity
import com.jeffreytht.gobblet.ui.GobbletActivity.Companion.GOBBLET_MODE
import com.jeffreytht.gobblet.ui.HomeActivity
import javax.inject.Inject

class GobbletController @Inject constructor(private val activity: HomeActivity) {
    fun update(@GobbletMode.Mode mode: Int) {
        val intent = Intent(activity, GobbletActivity::class.java).apply {
            putExtra(GOBBLET_MODE, mode)
        }
        activity.startActivity(intent)
    }
}
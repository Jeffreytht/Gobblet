package com.jeffreytht.gobblet.util

import android.content.Intent
import com.jeffreytht.gobblet.model.Game
import com.jeffreytht.gobblet.ui.GameActivity
import com.jeffreytht.gobblet.ui.GameActivity.Companion.GOBBLET_MODE
import com.jeffreytht.gobblet.ui.HomeActivity
import javax.inject.Inject

class GobbletController @Inject constructor(private val activity: HomeActivity) {
    fun update(@Game.Mode mode: Int) {
        val intent = Intent(activity, GameActivity::class.java).apply {
            putExtra(GOBBLET_MODE, mode)
        }
        activity.startActivity(intent)
    }
}
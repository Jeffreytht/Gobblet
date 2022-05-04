package com.jeffreytht.gobblet.ui

import android.app.Activity
import android.os.Bundle
import com.jeffreytht.gobblet.R

class GobbletActivity : Activity() {
    companion object {
        const val GOBBLET_MODE = "GOBBLET_MODE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gobblet)
    }
}
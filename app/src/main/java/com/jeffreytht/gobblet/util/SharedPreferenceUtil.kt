package com.jeffreytht.gobblet.util

import android.content.Context
import com.jeffreytht.gobblet.R

class SharedPreferenceUtil(
    private val context: Context
) {
    companion object {
        const val NAME = "com.jeffreytht.gobblet.Setting"
    }

    private val sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)

    fun getVolume(): Boolean {
        return sharedPreferences.getBoolean(context.getString(R.string.volume), true)
    }

    fun setVolume(enable: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(context.getString(R.string.volume), enable)
            apply()
        }
    }
}
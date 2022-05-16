package com.jeffreytht.gobblet.util

import android.content.Context
import com.jeffreytht.gobblet.R
import com.jeffreytht.gobblet.model.AIPlayer.Companion.MEDIUM

class SharedPreferenceUtil(
    private val context: Context
) {
    companion object {
        const val NAME = "com.jeffreytht.gobblet.Setting"
        const val DEFAULT_DIMENSION = 4
    }

    private val sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)

    fun getDimension(): Int {
        return sharedPreferences.getInt(
            context.getString(R.string.pref_dimension),
            DEFAULT_DIMENSION
        )
    }

    fun setDimension(dimension: Int) {
        with(sharedPreferences.edit()) {
            putInt(context.getString(R.string.pref_dimension), dimension)
            apply()
        }
    }

    fun getDifficulty(): Int {
        return sharedPreferences.getInt(context.getString(R.string.pref_difficulty), MEDIUM)
    }

    fun setDifficulty(difficulty: Int) {
        with(sharedPreferences.edit()) {
            putInt(context.getString(R.string.pref_difficulty), difficulty)
            apply()
        }
    }

    fun getIsSoundOn(): Boolean {
        return sharedPreferences.getBoolean(context.getString(R.string.pref_sound), true)
    }

    fun setSoundEnable(enable: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(context.getString(R.string.pref_sound), enable)
            apply()
        }
    }
}
package com.jeffreytht.gobblet.ui

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel
import com.jeffreytht.gobblet.model.AIPlayer.Companion.EASY
import com.jeffreytht.gobblet.model.AIPlayer.Companion.HARD
import com.jeffreytht.gobblet.model.AIPlayer.Companion.MEDIUM
import com.jeffreytht.gobblet.util.Navigator
import com.jeffreytht.gobblet.util.SharedPreferenceUtil
import com.jeffreytht.gobblet.util.Sound
import com.jeffreytht.gobblet.util.SoundUtil

class SettingActivityViewModel(
    private val soundUtil: SoundUtil,
    private val sharedPreferenceUtil: SharedPreferenceUtil,
    private val navigator: Navigator
) : ViewModel() {
    var isSoundOn = ObservableBoolean(soundUtil.isSoundOn())
    var difficultyProgress = ObservableInt()
    var dimension = ObservableInt(sharedPreferenceUtil.getDimension())

    init {
        difficultyProgress.set(
            when (sharedPreferenceUtil.getDifficulty()) {
                EASY -> 0
                MEDIUM -> 1
                else -> 2
            }
        )
    }

    fun onDimensionChanged(dimension: Int) {
        soundUtil.play(Sound.CLICK)
        sharedPreferenceUtil.setDimension(dimension)
        this.dimension.set(dimension)
    }

    fun onSoundClicked(enable: Boolean) {
        if (isSoundOn.get() == enable) {
            return
        }

        isSoundOn.set(enable)
        soundUtil.enableSound(enable)
        soundUtil.play(Sound.CLICK)
    }

    fun onDifficultyChanged(progress: Int, fromUser: Boolean) {
        if (!fromUser) {
            return
        }

        val difficulty = when (progress) {
            0 -> EASY
            1 -> MEDIUM
            else -> HARD
        }

        difficultyProgress.set(progress)
        sharedPreferenceUtil.setDifficulty(difficulty)
        soundUtil.play(Sound.CLICK)
    }

    fun onBackPressed() {
        soundUtil.play(Sound.CLICK)
        navigator.finish()
    }
}
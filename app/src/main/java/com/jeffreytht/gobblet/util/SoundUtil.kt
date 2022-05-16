package com.jeffreytht.gobblet.util

import android.content.Context
import android.media.MediaPlayer
import androidx.annotation.IntDef
import com.jeffreytht.gobblet.R

interface Sound {
    companion object {
        const val GAME_WIN = R.raw.game_win
        const val GAME_LOSE = R.raw.game_lose
        const val CLICK = R.raw.click
        const val GAME_COLOR_GRID = R.raw.game_color_grid
    }

    @IntDef(GAME_WIN, GAME_LOSE, CLICK)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Type
}

class SoundUtil(
    private val context: Context,
    private val sharedPreferenceUtil: SharedPreferenceUtil
) {
    private var mIsVolumeOn = sharedPreferenceUtil.getVolume()

    fun isVolumeOn(): Boolean {
        return mIsVolumeOn
    }

    fun enableSound(enable: Boolean) {
        mIsVolumeOn = enable
        sharedPreferenceUtil.setVolume(enable)
    }

    fun play(@Sound.Type type: Int) {
        if (!mIsVolumeOn) {
            return
        }

        val mediaPlayer = MediaPlayer.create(context, type)
        mediaPlayer.start()
    }
}
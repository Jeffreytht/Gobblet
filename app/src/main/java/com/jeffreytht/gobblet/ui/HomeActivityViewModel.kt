package com.jeffreytht.gobblet.ui

import androidx.annotation.DrawableRes
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.jeffreytht.gobblet.R
import com.jeffreytht.gobblet.model.Game
import com.jeffreytht.gobblet.util.GobbletController

class HomeActivityViewModel(
    private val gobbletController: GobbletController,
    private val soundUtil: SoundUtil
) :
    ViewModel() {
    private var isVolumeOn = true
    var volumeIcon = ObservableField<@DrawableRes Int>(getVolumeIcons())

    fun onSinglePlayerClicked() {
        soundUtil.play(Sound.CLICK)
        gobbletController.update(Game.SINGLE_PLAYER)
    }

    fun onTwoPlayersClicked() {
        soundUtil.play(Sound.CLICK)
        gobbletController.update(Game.TWO_PLAYERS)
    }

    fun onVolumeClicked() {
        isVolumeOn = !isVolumeOn
        volumeIcon.set(getVolumeIcons())
        soundUtil.enableSound(isVolumeOn)
        soundUtil.play(Sound.CLICK)
    }

    @DrawableRes
    fun getVolumeIcons(): Int {
        return if (isVolumeOn) {
            R.drawable.ic_volume_up
        } else {
            R.drawable.ic_volume_off
        }
    }
}
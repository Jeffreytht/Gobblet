package com.jeffreytht.gobblet.ui

import androidx.lifecycle.ViewModel
import com.jeffreytht.gobblet.model.Game
import com.jeffreytht.gobblet.util.GobbletController
import com.jeffreytht.gobblet.util.SettingController
import com.jeffreytht.gobblet.util.Sound
import com.jeffreytht.gobblet.util.SoundUtil

class HomeActivityViewModel(
    private val gobbletController: GobbletController,
    private val settingController: SettingController,
    private val soundUtil: SoundUtil
) :
    ViewModel() {

    fun onSinglePlayerClicked() {
        soundUtil.play(Sound.CLICK)
        gobbletController.update(Game.SINGLE_PLAYER)
    }

    fun onTwoPlayersClicked() {
        soundUtil.play(Sound.CLICK)
        gobbletController.update(Game.TWO_PLAYERS)
    }

    fun onSettingClicked() {
        soundUtil.play(Sound.CLICK)
        settingController.update()
    }
}
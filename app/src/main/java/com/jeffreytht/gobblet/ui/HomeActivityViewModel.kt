package com.jeffreytht.gobblet.ui

import com.jeffreytht.gobblet.R
import com.jeffreytht.gobblet.model.GobbletMode.Companion.SINGLE_PLAYER
import com.jeffreytht.gobblet.model.GobbletMode.Companion.TWO_PLAYERS
import com.jeffreytht.gobblet.util.GobbletController
import javax.inject.Inject

class HomeActivityViewModel @Inject constructor(val gobbletController: GobbletController) {
    private var isVolumeOn = true
    var volumeIcon = R.drawable.ic_volume_up

    fun onSinglePlayerClicked() {
        gobbletController.update(SINGLE_PLAYER)
    }

    fun onTwoPlayersClicked() {
        gobbletController.update(TWO_PLAYERS)
    }

    fun onVolumeClicked() {
        isVolumeOn = !isVolumeOn
    }
}
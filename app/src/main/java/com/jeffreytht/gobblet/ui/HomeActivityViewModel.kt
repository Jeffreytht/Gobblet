package com.jeffreytht.gobblet.ui

import androidx.annotation.DrawableRes
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.jeffreytht.gobblet.R
import com.jeffreytht.gobblet.model.GobbletMode.Companion.SINGLE_PLAYER
import com.jeffreytht.gobblet.model.GobbletMode.Companion.TWO_PLAYERS
import com.jeffreytht.gobblet.util.GobbletController

class HomeActivityViewModel constructor(private val gobbletController: GobbletController) :
    ViewModel() {
    private var isVolumeOn = true
    var volumeIcon = ObservableField<@DrawableRes Int>(getVolumeIcons())

    fun onSinglePlayerClicked() {
        gobbletController.update(SINGLE_PLAYER)
    }

    fun onTwoPlayersClicked() {
        gobbletController.update(TWO_PLAYERS)
    }

    fun onVolumeClicked() {
        isVolumeOn = !isVolumeOn
        volumeIcon.set(getVolumeIcons())
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
package com.jeffreytht.gobblet.ui

import androidx.annotation.DrawableRes
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.ViewModel
import com.jeffreytht.gobblet.R
import com.jeffreytht.gobblet.model.GobbletMode.Companion.SINGLE_PLAYER
import com.jeffreytht.gobblet.model.GobbletMode.Companion.TWO_PLAYERS
import com.jeffreytht.gobblet.util.GobbletController

class HomeActivityViewModel constructor(private val gobbletController: GobbletController) :
    ViewModel(), Observable {
    private val callbacks = PropertyChangeRegistry()
    private var isVolumeOn = true

    fun onSinglePlayerClicked() {
        gobbletController.update(SINGLE_PLAYER)
    }

    fun onTwoPlayersClicked() {
        gobbletController.update(TWO_PLAYERS)
    }

    fun onVolumeClicked() {
        isVolumeOn = !isVolumeOn
        notifyPropertyChanged()
    }

    @DrawableRes
    fun getVolumeIcon(): Int {
        return if (isVolumeOn) {
            R.drawable.ic_volume_up
        } else {
            R.drawable.ic_volume_off
        }
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.remove(callback)
    }

    private fun notifyPropertyChanged() {
        callbacks.notifyCallbacks(this, 0, null)
    }
}
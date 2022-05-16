package com.jeffreytht.gobblet.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jeffreytht.gobblet.model.AIPlayer
import com.jeffreytht.gobblet.model.GameSetting
import com.jeffreytht.gobblet.ui.GameActivityViewModel
import com.jeffreytht.gobblet.util.*

class GameActivityViewModelFactory(
    private val gameSetting: GameSetting,
    private val resourcesProvider: ResourcesProvider,
    private val dialogBuilder: DialogBuilder,
    private val aiPlayer: AIPlayer,
    private val soundUtil: SoundUtil,
    private val navigator: Navigator,
    private val adUtil: AdUtil
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return GameActivityViewModel(
            gameSetting,
            soundUtil,
            resourcesProvider,
            dialogBuilder,
            aiPlayer,
            navigator,
            adUtil
        ) as T
    }
}

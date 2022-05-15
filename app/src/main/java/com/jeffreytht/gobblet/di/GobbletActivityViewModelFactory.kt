package com.jeffreytht.gobblet.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jeffreytht.gobblet.model.AIPlayer
import com.jeffreytht.gobblet.ui.GameActivity
import com.jeffreytht.gobblet.ui.GameActivityViewModel
import com.jeffreytht.gobblet.ui.GameSettingProvider
import com.jeffreytht.gobblet.ui.SoundUtil
import com.jeffreytht.gobblet.util.DialogBuilder
import com.jeffreytht.gobblet.util.ResourcesProvider

class GobbletActivityViewModelFactory(
    private val gobbletActivity: GameActivity,
    private val gameSettingProvider: GameSettingProvider,
    private val resourcesProvider: ResourcesProvider,
    private val dialogBuilder: DialogBuilder,
    private val aiPlayer: AIPlayer,
    private val soundUtil: SoundUtil
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return GameActivityViewModel(
            gobbletActivity,
            gameSettingProvider,
            soundUtil,
            resourcesProvider,
            dialogBuilder,
            aiPlayer
        ) as T
    }
}

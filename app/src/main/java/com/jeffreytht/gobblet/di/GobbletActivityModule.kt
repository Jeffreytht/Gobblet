package com.jeffreytht.gobblet.di

import androidx.lifecycle.ViewModelProvider
import com.jeffreytht.gobblet.model.AIPlayer
import com.jeffreytht.gobblet.ui.GameActivity
import com.jeffreytht.gobblet.ui.GameActivityViewModel
import com.jeffreytht.gobblet.ui.GameSettingProvider
import com.jeffreytht.gobblet.ui.SoundUtil
import com.jeffreytht.gobblet.util.DialogBuilder
import com.jeffreytht.gobblet.util.ResourcesProvider
import dagger.Module
import dagger.Provides

@Module(includes = [GobbletSettingModule::class])
abstract class GobbletActivityModule {
    companion object {
        @Provides
        fun providesAIPlayer(
            gameSettingProvider: GameSettingProvider,
            resourcesProvider: ResourcesProvider
        ): AIPlayer {
            return AIPlayer(gameSettingProvider, resourcesProvider)
        }

        @Provides
        fun providesGobbletActivityViewModel(
            gobbletActivity: GameActivity,
            gameSettingProvider: GameSettingProvider,
            resourcesProvider: ResourcesProvider,
            dialogBuilder: DialogBuilder,
            aiPlayer: AIPlayer,
            soundUtil: SoundUtil
        ): GameActivityViewModel {
            return ViewModelProvider(
                gobbletActivity,
                GobbletActivityViewModelFactory(
                    gobbletActivity,
                    gameSettingProvider,
                    resourcesProvider,
                    dialogBuilder,
                    aiPlayer,
                    soundUtil
                )
            ).get(
                GameActivityViewModel::class.java
            )
        }

        @Provides
        fun providesDialogBuilder(activity: GameActivity): DialogBuilder {
            return DialogBuilder(activity)
        }
    }

    abstract fun contributeGobbletActivity(): GameActivity
}
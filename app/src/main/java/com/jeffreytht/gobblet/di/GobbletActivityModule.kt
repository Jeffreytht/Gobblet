package com.jeffreytht.gobblet.di

import androidx.lifecycle.ViewModelProvider
import com.jeffreytht.gobblet.model.AIPlayer
import com.jeffreytht.gobblet.model.Game
import com.jeffreytht.gobblet.model.GameSetting
import com.jeffreytht.gobblet.model.Peace.Companion.GREEN
import com.jeffreytht.gobblet.model.Peace.Companion.RED
import com.jeffreytht.gobblet.ui.GameActivity
import com.jeffreytht.gobblet.ui.GameActivityViewModel
import com.jeffreytht.gobblet.util.DialogBuilder
import com.jeffreytht.gobblet.util.ResourcesProvider
import com.jeffreytht.gobblet.util.SharedPreferenceUtil
import com.jeffreytht.gobblet.util.SoundUtil
import dagger.Module
import dagger.Provides

@Module
abstract class GobbletActivityModule {
    companion object {
        @Provides
        fun providesAIPlayer(
            gameSetting: GameSetting,
            resourcesProvider: ResourcesProvider
        ): AIPlayer {
            return AIPlayer(gameSetting, resourcesProvider)
        }

        @Provides
        fun providesGobbletActivityViewModel(
            gobbletActivity: GameActivity,
            gameSetting: GameSetting,
            resourcesProvider: ResourcesProvider,
            dialogBuilder: DialogBuilder,
            aiPlayer: AIPlayer,
            soundUtil: SoundUtil
        ): GameActivityViewModel {
            return ViewModelProvider(
                gobbletActivity,
                GobbletActivityViewModelFactory(
                    gobbletActivity,
                    gameSetting,
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

        @Provides
        fun providesGameSetting(
            gobbletActivity: GameActivity,
            sharedPreferenceUtil: SharedPreferenceUtil
        ): GameSetting {
            return GameSetting().apply {
                dimension = sharedPreferenceUtil.getDimension()
                difficulty = sharedPreferenceUtil.getDifficulty()
                player1Color = GREEN
                player2Color = RED
                mode = gobbletActivity.intent.getIntExtra(
                    GameActivity.GOBBLET_MODE,
                    Game.SINGLE_PLAYER
                )
            }
        }
    }

    abstract fun contributeGobbletActivity(sharedPreferenceUtil: SharedPreferenceUtil): GameActivity
}
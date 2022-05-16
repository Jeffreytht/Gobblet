package com.jeffreytht.gobblet.di

import android.app.Activity
import androidx.lifecycle.ViewModelProvider
import com.jeffreytht.gobblet.model.AIPlayer
import com.jeffreytht.gobblet.model.Game
import com.jeffreytht.gobblet.model.GameSetting
import com.jeffreytht.gobblet.model.Peace.Companion.GREEN
import com.jeffreytht.gobblet.model.Peace.Companion.RED
import com.jeffreytht.gobblet.ui.GameActivity
import com.jeffreytht.gobblet.ui.GameActivityViewModel
import com.jeffreytht.gobblet.util.*
import dagger.Module
import dagger.Provides

@Module(includes = [NavigatorModule::class])
abstract class GameActivityModule {
    companion object {
        @Provides
        fun providesAIPlayer(
            gameSetting: GameSetting,
            resourcesProvider: ResourcesProvider
        ): AIPlayer {
            return AIPlayer(gameSetting, resourcesProvider)
        }

        @Provides
        fun providesGameActivityViewModel(
            gameActivity: GameActivity,
            gameSetting: GameSetting,
            resourcesProvider: ResourcesProvider,
            dialogBuilder: DialogBuilder,
            aiPlayer: AIPlayer,
            soundUtil: SoundUtil,
            navigator: Navigator
        ): GameActivityViewModel {
            return ViewModelProvider(
                gameActivity,
                GameActivityViewModelFactory(
                    gameActivity,
                    gameSetting,
                    resourcesProvider,
                    dialogBuilder,
                    aiPlayer,
                    soundUtil,
                    navigator
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
            gameActivity: GameActivity,
            sharedPreferenceUtil: SharedPreferenceUtil
        ): GameSetting {
            return GameSetting().apply {
                dimension = sharedPreferenceUtil.getDimension()
                difficulty = sharedPreferenceUtil.getDifficulty()
                player1Color = GREEN
                player2Color = RED
                mode = gameActivity.intent.getIntExtra(
                    GameActivity.GOBBLET_MODE,
                    Game.SINGLE_PLAYER
                )
            }
        }

        @Provides
        fun providesActivity(gameActivity: GameActivity): Activity {
            return gameActivity
        }
    }

    abstract fun contributeGameActivity(): GameActivity
}
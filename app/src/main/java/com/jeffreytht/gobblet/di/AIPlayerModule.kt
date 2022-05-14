package com.jeffreytht.gobblet.di

import com.jeffreytht.gobblet.model.AIPlayer
import com.jeffreytht.gobblet.model.Peace
import com.jeffreytht.gobblet.util.ResourcesProvider
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
interface AIPlayerModule {
    companion object {
        @Provides
        fun providesAIPlayer(
            @Named(AI_COLOR) @Peace.Color aiColor: Int,
            @Named(PLAYER_COLOR) @Peace.Color playerColor: Int,
            @Named(DIFFICULTY) @AIPlayer.Difficulty difficulty: Int,
            @Named(DIMENSION) dimension: Int,
            resourcesProvider: ResourcesProvider
        ): AIPlayer {
            return AIPlayer(aiColor, playerColor, difficulty, dimension, resourcesProvider)
        }
    }
}
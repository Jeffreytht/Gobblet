package com.jeffreytht.gobblet.di

import com.jeffreytht.gobblet.model.Game
import com.jeffreytht.gobblet.model.GobbletMode
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
interface GameModule {
    companion object {
        @Provides
        fun providesGame(
            @Named(DIMENSION) dimension: Int,
            @Named(GOBBLET_MODE) @GobbletMode.Mode gobbletMode: Int
        ): Game {
            return Game(dimension, gobbletMode)
        }
    }
}
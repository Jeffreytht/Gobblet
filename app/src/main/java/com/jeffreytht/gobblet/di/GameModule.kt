package com.jeffreytht.gobblet.di

import com.jeffreytht.gobblet.model.Game
import dagger.Module
import dagger.Provides

@Module
interface GameModule {
    companion object {
        @Provides
        fun providesGame(dimension: Int): Game {
            return Game(dimension)
        }
    }
}
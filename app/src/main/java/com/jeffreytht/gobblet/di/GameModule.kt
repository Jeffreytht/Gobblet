package com.jeffreytht.gobblet.di

import com.jeffreytht.gobblet.model.Game
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
interface GameModule {
    companion object {
        @Provides
        fun providesGame(
            @Named(ROW) row: Int,
            @Named(COL) col: Int,
        ): Game {
            return Game(row, col)
        }
    }
}
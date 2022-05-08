package com.jeffreytht.gobblet.di

import com.jeffreytht.gobblet.model.Game
import dagger.BindsInstance
import dagger.Component

@Component(modules = [GameModule::class])
interface GameComponent {
    fun provideGame(): Game

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun withDimension(dimension: Int): Builder

        fun build(): GameComponent
    }
}
package com.jeffreytht.gobblet.di

import com.jeffreytht.gobblet.model.Game
import dagger.BindsInstance
import dagger.Component
import javax.inject.Named

const val DIMENSION = "DIMENSION"
const val GOBBLET_MODE = "GOBBLET_MODE"

@Component(modules = [GameModule::class])
interface GameComponent {
    fun provideGame(): Game

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun withDimension(@Named(DIMENSION) dimension: Int): Builder

        fun build(): GameComponent
    }
}
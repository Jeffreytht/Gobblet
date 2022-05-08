package com.jeffreytht.gobblet.di

import com.jeffreytht.gobblet.model.Game
import dagger.BindsInstance
import dagger.Component
import javax.inject.Named

const val ROW = "ROW"
const val COL = "COL"

@Component(modules = [GameModule::class])
interface GameComponent {
    fun provideGame(): Game

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun withRow(@Named(ROW) row: Int): Builder

        @BindsInstance
        fun withCol(@Named(COL) col: Int): Builder

        fun build(): GameComponent
    }
}
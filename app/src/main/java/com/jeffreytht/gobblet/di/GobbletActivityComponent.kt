package com.jeffreytht.gobblet.di

import android.content.Context
import com.jeffreytht.gobblet.model.AIPlayer
import com.jeffreytht.gobblet.model.GobbletMode
import com.jeffreytht.gobblet.model.Peace
import com.jeffreytht.gobblet.ui.GobbletActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Named

const val AI_COLOR = "AI_COLOR"
const val PLAYER_COLOR = "PLAYER_COLOR"
const val DIFFICULTY = "DIFFICULTY"

@Component(modules = [UtilModule::class, AIPlayerModule::class, GobbletActivityModule::class])
interface GobbletActivityComponent {
    fun inject(gobbletActivity: GobbletActivity)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun withDimension(@Named(DIMENSION) dimension: Int): Builder

        @BindsInstance
        fun withActivity(gobbletActivity: GobbletActivity): Builder

        @BindsInstance
        fun withContext(context: Context): Builder

        @BindsInstance
        fun withGobbletMode(@Named(GOBBLET_MODE) @GobbletMode.Mode gobbletMode: Int): Builder

        @BindsInstance
        fun withAIColor(@Named(AI_COLOR) @Peace.Color color: Int): Builder

        @BindsInstance
        fun withPlayerColor(@Named(PLAYER_COLOR) @Peace.Color color: Int): Builder

        @BindsInstance
        fun withDifficulty(@Named(DIFFICULTY) @AIPlayer.Difficulty difficulty: Int): Builder

        fun build(): GobbletActivityComponent
    }
}
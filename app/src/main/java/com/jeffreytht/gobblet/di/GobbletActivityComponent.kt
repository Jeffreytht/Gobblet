package com.jeffreytht.gobblet.di

import com.jeffreytht.gobblet.ui.GobbletActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Named

@Component(modules = [GobbletActivityModule::class])
interface GobbletActivityComponent {
    companion object {
        const val ROW = "ROW"
        const val COL = "COL"
    }

    fun inject(gobbletActivity: GobbletActivity)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun withRow(@Named(ROW) row: Int): Builder

        @BindsInstance
        fun withCol(@Named(COL) col: Int): Builder

        @BindsInstance
        fun activity(gobbletActivity: GobbletActivity): Builder
        fun build(): GobbletActivityComponent
    }
}
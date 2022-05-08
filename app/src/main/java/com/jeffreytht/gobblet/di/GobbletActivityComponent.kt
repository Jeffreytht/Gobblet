package com.jeffreytht.gobblet.di

import android.content.Context
import com.jeffreytht.gobblet.ui.GobbletActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Named

@Component(modules = [UtilModule::class, GobbletActivityModule::class])
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
        fun withActivity(gobbletActivity: GobbletActivity): Builder

        @BindsInstance
        fun withContext(context: Context): Builder

        fun build(): GobbletActivityComponent
    }
}
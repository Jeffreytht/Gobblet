package com.jeffreytht.gobblet.di

import android.content.Context
import com.jeffreytht.gobblet.ui.GobbletActivity
import dagger.BindsInstance
import dagger.Component

@Component(modules = [UtilModule::class, GobbletActivityModule::class])
interface GobbletActivityComponent {
    fun inject(gobbletActivity: GobbletActivity)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun withDimension(dimension: Int): Builder

        @BindsInstance
        fun withActivity(gobbletActivity: GobbletActivity): Builder

        @BindsInstance
        fun withContext(context: Context): Builder

        fun build(): GobbletActivityComponent
    }
}
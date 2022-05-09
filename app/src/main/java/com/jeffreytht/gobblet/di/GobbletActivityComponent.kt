package com.jeffreytht.gobblet.di

import android.content.Context
import com.jeffreytht.gobblet.model.GobbletMode
import com.jeffreytht.gobblet.ui.GobbletActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Named

@Component(modules = [UtilModule::class, GobbletActivityModule::class])
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

        fun build(): GobbletActivityComponent
    }
}
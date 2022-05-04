package com.jeffreytht.gobblet.di

import com.jeffreytht.gobblet.ui.HomeActivity
import dagger.BindsInstance
import dagger.Component

@Component
interface HomeActivityComponent {
    fun inject(homeActivity: HomeActivity)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun activity(homeActivity: HomeActivity): Builder
        fun build(): HomeActivityComponent
    }
}

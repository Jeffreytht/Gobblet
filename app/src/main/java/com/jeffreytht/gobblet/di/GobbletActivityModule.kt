package com.jeffreytht.gobblet.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.jeffreytht.gobblet.ui.GobbletActivity
import com.jeffreytht.gobblet.ui.GobbletActivityViewModel
import com.jeffreytht.gobblet.util.ResourcesProvider
import dagger.Module
import dagger.Provides

@Module
interface GobbletActivityModule {
    companion object {
        @Provides
        fun providesGobbletActivityViewModel(
            gobbletActivity: GobbletActivity,
            context: Context,
            dimension: Int,
            resourcesProvider: ResourcesProvider
        ): GobbletActivityViewModel {
            return ViewModelProvider(
                gobbletActivity,
                GobbletActivityViewModelFactory(
                    dimension,
                    context,
                    resourcesProvider
                )
            ).get(
                GobbletActivityViewModel::class.java
            )
        }
    }
}
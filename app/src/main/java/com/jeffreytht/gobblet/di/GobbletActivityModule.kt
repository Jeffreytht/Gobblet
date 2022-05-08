package com.jeffreytht.gobblet.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.jeffreytht.gobblet.util.ResourcesProvider
import com.jeffreytht.gobblet.di.GobbletActivityComponent.Companion.COL
import com.jeffreytht.gobblet.di.GobbletActivityComponent.Companion.ROW
import com.jeffreytht.gobblet.ui.GobbletActivity
import com.jeffreytht.gobblet.ui.GobbletActivityViewModel
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
interface GobbletActivityModule {
    companion object {
        @Provides
        fun providesGobbletActivityViewModel(
            gobbletActivity: GobbletActivity,
            context: Context,
            @Named(ROW) row: Int,
            @Named(COL) col: Int,
            resourcesProvider: ResourcesProvider
        ): GobbletActivityViewModel {
            return ViewModelProvider(
                gobbletActivity,
                GobbletActivityViewModelFactory(
                    row,
                    col,
                    context,
                    resourcesProvider
                )
            ).get(
                GobbletActivityViewModel::class.java
            )
        }
    }
}
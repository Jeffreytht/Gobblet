package com.jeffreytht.gobblet.di

import android.app.Activity
import androidx.lifecycle.ViewModelProvider
import com.jeffreytht.gobblet.util.ResourcesProvider
import com.jeffreytht.gobblet.di.GobbletActivityComponent.Companion.COL
import com.jeffreytht.gobblet.di.GobbletActivityComponent.Companion.ROW
import com.jeffreytht.gobblet.ui.GobbletActivity
import com.jeffreytht.gobblet.ui.GobbletActivityViewModel
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module(includes = [UtilModule::class])
interface GobbletActivityModule {
    companion object {
        @Provides
        fun providesGobbletActivityViewModel(
            gobbletActivity: GobbletActivity,
            @Named(ROW) row: Int,
            @Named(COL) col: Int,
            resourcesProvider: ResourcesProvider
        ): GobbletActivityViewModel {
            return ViewModelProvider(
                gobbletActivity,
                GobbletActivityViewModelFactory(row, col, resourcesProvider)
            ).get(
                GobbletActivityViewModel::class.java
            )
        }

        @Provides
        fun providesActivity(gobbletActivity: GobbletActivity): Activity {
            return gobbletActivity
        }
    }
}
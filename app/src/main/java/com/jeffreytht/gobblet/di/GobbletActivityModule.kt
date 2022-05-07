package com.jeffreytht.gobblet.di

import androidx.lifecycle.ViewModelProvider
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
            @Named(ROW) row: Int,
            @Named(COL) col: Int
        ): GobbletActivityViewModel {
            return ViewModelProvider(
                gobbletActivity,
                GobbletActivityViewModelFactory(row, col)
            ).get(
                GobbletActivityViewModel::class.java
            )
        }
    }
}
package com.jeffreytht.gobblet.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.jeffreytht.gobblet.model.AIPlayer
import com.jeffreytht.gobblet.model.GobbletMode
import com.jeffreytht.gobblet.ui.GobbletActivity
import com.jeffreytht.gobblet.ui.GobbletActivityViewModel
import com.jeffreytht.gobblet.util.DialogBuilder
import com.jeffreytht.gobblet.util.ResourcesProvider
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
            @Named(DIMENSION) dimension: Int,
            @Named(GOBBLET_MODE) @GobbletMode.Mode gobbletMode: Int,
            resourcesProvider: ResourcesProvider,
            dialogBuilder: DialogBuilder,
            aiPlayer: AIPlayer
        ): GobbletActivityViewModel {
            return ViewModelProvider(
                gobbletActivity,
                GobbletActivityViewModelFactory(
                    dimension,
                    context,
                    gobbletMode,
                    resourcesProvider,
                    dialogBuilder,
                    aiPlayer
                )
            ).get(
                GobbletActivityViewModel::class.java
            )
        }
    }
}
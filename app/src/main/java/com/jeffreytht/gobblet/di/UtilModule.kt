package com.jeffreytht.gobblet.di

import android.content.Context
import com.jeffreytht.gobblet.util.DialogBuilder
import com.jeffreytht.gobblet.util.ResourcesProvider
import dagger.Module
import dagger.Provides

@Module
class UtilModule {
    @Provides
    fun providesResourcesProvider(context: Context): ResourcesProvider {
        return ResourcesProvider(context)
    }

    @Provides
    fun providesDialogBuilder(context: Context): DialogBuilder {
        return DialogBuilder(context)
    }
}
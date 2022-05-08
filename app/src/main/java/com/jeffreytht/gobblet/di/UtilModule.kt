package com.jeffreytht.gobblet.di

import android.content.Context
import com.jeffreytht.gobblet.util.ResourcesProvider
import dagger.Module
import dagger.Provides

@Module
class UtilModule {
    @Provides
    fun providesResourcesProvider(context: Context): ResourcesProvider {
        return ResourcesProvider(context)
    }
}
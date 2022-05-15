package com.jeffreytht.gobblet.di

import android.app.Application
import com.jeffreytht.gobblet.ui.SoundUtil
import com.jeffreytht.gobblet.util.ResourcesProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
abstract class AppModule {
    companion object {
        @Singleton
        @Provides
        fun providesResourcesProvider(application: Application): ResourcesProvider {
            return ResourcesProvider(application)
        }

        @Singleton
        @Provides
        fun providesSoundUtil(application: Application): SoundUtil {
            return SoundUtil(application)
        }
    }
}
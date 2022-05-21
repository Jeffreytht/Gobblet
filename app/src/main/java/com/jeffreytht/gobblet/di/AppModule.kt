package com.jeffreytht.gobblet.di

import android.app.Application
import com.jeffreytht.gobblet.util.AdUtil
import com.jeffreytht.gobblet.util.ResourcesProvider
import com.jeffreytht.gobblet.util.SharedPreferenceUtil
import com.jeffreytht.gobblet.util.SoundUtil
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
        fun providesSoundUtil(
            application: Application,
            sharedPreferenceUtil: SharedPreferenceUtil
        ): SoundUtil {
            return SoundUtil(application, sharedPreferenceUtil)
        }

        @Singleton
        @Provides
        fun providesSharedPreferenceUtil(application: Application): SharedPreferenceUtil {
            return SharedPreferenceUtil(application)
        }

        @Singleton
        @Provides
        fun providesAdUtil(application: Application): AdUtil {
            return AdUtil(application)
        }
    }
}
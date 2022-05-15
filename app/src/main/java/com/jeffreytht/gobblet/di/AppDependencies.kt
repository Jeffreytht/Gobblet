package com.jeffreytht.gobblet.di

import com.jeffreytht.gobblet.ui.SoundUtil
import com.jeffreytht.gobblet.util.ResourcesProvider

interface AppDependencies {
    fun providesResourcesProvider(): ResourcesProvider
    fun providesSoundUtil(): SoundUtil
}
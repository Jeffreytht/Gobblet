package com.jeffreytht.gobblet.di

import com.jeffreytht.gobblet.util.ResourcesProvider
import com.jeffreytht.gobblet.util.SoundUtil

interface AppDependencies {
    fun providesResourcesProvider(): ResourcesProvider
    fun providesSoundUtil(): SoundUtil
}
package com.jeffreytht.gobblet.di

import com.jeffreytht.gobblet.util.ResourcesProvider

interface AppDependencies {
    fun providesResourcesProvider(): ResourcesProvider
}
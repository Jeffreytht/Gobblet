package com.jeffreytht.gobblet.di

import com.jeffreytht.gobblet.adapter.PeacesAdapter
import com.jeffreytht.gobblet.model.Peace
import com.jeffreytht.gobblet.util.PeaceHandler
import com.jeffreytht.gobblet.util.ResourcesProvider
import dagger.Module
import dagger.Provides

@Module(includes = [UtilModule::class])
interface PeaceAdapterModule {
    companion object {
        @Provides
        fun providesPeaceAdapter(
            data: ArrayList<Peace>,
            peaceHandler: PeaceHandler,
            resourcesProvider: ResourcesProvider
        ): PeacesAdapter {
            return PeacesAdapter(data, peaceHandler, resourcesProvider)
        }
    }
}
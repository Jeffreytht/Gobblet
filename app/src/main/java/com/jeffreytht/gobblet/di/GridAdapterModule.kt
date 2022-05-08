package com.jeffreytht.gobblet.di

import com.jeffreytht.gobblet.adapter.GridAdapter
import com.jeffreytht.gobblet.model.Grid
import com.jeffreytht.gobblet.util.PeaceHandler
import com.jeffreytht.gobblet.util.ResourcesProvider
import dagger.Module
import dagger.Provides
import kotlin.collections.ArrayList

@Module(includes = [UtilModule::class])
interface GridAdapterModule {
    companion object {
        @Provides
        fun providesGridAdapter(
            data: ArrayList<ArrayList<Grid>>,
            peaceHandler: PeaceHandler,
            resourcesProvider: ResourcesProvider
        ): GridAdapter {
            return GridAdapter(data, peaceHandler, resourcesProvider)
        }
    }
}
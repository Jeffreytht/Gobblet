package com.jeffreytht.gobblet.di

import com.jeffreytht.gobblet.adapter.GridAdapter
import com.jeffreytht.gobblet.model.Grid
import com.jeffreytht.gobblet.util.PeaceHandler
import com.jeffreytht.gobblet.util.ResourcesProvider
import dagger.Module
import dagger.Provides
import java.util.Stack
import javax.inject.Named
import kotlin.collections.ArrayList

@Module(includes = [UtilModule::class])
interface GridAdapterModule {
    companion object {
        @Provides
        fun providesData(@Named(ROW) row: Int, @Named(COL) col: Int): ArrayList<ArrayList<Grid>> {
            val data = ArrayList<ArrayList<Grid>>()
            for (i in 0 until row) {
                data.add(ArrayList())
                for (j in 0 until col) {
                    data[i].add(Grid(row = i, col = j, peaces = Stack()))
                }
            }
            return data
        }

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
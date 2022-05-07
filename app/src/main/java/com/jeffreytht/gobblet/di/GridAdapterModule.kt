package com.jeffreytht.gobblet.di

import com.jeffreytht.gobblet.adapter.GridAdapter
import com.jeffreytht.gobblet.model.Grid
import com.jeffreytht.gobblet.util.PeaceHandler
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
interface GridAdapterModule {
    companion object {
        @Provides
        fun providesData(@Named(ROW) row: Int, @Named(COL) col: Int): ArrayList<ArrayList<Grid>> {
            val data = ArrayList<ArrayList<Grid>>()
            for (i in 0 until row) {
                data.add(ArrayList())
                for (j in 0 until col) {
                    data[i].add(Grid(row = i, col = j))
                }
            }
            return data
        }

        @Provides
        fun providesGridAdapter(
            data: ArrayList<ArrayList<Grid>>,
            peaceHandler: PeaceHandler
        ): GridAdapter {
            return GridAdapter(data, peaceHandler)
        }
    }
}
package com.jeffreytht.gobblet.di

import android.content.Context
import com.jeffreytht.gobblet.adapter.GridAdapter
import com.jeffreytht.gobblet.model.Grid
import com.jeffreytht.gobblet.util.PeaceHandler
import dagger.BindsInstance
import dagger.Component

@Component(modules = [GridAdapterModule::class])
interface GridAdapterComponent {
    fun providesGridAdapter(): GridAdapter

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun withPeaceHandler(peaceHandler: PeaceHandler): Builder

        @BindsInstance
        fun withData(grids: ArrayList<ArrayList<Grid>>): Builder

        @BindsInstance
        fun withContext(context: Context): Builder

        fun build(): GridAdapterComponent
    }
}
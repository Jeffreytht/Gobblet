package com.jeffreytht.gobblet.di

import android.app.Activity
import com.jeffreytht.gobblet.adapter.GridAdapter
import com.jeffreytht.gobblet.util.PeaceHandler
import dagger.BindsInstance
import dagger.Component
import javax.inject.Named

const val ROW = "ROW"
const val COL = "COL"

@Component(modules = [GridAdapterModule::class])
interface GridAdapterComponent {
    fun providesGridAdapter(): GridAdapter

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun withPeaceHandler(peaceHandler: PeaceHandler): Builder

        @BindsInstance
        fun withRow(@Named(ROW) row: Int): Builder

        @BindsInstance
        fun withCol(@Named(COL) col: Int): Builder

        @BindsInstance
        fun withActivity(activity: Activity): Builder

        fun build(): GridAdapterComponent
    }
}
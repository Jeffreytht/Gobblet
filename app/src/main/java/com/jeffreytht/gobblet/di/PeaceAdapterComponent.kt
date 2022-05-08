package com.jeffreytht.gobblet.di

import android.content.Context
import com.jeffreytht.gobblet.adapter.PeacesAdapter
import com.jeffreytht.gobblet.model.Peace
import com.jeffreytht.gobblet.util.PeaceHandler
import dagger.BindsInstance
import dagger.Component

@Component(modules = [PeaceAdapterModule::class])
interface PeaceAdapterComponent {
    fun providesPeacesAdapter(): PeacesAdapter

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun withPeaceHandler(peaceHandler: PeaceHandler): Builder

        @BindsInstance
        fun withContext(context: Context): Builder

        @BindsInstance
        fun withData(data: ArrayList<Peace>): Builder

        fun build(): PeaceAdapterComponent
    }
}
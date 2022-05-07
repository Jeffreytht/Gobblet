package com.jeffreytht.gobblet.di

import com.jeffreytht.gobblet.adapter.PeacesAdapter
import com.jeffreytht.gobblet.model.Peace
import com.jeffreytht.gobblet.ui.GobbletActivityViewModel
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

        @Provides
        fun providesData(@Peace.Color color: Int): ArrayList<Peace> {
            val dataset = ArrayList<Peace>()
            var scale = 1.0f
            var size = Peace.LARGE
            for (i in 0 until GobbletActivityViewModel.PEACES_COUNT) {
                if (i > 0 && i % 3 == 0) {
                    scale -= GobbletActivityViewModel.SCALE_DIFF
                    size += 1
                }
                dataset.add(
                    Peace(
                        id = i,
                        color = color,
                        scale = scale,
                        size = when (size) {
                            0 -> Peace.LARGE
                            1 -> Peace.MEDIUM
                            2 -> Peace.SMALL
                            else -> Peace.EXTRA_SMALL
                        }
                    )
                )
            }
            return dataset
        }
    }
}
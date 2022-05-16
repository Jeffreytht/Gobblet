package com.jeffreytht.gobblet.di

import androidx.lifecycle.ViewModelProvider
import com.jeffreytht.gobblet.ui.HomeActivity
import com.jeffreytht.gobblet.ui.HomeActivityViewModel
import com.jeffreytht.gobblet.util.GobbletController
import com.jeffreytht.gobblet.util.SettingController
import com.jeffreytht.gobblet.util.SoundUtil
import dagger.Module
import dagger.Provides

@Module
abstract class HomeActivityModule {
    companion object {
        @Provides
        fun providesHomeActivityViewModel(
            homeActivity: HomeActivity,
            settingController: SettingController,
            gobbletController: GobbletController,
            soundUtil: SoundUtil
        ): HomeActivityViewModel {
            return ViewModelProvider(
                homeActivity,
                HomeActivityViewModelFactory(gobbletController, settingController, soundUtil)
            ).get(
                HomeActivityViewModel::class.java
            )
        }

        @Provides
        fun providesGobbletController(homeActivity: HomeActivity): GobbletController {
            return GobbletController(homeActivity)
        }

        @Provides
        fun providesSettingController(homeActivity: HomeActivity): SettingController {
            return SettingController(homeActivity)
        }
    }

    abstract fun contributeHomeActivity(): HomeActivity
}
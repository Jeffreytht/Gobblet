package com.jeffreytht.gobblet.di

import android.app.Activity
import androidx.lifecycle.ViewModelProvider
import com.jeffreytht.gobblet.ui.SettingActivity
import com.jeffreytht.gobblet.ui.SettingActivityViewModel
import com.jeffreytht.gobblet.util.Navigator
import com.jeffreytht.gobblet.util.SharedPreferenceUtil
import com.jeffreytht.gobblet.util.SoundUtil
import dagger.Module
import dagger.Provides

@Module(includes = [NavigatorModule::class])
abstract class SettingActivityModule {
    companion object {
        @Provides
        fun providesSettingActivityViewModel(
            settingActivity: SettingActivity,
            soundUtil: SoundUtil,
            sharedPreferenceUtil: SharedPreferenceUtil,
            navigator: Navigator
        ): SettingActivityViewModel {
            return ViewModelProvider(
                settingActivity,
                SettingActivityViewModelFactory(soundUtil, sharedPreferenceUtil, navigator)
            ).get(SettingActivityViewModel::class.java)
        }

        @Provides
        fun providesActivity(settingActivity: SettingActivity): Activity {
            return settingActivity
        }
    }

    abstract fun contributeSettingActivity(): SettingActivity
}
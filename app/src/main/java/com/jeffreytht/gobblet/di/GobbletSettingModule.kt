package com.jeffreytht.gobblet.di

import com.jeffreytht.gobblet.ui.GameActivity
import com.jeffreytht.gobblet.ui.GameSettingProvider
import dagger.Module
import dagger.Provides

@Module
interface GobbletSettingModule {
    companion object {
        @Provides
        fun providesGobbletSetting(gobbletActivity: GameActivity): GameSettingProvider {
            return gobbletActivity
        }
    }
}
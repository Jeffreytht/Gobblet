package com.jeffreytht.gobblet.di

import com.jeffreytht.gobblet.ui.GameActivity
import com.jeffreytht.gobblet.ui.HomeActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {
    @ContributesAndroidInjector(modules = [HomeActivityModule::class])
    abstract fun bindHomeActivity(): HomeActivity

    @ContributesAndroidInjector(modules = [GobbletActivityModule::class])
    abstract fun bindGobbletActivity(): GameActivity
}
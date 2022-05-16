package com.jeffreytht.gobblet.di

import android.app.Activity
import com.jeffreytht.gobblet.util.Navigator
import dagger.Module
import dagger.Provides

@Module
class NavigatorModule {
    @Provides
    fun providesNavigator(activity: Activity): Navigator {
        return Navigator(activity)
    }
}
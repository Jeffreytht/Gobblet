package com.jeffreytht.gobblet.di

import android.app.Application
import com.jeffreytht.gobblet.MyApp
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        AppModule::class,
        ActivityBuilder::class
    ]
)
interface AppComponent : AndroidInjector<MyApp>, AppDi {
    @Component.Factory
    interface Builder {
        fun create(@BindsInstance application: Application): AppComponent
    }
}

interface AppDi :
    AppDependencies
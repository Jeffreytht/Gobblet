package com.jeffreytht.gobblet

import com.jeffreytht.gobblet.di.AppComponent
import com.jeffreytht.gobblet.di.AppDependencies
import com.jeffreytht.gobblet.di.DaggerAppComponent
import com.jeffreytht.gobblet.util.AdUtil
import com.jeffreytht.gobblet.util.DependencyProvider
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import javax.inject.Inject
import kotlin.reflect.KClass


class MyApp : DaggerApplication(), DependencyProvider {
    @Inject
    lateinit var appComponent: AppComponent
    @Inject
    lateinit var adUtil: AdUtil

    override fun onCreate() {
        super.onCreate()
        adUtil.loadAds()
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        appComponent = DaggerAppComponent.factory().create(this)
        return appComponent
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> extractDependency(cls: KClass<T>): T? {
        return when (cls) {
            AppDependencies::class -> appComponent as T
            else -> null
        }
    }
}
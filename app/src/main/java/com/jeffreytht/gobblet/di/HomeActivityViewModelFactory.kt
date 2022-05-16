package com.jeffreytht.gobblet.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jeffreytht.gobblet.ui.HomeActivityViewModel
import com.jeffreytht.gobblet.util.GobbletController
import com.jeffreytht.gobblet.util.SettingController
import com.jeffreytht.gobblet.util.SoundUtil

class HomeActivityViewModelFactory(
    private val gobbletController: GobbletController,
    private val settingController: SettingController,
    private val soundUtil: SoundUtil
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeActivityViewModel(gobbletController, settingController, soundUtil) as T
    }
}
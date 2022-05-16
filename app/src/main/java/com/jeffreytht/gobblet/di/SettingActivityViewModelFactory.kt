package com.jeffreytht.gobblet.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jeffreytht.gobblet.ui.SettingActivityViewModel
import com.jeffreytht.gobblet.util.Navigator
import com.jeffreytht.gobblet.util.SharedPreferenceUtil
import com.jeffreytht.gobblet.util.SoundUtil

class SettingActivityViewModelFactory(
    private val soundUtil: SoundUtil,
    private val sharedPreferenceUtil: SharedPreferenceUtil,
    private val navigator: Navigator
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SettingActivityViewModel(soundUtil, sharedPreferenceUtil, navigator) as T
    }
}
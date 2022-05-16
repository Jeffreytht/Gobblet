package com.jeffreytht.gobblet.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jeffreytht.gobblet.ui.HomeActivityViewModel
import com.jeffreytht.gobblet.util.GobbletController
import com.jeffreytht.gobblet.util.SoundUtil

class ActivityViewModelFactory(
    private val gobbletController: GobbletController,
    private val soundUtil: SoundUtil
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeActivityViewModel(gobbletController, soundUtil) as T
    }
}
package com.jeffreytht.gobblet.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jeffreytht.gobblet.ui.HomeActivityViewModel
import com.jeffreytht.gobblet.util.GobbletController

class ActivityViewModelFactory(
    private val gobbletController: GobbletController
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeActivityViewModel(gobbletController) as T
    }
}
package com.jeffreytht.gobblet.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jeffreytht.gobblet.ui.GobbletActivityViewModel
import com.jeffreytht.gobblet.util.ResourcesProvider

class GobbletActivityViewModelFactory(
    private val dimension: Int,
    private val context: Context,
    private val resourcesProvider: ResourcesProvider
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return GobbletActivityViewModel(dimension, context, resourcesProvider) as T
    }
}

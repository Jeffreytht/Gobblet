package com.jeffreytht.gobblet.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jeffreytht.gobblet.model.AIPlayer
import com.jeffreytht.gobblet.model.GobbletMode
import com.jeffreytht.gobblet.ui.GobbletActivityViewModel
import com.jeffreytht.gobblet.util.DialogBuilder
import com.jeffreytht.gobblet.util.ResourcesProvider

class GobbletActivityViewModelFactory(
    private val dimension: Int,
    private val context: Context,
    @GobbletMode.Mode private val gobbletMode: Int,
    private val resourcesProvider: ResourcesProvider,
    private val dialogBuilder: DialogBuilder,
    private val aiPlayer: AIPlayer
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return GobbletActivityViewModel(
            dimension,
            context,
            gobbletMode,
            resourcesProvider,
            dialogBuilder,
            aiPlayer
        ) as T
    }
}

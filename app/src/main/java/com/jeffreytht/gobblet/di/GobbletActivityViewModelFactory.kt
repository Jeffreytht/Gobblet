package com.jeffreytht.gobblet.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jeffreytht.gobblet.ui.GobbletActivityViewModel

class GobbletActivityViewModelFactory(private val row: Int, private val col: Int) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return GobbletActivityViewModel(row, col) as T
    }
}

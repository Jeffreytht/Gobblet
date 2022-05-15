package com.jeffreytht.gobblet.util

import com.jeffreytht.gobblet.model.Grid
import com.jeffreytht.gobblet.model.Peace

interface GameInteractor {
    fun movePeace(peace: Peace, grid: Grid)
}
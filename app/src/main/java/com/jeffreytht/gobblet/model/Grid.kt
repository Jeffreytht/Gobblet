package com.jeffreytht.gobblet.model

import androidx.annotation.DrawableRes
import java.util.*

data class Grid(
    val row: Int,
    val col: Int,
    val peaces: Stack<Peace>,
    @DrawableRes var background: Int
)
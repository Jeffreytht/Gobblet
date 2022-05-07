package com.jeffreytht.gobblet.model

import java.util.Stack

data class Grid(
    val row: Int,
    val col: Int,
    val peaces: Stack<Peace>
)
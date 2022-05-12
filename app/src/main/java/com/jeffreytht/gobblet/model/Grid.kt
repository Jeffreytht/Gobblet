package com.jeffreytht.gobblet.model

import androidx.annotation.DrawableRes
import java.util.*

class Grid(
    val row: Int,
    val col: Int,
    val peaces: Stack<Peace>,
    @DrawableRes var background: Int
) {

    fun deepCopy(): Grid {
        val stack = Stack<Peace>()
        val tempStack = Stack<Peace>()

        while (peaces.isNotEmpty()) {
            tempStack.push(peaces.pop())
        }

        while (tempStack.isNotEmpty()) {
            stack.push(tempStack.peek().copy())
            peaces.push(tempStack.pop())
        }

        return Grid(
            row,
            col,
            stack,
            background
        )
    }
}
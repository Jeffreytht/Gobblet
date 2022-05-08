package com.jeffreytht.gobblet.model

import android.util.Log
import com.jeffreytht.gobblet.model.Peace.Companion.GREEN
import com.jeffreytht.gobblet.model.Peace.Companion.RED
import com.jeffreytht.gobblet.ui.GobbletActivityViewModel
import java.util.Stack
import kotlin.collections.ArrayList

class Game(
    val row: Int,
    val col: Int,
) {
    private val gameInteractors = HashSet<GameInteractor>()

    val grids = initGrid()
    val peacesMap = hashMapOf(
        GREEN to initPeaces(GREEN),
        RED to initPeaces(RED)
    )

    fun registerGameInteractor(gameInteractor: Collection<GameInteractor>) {
        gameInteractors.addAll(gameInteractor)
    }

    fun registerGameInteractor(gameInteractor: GameInteractor) {
        gameInteractors.add(gameInteractor)
    }

    fun isValidMove(peace: Peace, grid: Grid): Boolean {
        return if (grid.peaces.isEmpty()) {
            true
        } else {
            peace.size > grid.peaces.peek().size
        }
    }

    fun move(peace: Peace, grid: Grid): Boolean {
        if (!isValidMove(peace, grid)) {
            return false
        }

        Log.d(
            "Gobblet", "Moving peace " + peace.size + " " + peace.color
                    + " to Grid [" + grid.row + "," + grid.col + "]"
        )

        for (i in gameInteractors) {
            i.movePeace(peace, grid)
        }

        return true
    }

    private fun initPeaces(@Peace.Color color: Int): ArrayList<Peace> {
        val dataset = ArrayList<Peace>()
        var scale = 1.0f
        @Peace.Size var size = Peace.LARGE
        for (i in 0 until GobbletActivityViewModel.PEACES_COUNT) {
            if (i > 0 && i % 3 == 0) {
                scale -= GobbletActivityViewModel.SCALE_DIFF
                size--
            }
            dataset.add(
                Peace(
                    id = i,
                    color = color,
                    scale = scale,
                    size = size
                )
            )
        }
        return dataset
    }

    private fun initGrid(): ArrayList<ArrayList<Grid>> {
        val data = ArrayList<ArrayList<Grid>>()
        for (i in 0 until row) {
            data.add(ArrayList())
            for (j in 0 until col) {
                data[i].add(Grid(row = i, col = j, peaces = Stack()))
            }
        }
        return data
    }
}
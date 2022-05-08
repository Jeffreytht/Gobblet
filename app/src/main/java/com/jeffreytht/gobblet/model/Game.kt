package com.jeffreytht.gobblet.model

import android.util.Log
import com.jeffreytht.gobblet.model.Peace.Companion.GREEN
import com.jeffreytht.gobblet.model.Peace.Companion.RED
import com.jeffreytht.gobblet.ui.GobbletActivityViewModel
import java.util.Stack

class Game(
    val dimension: Int
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

    private fun isValidMove(peace: Peace, grid: Grid): Boolean {
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

        Log.d("", "move: " + endMove())
        return true
    }

    private fun endMove(): Boolean {
        // Check row
        for (i in 0 until dimension) {
            var gameOver = true
            for (j in 1 until dimension) {
                if (grids[i][j - 1].peaces.empty() ||
                    grids[i][j].peaces.empty() ||
                    grids[i][j - 1].peaces.peek().color != grids[i][j].peaces.peek().color
                ) {
                    gameOver = false
                    break
                }
            }
            if (gameOver) {
                return true
            }
        }

        // Check col
        for (i in 0 until dimension) {
            var gameOver = true
            for (j in 1 until dimension) {
                if (grids[j - 1][i].peaces.empty() ||
                    grids[j][i].peaces.empty() ||
                    grids[j - 1][i].peaces.peek().color != grids[j][i].peaces.peek().color
                ) {
                    gameOver = false
                    break
                }
            }
            if (gameOver) {
                return true
            }
        }

        // Check diagonal
        var gameOver = true
        for (i in 1 until dimension) {
            if (grids[i - 1][i - 1].peaces.empty() ||
                grids[i][i].peaces.empty() ||
                grids[i][i].peaces.peek().color != grids[i - 1][i - 1].peaces.peek().color
            ) {
                gameOver = false
                break
            }
        }
        if (gameOver) {
            return true
        }

        // Check diagonal
        gameOver = true
        for (i in 1 until dimension) {
            if (grids[i][dimension - i - 1].peaces.empty() ||
                grids[i - 1][dimension - i].peaces.empty() ||
                grids[i][dimension - i - 1].peaces.peek().color !=
                grids[i - 1][dimension - i].peaces.peek().color
            ) {
                gameOver = false
                break
            }
        }

        return gameOver
    }


    private fun initPeaces(@Peace.Color color: Int): ArrayList<Peace> {
        val dataset = ArrayList<Peace>()
        var scale = 1.0f
        @Peace.Size var size = Peace.LARGE
        for (i in 0 until GobbletActivityViewModel.PEACES_COUNT) {
            if (i > 0 && i % (dimension - 1) == 0) {
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
        for (i in 0 until dimension) {
            data.add(ArrayList())
            for (j in 0 until dimension) {
                data[i].add(Grid(row = i, col = j, peaces = Stack()))
            }
        }
        return data
    }
}
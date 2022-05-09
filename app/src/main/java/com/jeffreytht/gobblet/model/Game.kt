package com.jeffreytht.gobblet.model

import com.jeffreytht.gobblet.model.Peace.Companion.GREEN
import com.jeffreytht.gobblet.model.Peace.Companion.RED
import com.jeffreytht.gobblet.ui.GobbletActivityViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.util.*

class Game(
    val dimension: Int,
    @GobbletMode.Mode private val gobbletMode: Int
) {
    private val gameInteractors = HashSet<GameInteractor>()
    private var playerTurnSubject = BehaviorSubject.createDefault(GREEN)
    private val winnerSubject = BehaviorSubject.createDefault(-1)

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

    fun move(peace: Peace, grid: Grid) {
        if (!isValidMove(peace, grid) || winnerSubject.value != -1) {
            return
        }
        for (i in gameInteractors) {
            i.movePeace(peace, grid)
        }
        endMove()
    }

    private fun endMove() {
        val winner = getWinner()
        if (winner != -1) {
            return winnerSubject.onNext(winner)
        }
        playerTurnSubject.onNext(if (playerTurnSubject.value == GREEN) RED else GREEN)
    }

    private fun getWinner(): Int {
        val winner = HashSet<Int>()

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
                winner.add(grids[i][0].peaces.peek().color)
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
                winner.add(grids[0][i].peaces.peek().color)
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
            winner.add(grids[0][0].peaces.peek().color)
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
        if (gameOver) {
            winner.add(grids[0][dimension - 1].peaces.peek().color)
        }

        if (winner.isEmpty()) {
            return -1
        }

        if (winner.size == 1) {
            return winner.first()
        }

        return if (playerTurnSubject.value!! == GREEN) RED else GREEN
    }

    fun getPlayerTurnObservable(): Observable<@Peace.Color Int> {
        return playerTurnSubject.hide()
    }

    fun getWinnerObservable(): Observable<Int> {
        return winnerSubject.hide()
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
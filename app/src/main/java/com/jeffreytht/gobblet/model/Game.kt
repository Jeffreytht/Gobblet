package com.jeffreytht.gobblet.model

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.IntDef
import com.jeffreytht.gobblet.R
import com.jeffreytht.gobblet.model.Peace.Companion.GREEN
import com.jeffreytht.gobblet.model.Peace.Companion.RED
import com.jeffreytht.gobblet.util.GameInteractor
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.util.*
import kotlin.math.pow

class Game(private val gameSetting: GameSetting) {
    companion object {
        const val SINGLE_PLAYER = 1
        const val TWO_PLAYERS = 2
    }

    @Target(AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.TYPE)
    @IntDef(SINGLE_PLAYER, TWO_PLAYERS)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Mode

    private val gameInteractors = HashSet<GameInteractor>()
    private var playerTurnSubject = BehaviorSubject.createDefault(GREEN)
    private val winnerSubject = BehaviorSubject.createDefault(Winner.NO_WINNER)
    val grids = ArrayList<ArrayList<Grid>>()
    val peaces = hashMapOf<Int, ArrayList<Peace>>(
        GREEN to ArrayList(),
        RED to ArrayList()
    )

    init {
        reset()
    }

    fun reset() {
        grids.clear()
        grids.addAll(initGrid())
        peaces[GREEN]?.clear()
        peaces[GREEN]?.addAll(initPeaces(GREEN, R.drawable.ic_green_large_peace))
        peaces[RED]?.clear()
        peaces[RED]?.addAll(initPeaces(RED, R.drawable.ic_red_large_peace))
        playerTurnSubject.onNext(GREEN)
        winnerSubject.onNext(Winner.NO_WINNER)
    }

    fun registerGameInteractor(gameInteractor: Collection<GameInteractor>) {
        gameInteractors.addAll(gameInteractor)
    }

    fun registerGameInteractor(gameInteractor: GameInteractor) {
        gameInteractors.add(gameInteractor)
    }

    private fun isValidMove(peace: Peace, grid: Grid): Boolean {
        return grid.peaces.isEmpty() || peace.size > grid.peaces.peek().size
    }

    fun move(peace: Peace, grid: Grid): Boolean {
        if (!isValidMove(peace, grid) || winnerSubject.value != Winner.NO_WINNER) {
            return false
        }
        for (i in gameInteractors) {
            i.movePeace(peace, grid)
        }
        endMove()
        return true
    }

    private fun endMove() {
        val winner = getWinner()
        if (winner != Winner.NO_WINNER) {
            return winnerSubject.onNext(winner)
        }
        playerTurnSubject.onNext(if (playerTurnSubject.value == GREEN) RED else GREEN)
    }

    private fun getWinner(): Winner {
        val winner = HashMap<@Peace.Color Int, Winner>()

        // Check row
        for (i in 0 until gameSetting.dimension) {
            var gameOver = true
            for (j in 1 until gameSetting.dimension) {
                if (grids[i][j - 1].peaces.empty() ||
                    grids[i][j].peaces.empty() ||
                    grids[i][j - 1].peaces.peek().color != grids[i][j].peaces.peek().color
                ) {
                    gameOver = false
                    break
                }
            }
            if (gameOver) {
                winner[grids[i][0].peaces.peek().color] =
                    Winner(
                        color = grids[i][0].peaces.peek().color,
                        line = Winner.ROW,
                        idx = i
                    )
            }
        }

        // Check col
        for (i in 0 until gameSetting.dimension) {
            var gameOver = true
            for (j in 1 until gameSetting.dimension) {
                if (grids[j - 1][i].peaces.empty() ||
                    grids[j][i].peaces.empty() ||
                    grids[j - 1][i].peaces.peek().color != grids[j][i].peaces.peek().color
                ) {
                    gameOver = false
                    break
                }
            }
            if (gameOver) {
                winner[grids[0][i].peaces.peek().color] =
                    Winner(
                        color = grids[0][i].peaces.peek().color,
                        line = Winner.COL,
                        idx = i
                    )
            }
        }

        // Check diagonal
        var gameOver = true
        for (i in 1 until gameSetting.dimension) {
            if (grids[i - 1][i - 1].peaces.empty() ||
                grids[i][i].peaces.empty() ||
                grids[i][i].peaces.peek().color != grids[i - 1][i - 1].peaces.peek().color
            ) {
                gameOver = false
                break
            }
        }
        if (gameOver) {
            winner[grids[0][0].peaces.peek().color] =
                Winner(
                    color = grids[0][0].peaces.peek().color,
                    line = Winner.LEFT_DIAGONAL,
                    idx = 0
                )
        }

        // Check diagonal
        gameOver = true
        for (i in 1 until gameSetting.dimension) {
            if (grids[i][gameSetting.dimension - i - 1].peaces.empty() ||
                grids[i - 1][gameSetting.dimension - i].peaces.empty() ||
                grids[i][gameSetting.dimension - i - 1].peaces.peek().color !=
                grids[i - 1][gameSetting.dimension - i].peaces.peek().color
            ) {
                gameOver = false
                break
            }
        }
        if (gameOver) {
            winner[grids[0][gameSetting.dimension - 1].peaces.peek().color] =
                Winner(
                    color = grids[0][gameSetting.dimension - 1].peaces.peek().color,
                    line = Winner.RIGHT_DIAGONAL,
                    idx = 0
                )
        }

        if (winner.isEmpty()) {
            return Winner.NO_WINNER
        }

        if (winner.size == 1) {
            return winner[winner.keys.first()]!!
        }

        return if (playerTurnSubject.value!! == GREEN) winner[RED]!! else winner[GREEN]!!
    }

    fun getPlayerTurnObservable(): Observable<@Peace.Color Int> {
        return playerTurnSubject.hide()
    }

    fun getWinnerObservable(): Observable<Winner> {
        return winnerSubject.hide()
    }

    @SuppressLint("WrongConstant")
    private fun initPeaces(@Peace.Color color: Int, @DrawableRes res: Int): ArrayList<Peace> {
        val dataset = ArrayList<Peace>()
        @Peace.Size var size = 10f.pow(gameSetting.dimension - 1).toInt()
        for (i in 0 until gameSetting.dimension * (gameSetting.dimension - 1)) {
            if (i > 0 && i % (gameSetting.dimension - 1) == 0) {
                size /= 10
            }
            dataset.add(
                Peace(
                    id = i,
                    color = color,
                    size = size,
                    resId = res
                )
            )
        }
        return dataset
    }

    private fun initGrid(): ArrayList<ArrayList<Grid>> {
        val data = ArrayList<ArrayList<Grid>>()
        for (i in 0 until gameSetting.dimension) {
            data.add(ArrayList())
            for (j in 0 until gameSetting.dimension) {
                data[i].add(
                    Grid(
                        row = i,
                        col = j,
                        peaces = Stack(),
                        background = R.drawable.bg_grid_border
                    )
                )
            }
        }
        return data
    }
}
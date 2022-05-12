package com.jeffreytht.gobblet.model

import androidx.annotation.IntDef
import io.reactivex.rxjava3.core.Single
import kotlin.math.max
import kotlin.math.min

class AIPlayer(
    @Peace.Color val aiColor: Int,
    @Peace.Color private val playerColor: Int,
    @Difficulty private val difficulty: Int
) {
    companion object {
        const val EASY = 1
        const val MEDIUM = 3
        const val HARD = 5
        const val AI_WIN = 10
        const val AI_LOSE = -10
        const val TIE = 0
    }

    @IntDef(EASY, MEDIUM, HARD)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Difficulty

    private fun canMovePeace(peace: Peace, dst: Grid): Boolean {
        return dst.peaces.isEmpty() || peace.size > dst.peaces.peek().size
    }

    fun getNextMove(
        grids: ArrayList<ArrayList<Grid>>,
        peaces: HashMap<@Peace.Color Int, ArrayList<Peace>>,
    ): Single<Pair<Peace, Grid>> {
        val visited = HashMap<Peace, Boolean>()
        val dimension = grids.size
        var maxEval = Int.MIN_VALUE
        var move: Pair<Peace, Grid>? = null

        for (i in 0 until dimension) {
            for (j in 0 until dimension) {
                for (peace in peaces[aiColor]!!) {
                    if (visited[peace] == true || !canMovePeace(peace, grids[i][j])) {
                        continue
                    }

                    grids[i][j].peaces.push(peace)
                    visited[peace] = true
                    val eval =
                        minimax(
                            grids,
                            peaces,
                            difficulty - 1,
                            playerColor,
                            maxEval,
                            Int.MAX_VALUE,
                            visited
                        )
                    visited[peace] = false
                    grids[i][j].peaces.pop()

                    if (eval > maxEval) {
                        move = Pair(peace, grids[i][j])
                        maxEval = eval
                    }
                }

                for (k in 0 until dimension) {
                    for (l in 0 until dimension) {
                        if (i == k && j == l ||
                            grids[k][l].peaces.isEmpty() ||
                            grids[k][l].peaces.peek().color != aiColor ||
                            !canMovePeace(grids[k][l].peaces.peek(), grids[i][j])
                        ) {
                            continue
                        }

                        grids[i][j].peaces.push(grids[k][l].peaces.pop())
                        val eval =
                            minimax(
                                grids,
                                peaces,
                                difficulty - 1,
                                playerColor,
                                maxEval,
                                Int.MAX_VALUE,
                                visited
                            )
                        grids[k][l].peaces.push(grids[i][j].peaces.pop())

                        if (eval > maxEval) {
                            move = Pair(grids[k][l].peaces.peek(), grids[i][j])
                            maxEval = eval
                        }
                    }
                }
            }
        }

        return if (move == null) {
            Single.just(generateRandomMove(grids, peaces)!!)
        } else {
            Single.just(move)
        }
    }

    private fun generateRandomMove(
        grids: ArrayList<java.util.ArrayList<Grid>>,
        peaces: HashMap<@Peace.Color Int, java.util.ArrayList<Peace>>
    ): Pair<Peace, Grid>? {
        val dimension = grids.size
        for (i in 0 until dimension) {
            for (j in 0 until dimension) {
                for (peace in peaces[aiColor]!!) {
                    if (!canMovePeace(peace, grids[i][j])) {
                        continue
                    }
                    return Pair(peace, grids[i][j])
                }
                for (k in 0 until dimension) {
                    for (l in 0 until dimension) {
                        if (i == k && j == l ||
                            !canMovePeace(grids[k][l].peaces.peek(), grids[i][j])
                        ) {
                            continue
                        }
                        return Pair(grids[k][l].peaces.peek(), grids[i][j])
                    }
                }
            }
        }
        return null
    }

    private fun minimax(
        grids: ArrayList<ArrayList<Grid>>,
        peaces: HashMap<@Peace.Color Int, ArrayList<Peace>>,
        depth: Int,
        @Peace.Color turn: Int,
        maxScore: Int,
        minScore: Int,
        visited: HashMap<Peace, Boolean>
    ): Int {
        val dimension = grids.size
        val score = getScore(grids)
        if (depth == 0 || score != TIE) {
            return score
        }

        if (turn == aiColor) {
            var mScore = maxScore
            var maxEval = Int.MIN_VALUE
            for (i in 0 until dimension) {
                for (j in 0 until dimension) {
                    for (peace in peaces[aiColor]!!) {
                        if (visited[peace] == true || !canMovePeace(peace, grids[i][j])) {
                            continue
                        }
                        grids[i][j].peaces.push(peace)
                        visited[peace] = true
                        val eval =
                            minimax(
                                grids,
                                peaces,
                                depth - 1,
                                playerColor,
                                mScore,
                                minScore,
                                visited
                            )
                        visited[peace] = false
                        grids[i][j].peaces.pop()

                        maxEval = max(maxEval, eval)
                        mScore = max(mScore, eval)

                        if (minScore <= mScore) {
                            return maxEval
                        }
                    }

                    for (k in 0 until dimension) {
                        for (l in 0 until dimension) {
                            if (i == k && j == l ||
                                grids[k][l].peaces.isEmpty() ||
                                grids[k][l].peaces.peek().color != aiColor ||
                                !canMovePeace(grids[k][l].peaces.peek(), grids[i][j])
                            ) {
                                continue
                            }

                            grids[i][j].peaces.push(grids[k][l].peaces.pop())
                            val eval =
                                minimax(
                                    grids,
                                    peaces,
                                    depth - 1,
                                    playerColor,
                                    mScore,
                                    minScore,
                                    visited
                                )
                            grids[k][l].peaces.push(grids[i][j].peaces.pop())

                            maxEval = max(maxEval, eval)
                            mScore = max(mScore, eval)

                            if (minScore <= mScore) {
                                return maxEval
                            }
                        }
                    }
                }
            }
            return maxEval
        } else {
            var mScore = minScore
            var minEval = Int.MAX_VALUE
            for (i in 0 until dimension) {
                for (j in 0 until dimension) {
                    for (peace in peaces[playerColor]!!) {
                        if (visited[peace] == true || !canMovePeace(peace, grids[i][j])) {
                            continue
                        }
                        grids[i][j].peaces.push(peace)
                        visited[peace] = true
                        val eval =
                            minimax(
                                grids,
                                peaces,
                                depth - 1,
                                aiColor,
                                maxScore,
                                mScore,
                                visited
                            )
                        visited[peace] = false
                        grids[i][j].peaces.pop()

                        minEval = min(minEval, eval)
                        mScore = min(mScore, eval)

                        if (mScore <= maxScore) {
                            return minEval
                        }
                    }

                    for (k in 0 until dimension) {
                        for (l in 0 until dimension) {
                            if (i == k && j == l ||
                                grids[k][l].peaces.isEmpty() ||
                                grids[k][l].peaces.peek().color != playerColor ||
                                !canMovePeace(grids[k][l].peaces.peek(), grids[i][j])
                            ) {
                                continue
                            }

                            grids[i][j].peaces.push(grids[k][l].peaces.pop())
                            val eval =
                                minimax(
                                    grids,
                                    peaces,
                                    depth - 1,
                                    aiColor,
                                    maxScore,
                                    mScore,
                                    visited
                                )
                            grids[k][l].peaces.push(grids[i][j].peaces.pop())

                            minEval = min(minEval, eval)
                            mScore = min(mScore, eval)

                            if (mScore <= maxScore) {
                                return minEval
                            }
                        }
                    }
                }
            }
            return minEval
        }
    }

    private fun getScore(grids: ArrayList<ArrayList<Grid>>): Int {
        val dimension = grids.size

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
                return if (grids[i][0].peaces.peek().color == aiColor) {
                    AI_WIN
                } else {
                    AI_LOSE
                }
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
                return if (grids[0][i].peaces.peek().color == aiColor) {
                    AI_WIN
                } else {
                    AI_LOSE
                }
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
            return if (grids[0][0].peaces.peek().color == aiColor) {
                AI_WIN
            } else {
                AI_LOSE
            }
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
            return if (grids[0][dimension - 1].peaces.peek().color == aiColor) {
                AI_WIN
            } else {
                AI_LOSE
            }
        }
        return TIE
    }
}
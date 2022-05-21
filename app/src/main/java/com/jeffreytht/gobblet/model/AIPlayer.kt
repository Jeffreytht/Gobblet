package com.jeffreytht.gobblet.model

import androidx.annotation.IntDef
import com.jeffreytht.gobblet.model.Peace.Companion.NO_COLOR
import io.reactivex.rxjava3.core.Single
import kotlin.math.pow

class AIPlayer(gameSetting: GameSetting) {
    companion object {
        const val EASY = 1
        const val MEDIUM = 2
        const val HARD = 3
        const val AI_WIN = Int.MAX_VALUE
        const val AI_LOSE = Int.MIN_VALUE
    }

    @Target(AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.TYPE)
    @IntDef(EASY, MEDIUM, HARD)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Difficulty

    @Peace.Color
    val aiColor: Int

    @Peace.Color
    private val playerColor: Int

    @Difficulty
    private val difficulty: Int
    private val dimension: Int
    private val cache =
        HashMap<Int, HashMap<Int, HashMap<Int, HashMap<Int, HashMap<String, Int>>>>>()

    init {
        gameSetting.let {
            aiColor = it.player2Color
            playerColor = it.player1Color
            difficulty = it.difficulty
            dimension = it.dimension
        }
    }

    private fun canMovePeace(peace: Peace, dst: Grid): Boolean {
        return dst.peaces.isEmpty() || peace.size > dst.peaces.peek().size
    }

    /**
     * Encode the stack of every grids into Int
     * For example:
     *          TOP
     *           v
     * Stack -> [GREEN(LARGE), RED(MEDIUM), GREEN(EXTRA_SMALL)]
     *
     * Let:
     * GREEN = 1
     * RED = 2
     *
     * Integer -> 1201
     */
    private fun encodeGrid(grids: ArrayList<ArrayList<Grid>>): ArrayList<ArrayList<Int>> {
        val newGrids = ArrayList<ArrayList<Grid>>()
        for (i in 0 until dimension) {
            newGrids.add(ArrayList())
            for (j in 0 until dimension) {
                newGrids[i].add(grids[i][j].deepCopy())
            }
        }

        val res = ArrayList<ArrayList<Int>>()
        for (i in 0 until dimension) {
            res.add(ArrayList())
            for (j in 0 until dimension) {
                var encodedInt = 0
                while (newGrids[i][j].peaces.isNotEmpty()) {
                    val peace = newGrids[i][j].peaces.pop()
                    encodedInt += peace.size * peace.color
                }
                res[i].add(encodedInt)
            }
        }
        return res
    }

    private fun encodeGrids(grids: ArrayList<ArrayList<Int>>): String {
        val newEGrids = StringBuilder()
        for (i in 0 until dimension) {
            for (j in 0 until dimension) {
                newEGrids.append(grids[i][j].toString().padStart(dimension, '0'))
            }
        }
        return newEGrids.toString()
    }

    private fun encodePeaces(peaces: ArrayList<Peace>): Int {
        return peaces.sumOf { it.size }
    }

    private fun canMove(peace: Int, grid: Int): Boolean {
        return peace > grid
    }

    @Deprecated("")
    private fun checkCache(
        turn: Int,
        depth: Int,
        aiPeaces: Int,
        playerPeaces: Int,
        grids: ArrayList<ArrayList<Int>>
    ): Pair<Boolean, Int> {
        cache[turn]?.get(depth)?.get(aiPeaces)?.get(playerPeaces)?.get(encodeGrids(grids))?.let {
            return Pair(true, it)
        }
        return Pair(false, -1)
    }

    @Deprecated("")
    private fun putCache(
        turn: Int,
        depth: Int,
        aiPeaces: Int,
        playerPeaces: Int,
        eGrids: ArrayList<ArrayList<Int>>,
        score: Int
    ): Int {
        cache
            .getOrPut(turn) { HashMap() }
            .getOrPut(depth) { HashMap() }
            .getOrPut(aiPeaces) { HashMap() }
            .getOrPut(playerPeaces) { HashMap() }[encodeGrids(eGrids)] = score
        return score
    }

    private fun getScore(grids: ArrayList<ArrayList<Int>>): Int {
        val gridsColor = ArrayList<ArrayList<@Peace.Color Int>>()
        val gridsSize = ArrayList<ArrayList<Int>>()
        for (i in 0 until dimension) {
            gridsColor.add(ArrayList())
            gridsSize.add(ArrayList())
            for (j in 0 until dimension) {
                val (color, size) = getFirstPeace(grids[i][j])
                gridsColor[i].add(color)
                gridsSize[i].add(size)
            }
        }

        // Check row
        for (i in 0 until dimension) {
            var gameOver = true
            for (j in 1 until dimension) {
                if (gridsColor[i][j - 1] == NO_COLOR ||
                    gridsColor[i][j] == NO_COLOR ||
                    gridsColor[i][j - 1] != gridsColor[i][j]
                ) {
                    gameOver = false
                    break
                }
            }
            if (gameOver) {
                return if (gridsColor[i][0] == aiColor) {
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
                if (gridsColor[j - 1][i] == NO_COLOR ||
                    gridsColor[j][i] == NO_COLOR ||
                    gridsColor[j - 1][i] != gridsColor[j][i]
                ) {
                    gameOver = false
                    break
                }
            }
            if (gameOver) {
                return if (gridsColor[0][i] == aiColor) {
                    AI_WIN
                } else {
                    AI_LOSE
                }
            }
        }

        // Check diagonal
        var gameOver = true
        for (i in 1 until dimension) {
            if (gridsColor[i - 1][i - 1] == NO_COLOR ||
                gridsColor[i][i] == NO_COLOR ||
                gridsColor[i][i] != gridsColor[i - 1][i - 1]
            ) {
                gameOver = false
                break
            }
        }
        if (gameOver) {
            return if (gridsColor[0][0] == aiColor) {
                AI_WIN
            } else {
                AI_LOSE
            }
        }

        // Check diagonal
        gameOver = true
        for (i in 1 until dimension) {
            if (gridsColor[i][dimension - i - 1] == NO_COLOR ||
                gridsColor[i - 1][dimension - i] == NO_COLOR ||
                gridsColor[i][dimension - i - 1] !=
                gridsColor[i - 1][dimension - i]
            ) {
                gameOver = false
                break
            }
        }
        if (gameOver) {
            return if (gridsColor[0][dimension - 1] == aiColor) {
                AI_WIN
            } else {
                AI_LOSE
            }
        }

        // TIE
        var score = 0
        for (i in 0 until dimension) {
            for (j in 0 until dimension) {
                if (gridsColor[i][j] == NO_COLOR) {
                    continue
                }
                if (gridsColor[i][j] == aiColor) {
                    score += gridsSize[i][j]
                } else {
                    score -= gridsSize[i][j]
                }
            }
        }
        return score
    }

    private fun minimax(
        eGrids: ArrayList<ArrayList<Int>>,
        aiPeaces: Int,
        playerPeaces: Int,
        depth: Int,
        @Peace.Color turn: Int,
        maxScore: Int,
        minScore: Int
    ): Pair<Int, Int> {
        val score = getScore(eGrids)
        if (depth == 0 || score == AI_LOSE || score == AI_WIN) {
            return Pair(depth, score)
        }

        if (turn == aiColor) {
            var mScore = maxScore
            var maxEval = Pair(0, Int.MIN_VALUE)
            for (i in 0 until dimension) {
                for (j in 0 until dimension) {
                    for (k in dimension - 1 downTo 0) {
                        val multiplier = 10f.pow(k).toInt()
                        if (aiPeaces / multiplier % 10 == 0 || !canMove(multiplier, eGrids[i][j])) {
                            continue
                        }

                        eGrids[i][j] += multiplier * aiColor
                        val eval = minimax(
                            eGrids,
                            aiPeaces - multiplier,
                            playerPeaces,
                            depth - 1,
                            playerColor,
                            mScore,
                            minScore
                        )
                        eGrids[i][j] -= multiplier * aiColor

                        if (eval.second > maxEval.second ||
                            eval.second == maxEval.second && eval.first > maxEval.first
                        ) {
                            maxEval = eval
                            mScore = maxEval.second
                        }

                        if (minScore <= mScore) {
                            return maxEval
                        }
                    }

                    for (k in 0 until dimension) {
                        for (l in 0 until dimension) {
                            if (i == k && j == l || eGrids[k][l] == 0) {
                                continue
                            }
                            val (color, size) = getFirstPeace(eGrids[k][l])
                            if (color != aiColor || !canMove(size, eGrids[i][j])) {
                                continue
                            }

                            eGrids[k][l] -= aiColor * size
                            eGrids[i][j] += aiColor * size
                            val eval = minimax(
                                eGrids,
                                aiPeaces,
                                playerPeaces,
                                depth - 1,
                                playerColor,
                                mScore,
                                minScore
                            )
                            eGrids[k][l] += aiColor * size
                            eGrids[i][j] -= aiColor * size

                            if (eval.second > maxEval.second ||
                                eval.second == maxEval.second && eval.first > maxEval.first
                            ) {
                                maxEval = eval
                                mScore = maxEval.second
                            }

                            if (minScore <= mScore) {
                                return maxEval
                            }
                        }
                    }
                }
            }
            return maxEval
        }

        var mScore = minScore
        var minEval = Pair(0, Int.MAX_VALUE)
        for (i in 0 until dimension) {
            for (j in 0 until dimension) {
                for (k in dimension - 1 downTo 0) {
                    val multiplier = 10f.pow(k).toInt()
                    if (playerPeaces / multiplier % 10 == 0 || !canMove(multiplier, eGrids[i][j])) {
                        continue
                    }

                    eGrids[i][j] += multiplier * playerColor
                    val eval = minimax(
                        eGrids,
                        aiPeaces,
                        playerPeaces - multiplier,
                        depth - 1,
                        aiColor,
                        maxScore,
                        mScore
                    )
                    eGrids[i][j] -= multiplier * playerColor

                    if (eval.second < minEval.second ||
                        eval.second == minEval.second && eval.first > minEval.first
                    ) {
                        minEval = eval
                        mScore = minEval.second
                    }

                    if (mScore <= maxScore) {
                        return minEval
                    }
                }

                for (k in 0 until dimension) {
                    for (l in 0 until dimension) {
                        if (i == k && j == l || eGrids[k][l] == 0) {
                            continue
                        }
                        val (color, size) = getFirstPeace(eGrids[k][l])
                        if (color != playerColor || !canMove(size, eGrids[i][j])) {
                            continue
                        }

                        eGrids[k][l] -= playerColor * size
                        eGrids[i][j] += playerColor * size
                        val eval = minimax(
                            eGrids,
                            aiPeaces,
                            playerPeaces,
                            depth - 1,
                            aiColor,
                            maxScore,
                            mScore
                        )
                        eGrids[k][l] += playerColor * size
                        eGrids[i][j] -= playerColor * size

                        if (eval.second < minEval.second ||
                            eval.second == minEval.second && eval.first > minEval.first
                        ) {
                            minEval = eval
                            mScore = minEval.second
                        }

                        if (mScore <= maxScore) {
                            return minEval
                        }
                    }
                }
            }
        }
        return minEval
    }

    private fun getFirstPeace(num: Int): Pair<@Peace.Color Int, Int> {
        var multiplier = 1
        while (num / multiplier > 9) {
            multiplier *= 10
        }
        return Pair(num / multiplier, multiplier)
    }

    fun getNextMove(
        grids: ArrayList<ArrayList<Grid>>,
        peaces: HashMap<@Peace.Color Int, ArrayList<Peace>>
    ): Single<Pair<Peace, Grid>> {
        val eGrids = encodeGrid(grids)
        val aiPeaces = encodePeaces(peaces[aiColor]!!)
        val playerPeaces = encodePeaces(peaces[playerColor]!!)

        var maxEval = Pair(0, Int.MIN_VALUE)
        val moves = ArrayList<Pair<Peace, Grid>>()

        for (i in 0 until dimension) {
            for (j in 0 until dimension) {
                for (k in dimension - 1 downTo 0) {
                    val multiplier = 10f.pow(k).toInt()
                    if (aiPeaces / multiplier % 10 == 0 || !canMove(multiplier, eGrids[i][j])) {
                        continue
                    }

                    eGrids[i][j] += multiplier * aiColor
                    val eval = minimax(
                        eGrids,
                        aiPeaces - multiplier,
                        playerPeaces,
                        difficulty - 1,
                        playerColor,
                        Int.MIN_VALUE,
                        Int.MAX_VALUE
                    )
                    eGrids[i][j] -= multiplier * aiColor

                    if (eval.second < maxEval.second) {
                        continue
                    }

                    if (eval.second > maxEval.second || eval.first > maxEval.first) {
                        moves.clear()
                        maxEval = eval
                    }

                    moves.add(
                        Pair(
                            peaces[aiColor]!!.find { it.size == multiplier }!!,
                            grids[i][j]
                        )
                    )
                }

                for (k in 0 until dimension) {
                    for (l in 0 until dimension) {
                        if (i == k && j == l || eGrids[k][l] == 0) {
                            continue
                        }
                        val (color, size) = getFirstPeace(eGrids[k][l])
                        if (color != aiColor || !canMove(size, eGrids[i][j])) {
                            continue
                        }

                        eGrids[k][l] -= aiColor * size
                        eGrids[i][j] += aiColor * size
                        val eval = minimax(
                            eGrids,
                            aiPeaces,
                            playerPeaces,
                            difficulty - 1,
                            playerColor,
                            Int.MIN_VALUE,
                            Int.MAX_VALUE
                        )
                        eGrids[k][l] += aiColor * size
                        eGrids[i][j] -= aiColor * size

                        if (eval.second < maxEval.second) {
                            continue
                        }

                        if (eval.second > maxEval.second || eval.first > maxEval.first) {
                            moves.clear()
                            maxEval = eval
                        }

                        moves.add(Pair(grids[k][l].peaces.peek(), grids[i][j]))
                    }
                }
            }
        }

        return if (moves.isEmpty()) {
            Single.just(generateRandomMove(grids, peaces)!!)
        } else {
            Single.just(moves[(Math.random() * moves.size).toInt()])
        }
    }

    private fun generateRandomMove(
        grids: ArrayList<ArrayList<Grid>>,
        peaces: HashMap<@Peace.Color Int, ArrayList<Peace>>
    ): Pair<Peace, Grid>? {
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
                            grids[k][l].peaces.isEmpty() ||
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
}
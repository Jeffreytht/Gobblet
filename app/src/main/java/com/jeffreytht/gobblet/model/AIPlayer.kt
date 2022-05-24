package com.jeffreytht.gobblet.model

import androidx.annotation.IntDef
import com.jeffreytht.gobblet.model.Peace.Companion.NO_COLOR
import io.reactivex.rxjava3.core.Single

class AIPlayer(gameSetting: GameSetting) {
    companion object {
        const val EASY = 2
        const val MEDIUM = 3
        const val HARD = 4
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
        val newGrids = ArrayList<ArrayList<Grid>>(dimension)
        for (i in 0 until dimension) {
            newGrids.add(ArrayList(dimension))
            for (j in 0 until dimension) {
                newGrids[i].add(grids[i][j].deepCopy())
            }
        }

        val res = ArrayList<ArrayList<Int>>(dimension)
        for (i in 0 until dimension) {
            res.add(ArrayList(dimension))
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

    private fun encodePeaces(peaces: ArrayList<Peace>): Int {
        return peaces.sumOf { it.size }
    }

    private fun canMove(peace: Int, grid: Int): Boolean {
        return peace > grid
    }

    private fun getScore(grids: ArrayList<ArrayList<Int>>): Int {
        val gridsColor = ArrayList<ArrayList<@Peace.Color Int>>(dimension)
        val gridsSize = ArrayList<ArrayList<Int>>(dimension)
        var score = 0

        for (i in 0 until dimension) {
            gridsColor.add(ArrayList(dimension))
            gridsSize.add(ArrayList(dimension))
            for (j in 0 until dimension) {
                val (color, size) = getFirstPeace(grids[i][j])
                gridsColor[i].add(color)
                gridsSize[i].add(size)
                when (color) {
                    aiColor -> score += size
                    playerColor -> score -= size
                }
            }
        }

        for (i in 0 until dimension) {
            // Row
            for (j in 1 until dimension) {
                val currGridColor = gridsColor[i][j]
                if (gridsColor[i][j - 1] != currGridColor || currGridColor == NO_COLOR) {
                    break
                }

                if (j == dimension - 1) {
                    when (currGridColor) {
                        aiColor -> return AI_WIN
                        playerColor -> return AI_LOSE
                    }
                }
            }

            // Col
            for (j in 1 until dimension) {
                val currGridColor = gridsColor[j][i]
                if (gridsColor[j - 1][i] != currGridColor || currGridColor == NO_COLOR) {
                    break
                }

                if (j == dimension - 1) {
                    when (currGridColor) {
                        aiColor -> return AI_WIN
                        playerColor -> return AI_LOSE
                    }
                }
            }

            // Check diagonal
            if (i == 0) {
                for (j in 1 until dimension) {
                    val currGridColor = gridsColor[j][j]
                    if (gridsColor[j - 1][j - 1] != currGridColor || currGridColor == NO_COLOR) {
                        break
                    }

                    if (j == dimension - 1) {
                        when (currGridColor) {
                            aiColor -> return AI_WIN
                            playerColor -> return AI_LOSE
                        }
                    }
                }

                for (j in 1 until dimension) {
                    val currGridColor = gridsColor[j][dimension - j - 1]
                    if (gridsColor[j - 1][dimension - j] != currGridColor || currGridColor == NO_COLOR) {
                        break
                    }

                    if (j == dimension - 1) {
                        when (currGridColor) {
                            aiColor -> return AI_WIN
                            playerColor -> return AI_LOSE
                        }
                    }
                }
            }
        }

        // TIE
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
                    if (aiPeaces > 0) {
                        val (_, size) = getFirstPeace(aiPeaces)
                        if (canMove(size, eGrids[i][j])) {
                            eGrids[i][j] += size * aiColor
                            val eval = minimax(
                                eGrids,
                                aiPeaces - size,
                                playerPeaces,
                                depth - 1,
                                playerColor,
                                maxEval.second,
                                minScore
                            )
                            eGrids[i][j] -= size * aiColor

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
                if (playerPeaces > 0) {
                    val (_, size) = getFirstPeace(playerPeaces)
                    if (canMove(size, eGrids[i][j])) {
                        eGrids[i][j] += size * playerColor
                        val eval = minimax(
                            eGrids,
                            aiPeaces,
                            playerPeaces - size,
                            depth - 1,
                            aiColor,
                            maxScore,
                            mScore
                        )
                        eGrids[i][j] -= size * playerColor

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
                if (aiPeaces > 0) {
                    val (_, size) = getFirstPeace(aiPeaces)
                    if (canMove(size, eGrids[i][j])) {
                        eGrids[i][j] += size * aiColor
                        val eval = minimax(
                            eGrids,
                            aiPeaces - size,
                            playerPeaces,
                            difficulty - 1,
                            playerColor,
                            Int.MIN_VALUE,
                            Int.MAX_VALUE
                        )
                        eGrids[i][j] -= size * aiColor

                        if (eval.second >= maxEval.second) {
                            if (eval.second > maxEval.second || eval.first > maxEval.first) {
                                moves.clear()
                                maxEval = eval
                            }
                            moves.add(
                                Pair(
                                    peaces[aiColor]!!.find { it.size == size }!!,
                                    grids[i][j]
                                )
                            )
                        }
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
package com.jeffreytht.gobblet.model

import com.jeffreytht.gobblet.model.AIPlayer.Companion.EASY
import com.jeffreytht.gobblet.model.Game.Companion.SINGLE_PLAYER
import com.jeffreytht.gobblet.model.Peace.Companion.GREEN
import com.jeffreytht.gobblet.model.Peace.Companion.RED

data class GameSetting(
    var dimension: Int = 0,
    var mode: @Game.Mode Int = SINGLE_PLAYER,
    var player1Color: @Peace.Color Int = GREEN,
    var player2Color: @Peace.Color Int = RED,
    var difficulty: @AIPlayer.Difficulty Int = EASY
)
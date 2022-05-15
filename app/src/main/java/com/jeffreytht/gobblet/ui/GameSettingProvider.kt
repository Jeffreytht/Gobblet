package com.jeffreytht.gobblet.ui

import com.jeffreytht.gobblet.model.GameSetting

interface GameSettingProvider {
    fun getGameSetting(): GameSetting
}
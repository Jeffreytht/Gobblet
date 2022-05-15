package com.jeffreytht.gobblet.ui

import android.os.Bundle
import com.google.android.gms.ads.MobileAds
import com.jeffreytht.gobblet.databinding.GameActivityBinding
import com.jeffreytht.gobblet.model.AIPlayer.Companion.HARD
import com.jeffreytht.gobblet.model.Game
import com.jeffreytht.gobblet.model.GameSetting
import com.jeffreytht.gobblet.model.Peace.Companion.GREEN
import com.jeffreytht.gobblet.model.Peace.Companion.RED
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class GameActivity : DaggerAppCompatActivity(), GameSettingProvider {
    companion object {
        const val GOBBLET_MODE = "GOBBLET_MODE"
    }

    @Inject
    lateinit var viewModel: GameActivityViewModel
    private lateinit var binding: GameActivityBinding
    private var gameSetting = GameSetting()

    override fun onCreate(savedInstanceState: Bundle?) {
        gameSetting.mode = intent.getIntExtra(GOBBLET_MODE, Game.SINGLE_PLAYER)
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this) {}
        binding = GameActivityBinding.inflate(layoutInflater)
        viewModel.init(binding, this)
        setContentView(binding.root)
    }

    override fun onBackPressed() {
        viewModel.onBackPressed(this)
    }

    override fun onDestroy() {
        viewModel.onDestroy()
        super.onDestroy()
    }

    override fun getGameSetting(): GameSetting {
        gameSetting.difficulty = HARD
        gameSetting.dimension = 4
        gameSetting.player1Color = GREEN
        gameSetting.player2Color = RED
        return gameSetting
    }
}
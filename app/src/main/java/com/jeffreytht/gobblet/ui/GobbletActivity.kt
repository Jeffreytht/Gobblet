package com.jeffreytht.gobblet.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.MobileAds
import com.jeffreytht.gobblet.databinding.ActivityGobbletBinding
import com.jeffreytht.gobblet.di.DaggerGobbletActivityComponent
import com.jeffreytht.gobblet.model.AIPlayer
import com.jeffreytht.gobblet.model.GobbletMode
import com.jeffreytht.gobblet.model.Peace.Companion.GREEN
import com.jeffreytht.gobblet.model.Peace.Companion.RED
import javax.inject.Inject

class GobbletActivity : AppCompatActivity() {
    companion object {
        const val GOBBLET_MODE = "GOBBLET_MODE"
    }

    @Inject
    lateinit var viewModel: GobbletActivityViewModel
    private lateinit var binding: ActivityGobbletBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        @GobbletMode.Mode val gobbletMode =
            intent.getIntExtra(GOBBLET_MODE, GobbletMode.SINGLE_PLAYER)

        DaggerGobbletActivityComponent
            .builder()
            .withDimension(4)
            .withContext(this)
            .withActivity(this)
            .withGobbletMode(gobbletMode)
            .withAIColor(RED)
            .withPlayerColor(GREEN)
            .withDifficulty(AIPlayer.HARD)
            .build()
            .inject(this)

        super.onCreate(savedInstanceState)
        MobileAds.initialize(this) {}
        binding = ActivityGobbletBinding.inflate(layoutInflater)
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
}
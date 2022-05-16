package com.jeffreytht.gobblet.ui

import android.os.Bundle
import com.google.android.gms.ads.MobileAds
import com.jeffreytht.gobblet.databinding.GameActivityBinding
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class GameActivity : DaggerAppCompatActivity() {
    companion object {
        const val GOBBLET_MODE = "GOBBLET_MODE"
    }

    @Inject
    lateinit var viewModel: GameActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this) {}
        val binding = GameActivityBinding.inflate(layoutInflater)
        viewModel.initView(binding, this)
        setContentView(binding.root)
    }

    override fun onBackPressed() {
        viewModel.onBackPressed()
    }

    override fun onDestroy() {
        viewModel.onDestroy()
        super.onDestroy()
    }
}
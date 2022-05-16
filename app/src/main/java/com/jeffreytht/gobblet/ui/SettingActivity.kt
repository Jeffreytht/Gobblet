package com.jeffreytht.gobblet.ui

import android.os.Bundle
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.jeffreytht.gobblet.databinding.SettingActivityBinding
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class SettingActivity : DaggerAppCompatActivity() {
    @Inject
    lateinit var settingActivityViewModel: SettingActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this) {}
        val binding = SettingActivityBinding.inflate(layoutInflater)
        binding.vm = settingActivityViewModel
        binding.adView.loadAd(AdRequest.Builder().build())
        setContentView(binding.root)
    }

    override fun onBackPressed() {
        settingActivityViewModel.onBackPressed()
    }
}
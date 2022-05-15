package com.jeffreytht.gobblet.ui

import android.os.Bundle
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.jeffreytht.gobblet.databinding.HomeActivityBinding
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject


class HomeActivity : DaggerAppCompatActivity() {
    @Inject
    lateinit var homeActivityViewModel: HomeActivityViewModel
    private lateinit var homeActivityBinding: HomeActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this) {}
        homeActivityBinding = HomeActivityBinding.inflate(layoutInflater)
        homeActivityBinding.vm = homeActivityViewModel
        homeActivityBinding.adViewHome.loadAd(AdRequest.Builder().build())
        setContentView(homeActivityBinding.root)
    }
}
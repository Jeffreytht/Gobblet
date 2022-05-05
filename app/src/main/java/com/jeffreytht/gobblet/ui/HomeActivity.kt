package com.jeffreytht.gobblet.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.jeffreytht.gobblet.databinding.HomeActivityBinding
import com.jeffreytht.gobblet.di.DaggerHomeActivityComponent
import javax.inject.Inject


class HomeActivity : AppCompatActivity() {
    @Inject
    lateinit var homeActivityViewModel: HomeActivityViewModel
    private lateinit var homeActivityBinding: HomeActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerHomeActivityComponent.builder()
            .activity(this)
            .build()
            .inject(this)

        super.onCreate(savedInstanceState)
        MobileAds.initialize(this) {}
        homeActivityBinding = HomeActivityBinding.inflate(layoutInflater)
        homeActivityBinding.vm = homeActivityViewModel
        homeActivityBinding.adViewHome.loadAd(AdRequest.Builder().build())
        setContentView(homeActivityBinding.root)
    }
}
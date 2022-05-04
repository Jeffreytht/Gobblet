package com.jeffreytht.gobblet.ui

import android.app.Activity
import android.os.Bundle
import com.jeffreytht.gobblet.databinding.HomeActivityBinding
import com.jeffreytht.gobblet.di.DaggerHomeActivityComponent
import javax.inject.Inject


class HomeActivity : Activity() {
    @Inject
    lateinit var homeActivityViewModel: HomeActivityViewModel
    private lateinit var homeActivityBinding: HomeActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerHomeActivityComponent.builder()
            .activity(this)
            .build()
            .inject(this)

        super.onCreate(savedInstanceState)
        homeActivityBinding = HomeActivityBinding.inflate(layoutInflater)
        homeActivityBinding.vm = homeActivityViewModel
        setContentView(homeActivityBinding.root)
    }
}
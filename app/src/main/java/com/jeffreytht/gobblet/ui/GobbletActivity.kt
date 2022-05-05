package com.jeffreytht.gobblet.ui

import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.jeffreytht.gobblet.GridAdapter
import com.jeffreytht.gobblet.PeacesAdapter
import com.jeffreytht.gobblet.R
import com.jeffreytht.gobblet.databinding.ActivityGobbletBinding

class GobbletActivity : AppCompatActivity() {
    companion object {
        const val GOBBLET_MODE = "GOBBLET_MODE"
        const val PEACES_COUNT = 12
    }

    private lateinit var binding: ActivityGobbletBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this) {}
        binding = ActivityGobbletBinding.inflate(layoutInflater)
        binding.adViewGobblet.loadAd(AdRequest.Builder().build())
        initializePeaces(
            binding.recyclerViewRedPeaces,
            R.drawable.ic_red_large_peace,
            R.drawable.ic_red_medium_peace,
            R.drawable.ic_red_small_peace
        )
        initializePeaces(
            binding.recyclerViewGreenPeaces,
            R.drawable.ic_green_large_peace,
            R.drawable.ic_green_medium_peace,
            R.drawable.ic_green_small_peace
        )
        binding.gobbletRecyclerView.adapter = GridAdapter(row = 4, col = 4)
        binding.gobbletRecyclerView.layoutManager =
            object : GridLayoutManager(this, 4) {
                override fun canScrollHorizontally(): Boolean = false
                override fun canScrollVertically(): Boolean = false
            }
        setContentView(binding.root)
    }

    private fun initializePeaces(
        recyclerView: RecyclerView,
        @DrawableRes smallPeace: Int,
        @DrawableRes mediumPeace: Int,
        @DrawableRes largePeace: Int
    ) {
        val dataset = Array(PEACES_COUNT) { 0 }
        for (i in 0 until PEACES_COUNT) {
            when {
                i < 3 -> dataset[i] = smallPeace
                i < 6 -> dataset[i] = mediumPeace
                i < 9 -> dataset[i] = largePeace
            }
        }
        recyclerView.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        recyclerView.adapter = PeacesAdapter(dataset)
    }
}
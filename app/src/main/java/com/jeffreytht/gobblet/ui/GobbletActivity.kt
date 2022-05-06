package com.jeffreytht.gobblet.ui

import android.graphics.Canvas
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
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
        const val SCALE_DIFF = 0.25f
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
        )
        initializePeaces(
            binding.recyclerViewGreenPeaces,
            R.drawable.ic_green_large_peace,
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
        @DrawableRes resId: Int,
    ) {
        val dataset = ArrayList<Pair<Int, Float>>()

        var scale = 1.25f
        for (i in 0 until PEACES_COUNT) {
            if (i % 3 == 0) {
                scale -= SCALE_DIFF
            }
            dataset.add(Pair(resId, scale))
        }

        val drawable =
            ResourcesCompat.getDrawable(resources, R.drawable.ic_red_large_peace, theme)!!

        recyclerView.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        recyclerView.adapter = PeacesAdapter(
            dataset,
            drawable.intrinsicWidth.toFloat() / drawable.intrinsicHeight,
            this::onLongClick
        )
    }

    private fun onLongClick(view: View): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val myShadow =
                (view as ImageView).drawable.constantState?.newDrawable()?.mutate()?.let {
                    PeaceShadow(
                        it,
                        view
                    )
                }

            view.startDragAndDrop(
                null,
                myShadow,
                null,
                0
            )

            return true
        }

        return false
    }
}

class PeaceShadow(private val shadow: Drawable, view: ImageView) : View.DragShadowBuilder(view) {

    override fun onProvideShadowMetrics(size: Point, touch: Point) {
        shadow.setBounds(0, 0, view.width, view.height)
        size.set(view.width, view.height)
        touch.set(view.width / 2, view.height / 2)
    }

    override fun onDrawShadow(canvas: Canvas) {
        shadow.draw(canvas)
    }
}
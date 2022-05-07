package com.jeffreytht.gobblet.ui

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.jeffreytht.gobblet.adapter.GridAdapter
import com.jeffreytht.gobblet.adapter.PeacesAdapter
import com.jeffreytht.gobblet.databinding.ActivityGobbletBinding
import com.jeffreytht.gobblet.di.DaggerGridAdapterComponent
import com.jeffreytht.gobblet.di.DaggerPeaceAdapterComponent
import com.jeffreytht.gobblet.model.Grid
import com.jeffreytht.gobblet.model.Peace
import com.jeffreytht.gobblet.model.Peace.Companion.GREEN
import com.jeffreytht.gobblet.model.Peace.Companion.RED
import com.jeffreytht.gobblet.util.PeaceHandler
import com.jeffreytht.gobblet.util.ResourcesProvider

class GobbletActivityViewModel(
    private val row: Int,
    private val col: Int,
    private val resourcesProvider: ResourcesProvider
) : ViewModel(), PeaceHandler {
    companion object {
        const val PEACES_COUNT = 12
        const val SCALE_DIFF = 0.25f
    }

    private val peacesAdapterMap = HashMap<@Peace.Color Int, PeacesAdapter>()
    private lateinit var gridAdapter: GridAdapter

    fun init(binding: ActivityGobbletBinding, context: Context) {
        binding.adViewGobblet.loadAd(AdRequest.Builder().build())
        initializePeaces(binding.recyclerViewRedPeaces, RED, context)
        initializePeaces(binding.recyclerViewGreenPeaces, GREEN, context)
        initializeGrids(binding.gobbletRecyclerView, context)
    }

    private fun initializeGrids(
        recyclerView: RecyclerView,
        context: Context
    ) {
        gridAdapter = DaggerGridAdapterComponent
            .builder()
            .withPeaceHandler(this)
            .withActivity(context as Activity)
            .withRow(row)
            .withCol(col)
            .build()
            .providesGridAdapter()

        recyclerView.adapter = gridAdapter

        recyclerView.layoutManager =
            object : GridLayoutManager(context, col) {
                override fun canScrollHorizontally(): Boolean = false
                override fun canScrollVertically(): Boolean = false
            }
    }

    private fun initializePeaces(
        recyclerView: RecyclerView,
        @Peace.Color color: Int,
        context: Context
    ) {
        peacesAdapterMap[color] = DaggerPeaceAdapterComponent
            .builder()
            .withActivity(context as Activity)
            .withPeaceHandler(this)
            .withColor(color)
            .build()
            .providesPeacesAdapter()

        recyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)

        recyclerView.adapter = peacesAdapterMap[color]
    }

    override fun onLongClick(peace: Peace, imageView: ImageView): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val myShadow = resourcesProvider.getDrawable(peace.resId)?.let {
                PeaceShadow(it, imageView)
            }
            imageView.startDragAndDrop(null, myShadow, peace, 0)
        }
        return true
    }

    override fun onDropToGrid(peace: Peace, grid: Grid) {
        gridAdapter.addPeace(grid, peace)
        peacesAdapterMap[peace.color]?.removePeace(peace)
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
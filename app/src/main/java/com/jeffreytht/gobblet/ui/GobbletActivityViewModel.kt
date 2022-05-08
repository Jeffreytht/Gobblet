package com.jeffreytht.gobblet.ui

import android.content.Context
import android.os.Build
import android.widget.ImageView
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.jeffreytht.gobblet.databinding.ActivityGobbletBinding
import com.jeffreytht.gobblet.di.DaggerGameComponent
import com.jeffreytht.gobblet.di.DaggerGridAdapterComponent
import com.jeffreytht.gobblet.di.DaggerPeaceAdapterComponent
import com.jeffreytht.gobblet.model.Grid
import com.jeffreytht.gobblet.model.Peace
import com.jeffreytht.gobblet.model.Peace.Companion.GREEN
import com.jeffreytht.gobblet.model.Peace.Companion.RED
import com.jeffreytht.gobblet.util.PeaceHandler
import com.jeffreytht.gobblet.util.ResourcesProvider

class GobbletActivityViewModel(
    row: Int,
    col: Int,
    context: Context,
    private val resourcesProvider: ResourcesProvider,
) : ViewModel(), PeaceHandler {
    companion object {
        const val PEACES_COUNT = 12
        const val SCALE_DIFF = 0.25f
    }

    private val game = DaggerGameComponent
        .builder()
        .withRow(row)
        .withCol(col)
        .build()
        .provideGame()

    private val gridAdapter = DaggerGridAdapterComponent
        .builder()
        .withPeaceHandler(this)
        .withContext(context)
        .withData(game.grids)
        .build()
        .providesGridAdapter()

    private val peacesAdapterMap = hashMapOf(
        GREEN to DaggerPeaceAdapterComponent
            .builder()
            .withContext(context)
            .withData(game.peacesMap[GREEN]!!)
            .withPeaceHandler(this)
            .build()
            .providesPeacesAdapter(),

        RED to DaggerPeaceAdapterComponent
            .builder()
            .withContext(context)
            .withData(game.peacesMap[RED]!!)
            .withPeaceHandler(this)
            .build()
            .providesPeacesAdapter(),
    )

    init {
        game.registerGameInteractor(gridAdapter)
        game.registerGameInteractor(peacesAdapterMap.values)
    }


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
        recyclerView.adapter = gridAdapter
        recyclerView.layoutManager =
            object : GridLayoutManager(context, game.col) {
                override fun canScrollHorizontally(): Boolean = false
                override fun canScrollVertically(): Boolean = false
            }
    }

    private fun initializePeaces(
        recyclerView: RecyclerView,
        @Peace.Color color: Int,
        context: Context
    ) {
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
        game.move(peace, grid)
    }
}


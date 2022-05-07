package com.jeffreytht.gobblet.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.jeffreytht.gobblet.GridAdapter
import com.jeffreytht.gobblet.PeacesAdapter
import com.jeffreytht.gobblet.R
import com.jeffreytht.gobblet.databinding.ActivityGobbletBinding

class GobbletActivityViewModel(private val row: Int, private val col: Int) : ViewModel(),
    Observable {
    companion object {
        const val PEACES_COUNT = 12
        const val SCALE_DIFF = 0.25f
    }

    private val callbacks = PropertyChangeRegistry()

    fun init(binding: ActivityGobbletBinding, context: Context) {
        binding.adViewGobblet.loadAd(AdRequest.Builder().build())
        initializePeaces(
            binding.recyclerViewRedPeaces,
            R.drawable.ic_red_large_peace,
            context
        )
        initializePeaces(
            binding.recyclerViewGreenPeaces,
            R.drawable.ic_green_large_peace,
            context
        )
        binding.gobbletRecyclerView.adapter = GridAdapter(row = row, col = col)
        binding.gobbletRecyclerView.layoutManager =
            object : GridLayoutManager(context, col) {
                override fun canScrollHorizontally(): Boolean = false
                override fun canScrollVertically(): Boolean = false
            }
    }

    private fun initializePeaces(
        recyclerView: RecyclerView,
        @DrawableRes resId: Int,
        context: Context
    ) {
        val dataset = ArrayList<Pair<Int, Float>>()

        var scale = 1.25f
        for (i in 0 until PEACES_COUNT) {
            if (i % 3 == 0) {
                scale -= SCALE_DIFF
            }
            dataset.add(Pair(resId, scale))
        }

        recyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)

        recyclerView.adapter = PeacesAdapter(
            dataset,
            this::onLongClick
        )
    }

    private fun onLongClick(view: View): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val myShadow =
                (view as ImageView).drawable.constantState?.newDrawable()?.mutate()?.let {
                    PeaceShadow(it, view)
                }

            view.startDragAndDrop(null, myShadow, null, 0)
            return true
        }
        return false
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.remove(callback)
    }

    private fun notifyPropertyChanged() {
        callbacks.notifyCallbacks(this, 0, null)
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
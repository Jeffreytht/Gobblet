package com.jeffreytht.gobblet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class GridAdapter(private val row: Int, private val col: Int) :
    RecyclerView.Adapter<GridAdapter.GridHolder>() {
    class GridHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridHolder {
        val inflater = LayoutInflater.from(parent.context)
        return GridHolder(inflater.inflate(R.layout.gobblet_grid_item, parent, false))
    }

    override fun onBindViewHolder(holder: GridHolder, position: Int) {

    }

    override fun getItemCount(): Int = row * col
}
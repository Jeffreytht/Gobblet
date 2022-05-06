package com.jeffreytht.gobblet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jeffreytht.gobblet.databinding.GobbletGridItemBinding

class GridAdapter(private val row: Int, private val col: Int) :
    RecyclerView.Adapter<GridAdapter.GridHolder>() {
    class GridHolder(binding: GobbletGridItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = GobbletGridItemBinding.inflate(inflater, parent, false)
            .apply {
                root.layoutParams.height = parent.measuredHeight / row
            }
        return GridHolder(binding)
    }

    override fun onBindViewHolder(holder: GridHolder, position: Int) {

    }

    override fun getItemCount(): Int = row * col
}
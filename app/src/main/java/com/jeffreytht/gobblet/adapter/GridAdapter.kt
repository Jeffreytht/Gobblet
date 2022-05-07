package com.jeffreytht.gobblet.adapter

import android.view.DragEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jeffreytht.gobblet.R
import com.jeffreytht.gobblet.databinding.GobbletGridItemBinding
import com.jeffreytht.gobblet.model.Grid
import com.jeffreytht.gobblet.model.Peace
import com.jeffreytht.gobblet.util.PeaceHandler

class GridAdapter(
    private val data: ArrayList<ArrayList<Grid>>,
    private val peaceHandler: PeaceHandler
) :
    RecyclerView.Adapter<GridAdapter.GridHolder>() {
    class GridHolder(binding: GobbletGridItemBinding, peaceHandler: PeaceHandler) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnDragListener { view, event ->
                when (event?.action) {
                    DragEvent.ACTION_DROP -> {
                        peaceHandler.onDropToGrid(
                            event.localState as Peace,
                            view.getTag(R.string.grid_tag) as Grid
                        )
                        true
                    }
                    else -> event.localState is Peace
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = GobbletGridItemBinding.inflate(inflater, parent, false)
            .apply {
                root.layoutParams.height = parent.measuredHeight / row
            }
        return GridHolder(binding, peaceHandler)
    }

    override fun onBindViewHolder(holder: GridHolder, position: Int) {
        holder.itemView.setTag(R.string.grid_tag, data[position / row][position % col])
    }

    override fun getItemCount(): Int = row * col

    private val row: Int = data.size

    private val col: Int = if (data.isEmpty()) 0 else data.first().size
}
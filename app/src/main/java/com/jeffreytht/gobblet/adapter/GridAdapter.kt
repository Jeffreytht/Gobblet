package com.jeffreytht.gobblet.adapter

import android.view.DragEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.jeffreytht.gobblet.R
import com.jeffreytht.gobblet.databinding.GobbletGridItemBinding
import com.jeffreytht.gobblet.model.GameInteractor
import com.jeffreytht.gobblet.model.Grid
import com.jeffreytht.gobblet.model.Peace
import com.jeffreytht.gobblet.util.PeaceHandler
import com.jeffreytht.gobblet.util.ResourcesProvider
import kotlin.collections.ArrayList

class GridAdapter(
    private val data: ArrayList<ArrayList<Grid>>,
    private val peaceHandler: PeaceHandler,
    private val resourcesProvider: ResourcesProvider
) :
    RecyclerView.Adapter<GridAdapter.GridHolder>(), GameInteractor {

    class GridHolder(
        binding: GobbletGridItemBinding,
        peaceHandler: PeaceHandler,
        private val parentHeight: Int,
        private val resourcesProvider: ResourcesProvider
    ) :
        RecyclerView.ViewHolder(binding.root) {

        private val imageView: ImageView = binding.peaceImageView

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

        fun initPeaces(grid: Grid) {
            itemView.setTag(R.string.grid_tag, grid)

            if (grid.peaces.empty()) {
                return
            }
            val peace = grid.peaces.peek()
            imageView.layoutParams.apply {
                height = ((parentHeight - 16) * peace.scale).toInt()
                width = (height * resourcesProvider.getAspectRatio(peace.resId)).toInt()

            }
            imageView.requestLayout()
            imageView.setImageResource(peace.resId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridHolder {
        val inflater = LayoutInflater.from(parent.context)
        val rowHeight = parent.measuredHeight / row
        val binding = GobbletGridItemBinding.inflate(inflater, parent, false)
            .apply {
                root.layoutParams.height = rowHeight
            }
        return GridHolder(binding, peaceHandler, rowHeight, resourcesProvider)
    }

    override fun onBindViewHolder(holder: GridHolder, position: Int) {
        holder.initPeaces(data[position / col][position % col])
    }

    override fun getItemCount(): Int = row * col

    private val row: Int = data.size

    private val col: Int = if (data.isEmpty()) 0 else data.first().size

    override fun movePeace(peace: Peace, grid: Grid) {
        data[grid.row][grid.col].peaces.add(peace)
        notifyItemChanged(grid.row * col + grid.col)
    }
}
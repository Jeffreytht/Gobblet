package com.jeffreytht.gobblet.adapter

import android.view.DragEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.jeffreytht.gobblet.R
import com.jeffreytht.gobblet.databinding.GobbletGridItemBinding
import com.jeffreytht.gobblet.model.GameInteractor
import com.jeffreytht.gobblet.model.Grid
import com.jeffreytht.gobblet.model.Peace
import com.jeffreytht.gobblet.util.PeaceHandler
import com.jeffreytht.gobblet.util.ResourcesProvider

class GridAdapter(
    private val data: ArrayList<ArrayList<Grid>>,
    private val peaceHandler: PeaceHandler,
    private val resourcesProvider: ResourcesProvider
) :
    RecyclerView.Adapter<GridAdapter.GridHolder>(), GameInteractor {
    private val peacesSet = HashMap<Peace, Grid>()

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

            binding.peaceImageView.setOnLongClickListener { view ->
                view.getTag(R.string.peace_tag)?.let {
                    peaceHandler.onLongClick(it as Peace, view as ImageView)
                }
                true
            }
        }

        fun initPeaces(grid: Grid) {
            itemView.setTag(R.string.grid_tag, grid)
            itemView.setBackgroundResource(grid.background)
            imageView.apply {
                requestLayout()
                if (grid.peaces.empty()) {
                    setImageDrawable(null)
                    layoutParams.apply {
                        width = LinearLayout.LayoutParams.MATCH_PARENT
                        height = LinearLayout.LayoutParams.MATCH_PARENT
                    }
                    return
                }
                val peace = grid.peaces.peek()
                layoutParams.apply {
                    height = ((parentHeight - 16) * peace.scale).toInt()
                    width = (height * resourcesProvider.getAspectRatio(peace.resId)).toInt()
                }
                setImageResource(peace.resId)
                setTag(R.string.peace_tag, peace)
            }
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
        peacesSet[peace]?.let {
            it.peaces.pop()
            notifyItemChanged(it.row * col + it.col)
        }
        with(grid) {
            peaces.push(peace)
            peacesSet[peace] = this
        }
        notifyItemChanged(grid.row * col + grid.col)
    }

    fun setGridBackground(@DrawableRes resId: Int, row: Int, col: Int) {
        data[row][col].background = resId
        notifyItemChanged(row * this.col + col)
    }

    fun setTopPeaceDrawable(@Peace.Color color: Int, @DrawableRes resId: Int) {
        data.forEach { colData ->
            colData.forEach colDataForEach@{ grid ->
                if (grid.peaces.empty()) {
                    return@colDataForEach
                }
                with(grid.peaces.peek()) {
                    if (this.color == color) {
                        grid.peaces.peek().resId = resId
                    }
                }
            }
        }
        notifyItemRangeChanged(0, itemCount)
    }
}
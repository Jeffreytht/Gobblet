package com.jeffreytht.gobblet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.jeffreytht.gobblet.R
import com.jeffreytht.gobblet.databinding.PeaceRowItemBinding
import com.jeffreytht.gobblet.model.GameInteractor
import com.jeffreytht.gobblet.model.Grid
import com.jeffreytht.gobblet.model.Peace
import com.jeffreytht.gobblet.util.PeaceHandler
import com.jeffreytht.gobblet.util.ResourcesProvider

class PeacesAdapter(
    private val dataset: ArrayList<Peace>,
    private val peaceHandler: PeaceHandler,
    private val resourcesProvider: ResourcesProvider
) :
    RecyclerView.Adapter<PeacesAdapter.PeaceViewHolder>(), GameInteractor {

    class PeaceViewHolder(
        private val binding: PeaceRowItemBinding,
        private val parentHeight: Int,
        private val peaceHandler: PeaceHandler,
        private val resourcesProvider: ResourcesProvider
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.peaceImageView.setOnLongClickListener {
                peaceHandler.onLongClick(it.getTag(R.string.peace_tag) as Peace, it as ImageView)
            }
        }

        fun setImage(peace: Peace) {
            binding.peaceImage = peace.resId
            binding.peaceImageView.layoutParams.apply {
                height = ((parentHeight - 16) * peace.scale).toInt()
                width = (height * resourcesProvider.getAspectRatio(peace.resId)).toInt()
            }
            binding.peaceImageView.setTag(R.string.peace_tag, peace)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeaceViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PeaceViewHolder(
            PeaceRowItemBinding.inflate(inflater, parent, false),
            parent.measuredHeight,
            peaceHandler,
            resourcesProvider
        )
    }

    override fun onBindViewHolder(holder: PeaceViewHolder, position: Int) {
        holder.setImage(dataset[position])
    }

    override fun getItemCount(): Int = dataset.size

    override fun movePeace(peace: Peace, grid: Grid) {
        val idx = dataset.indexOf(peace)
        if (idx != -1) {
            dataset.removeAt(idx)
            notifyItemRemoved(idx)
        }
    }
}
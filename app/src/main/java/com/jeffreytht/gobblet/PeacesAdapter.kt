package com.jeffreytht.gobblet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.jeffreytht.gobblet.databinding.PeaceRowItemBinding

class PeacesAdapter(
    private val dataset: ArrayList<Pair<Int, Float>>,
    private val onLongClick: (view: View) -> Boolean
) :
    RecyclerView.Adapter<PeacesAdapter.PeaceViewHolder>() {

    class PeaceViewHolder(
        private val binding: PeaceRowItemBinding,
        private val parentHeight: Int,
        onLongClick: (view: View) -> Boolean
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.peaceImageView.setOnLongClickListener(onLongClick)
        }

        fun setImage(imageData: Pair<Int, Float>) {
            binding.peaceImage = imageData.first
            val drawable = ResourcesCompat.getDrawable(
                itemView.context.resources,
                imageData.first,
                itemView.context.theme
            )

            var aspectRatio = 1.0f
            drawable?.let{
                aspectRatio = it.intrinsicWidth.toFloat() / it.intrinsicHeight
            }

            binding.peaceImageView.layoutParams.apply {
                height = ((parentHeight - 16) * imageData.second).toInt()
                width = (height * aspectRatio).toInt()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeaceViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PeaceViewHolder(
            PeaceRowItemBinding.inflate(inflater, parent, false),
            parent.measuredHeight,
            onLongClick
        )
    }

    override fun onBindViewHolder(holder: PeaceViewHolder, position: Int) {
        holder.setImage(dataset[position])
    }

    override fun getItemCount(): Int = dataset.size
}
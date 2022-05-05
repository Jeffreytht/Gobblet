package com.jeffreytht.gobblet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.jeffreytht.gobblet.databinding.PeaceRowItemBinding

class PeacesAdapter(private val dataset: Array<Int>) :
    RecyclerView.Adapter<PeacesAdapter.PeaceViewHolder>() {

    class PeaceViewHolder(private val binding: PeaceRowItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setImageResource(@DrawableRes res: Int) {
            binding.peaceImage = res
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeaceViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PeaceViewHolder(PeaceRowItemBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: PeaceViewHolder, position: Int) {
        holder.setImageResource(dataset[position])
    }

    override fun getItemCount(): Int = dataset.size
}
package com.jeffreytht.gobblet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class PeacesAdapter(private val dataset: Array<Int>) : RecyclerView.Adapter<PeacesAdapter.PeaceViewHolder>() {
    class PeaceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.peaceImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeaceViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PeaceViewHolder(inflater.inflate(R.layout.peace_row_item, parent, false))
    }


    override fun onBindViewHolder(holder: PeaceViewHolder, position: Int) {
        holder.imageView.setImageResource(dataset[position])
    }

    override fun getItemCount(): Int = dataset.size
}
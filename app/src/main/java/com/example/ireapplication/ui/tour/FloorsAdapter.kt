package com.example.ireapplication.ui.tour

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.ireapplication.data.models.FloorWithExhibits
import com.example.ireapplication.databinding.ItemFloorBinding

class FloorsAdapter(
    private val onFloorClick: (FloorWithExhibits) -> Unit
) : ListAdapter<FloorWithExhibits, FloorsAdapter.FloorViewHolder>(FloorDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FloorViewHolder {
        val binding = ItemFloorBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FloorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FloorViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FloorViewHolder(
        private val binding: ItemFloorBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onFloorClick(getItem(position))
                }
            }
        }

        fun bind(floorWithExhibits: FloorWithExhibits) {
            binding.apply {
                floorName.text = floorWithExhibits.floor.name
                
                Glide.with(floorImage)
                    .load(floorWithExhibits.floor.imageResourceId)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .skipMemoryCache(false)
                    .placeholder(com.google.android.material.R.drawable.mtrl_ic_error)
                    .error(com.google.android.material.R.drawable.mtrl_ic_error)
                    .centerCrop()
                    .into(floorImage)
            }
        }
    }

    private class FloorDiffCallback : DiffUtil.ItemCallback<FloorWithExhibits>() {
        override fun areItemsTheSame(oldItem: FloorWithExhibits, newItem: FloorWithExhibits): Boolean {
            return oldItem.floor.id == newItem.floor.id
        }

        override fun areContentsTheSame(oldItem: FloorWithExhibits, newItem: FloorWithExhibits): Boolean {
            return oldItem == newItem
        }
    }
} 
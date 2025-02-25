package com.example.ireapplication.ui.tour

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ireapplication.data.models.Exhibit
import com.example.ireapplication.databinding.ItemExhibitBinding

class ExhibitsAdapter(
    private val onExhibitClick: (Exhibit) -> Unit
) : ListAdapter<Exhibit, ExhibitsAdapter.ExhibitViewHolder>(ExhibitDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExhibitViewHolder {
        val binding = ItemExhibitBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ExhibitViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExhibitViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ExhibitViewHolder(
        private val binding: ItemExhibitBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onExhibitClick(getItem(position))
                }
            }
        }

        fun bind(exhibit: Exhibit) {
            binding.apply {
                exhibitName.text = exhibit.name
                exhibitDescription.text = exhibit.shortDescription
                
                Glide.with(exhibitImage)
                    .load(exhibit.imageResourceId)
                    .placeholder(com.google.android.material.R.drawable.mtrl_ic_error)
                    .error(com.google.android.material.R.drawable.mtrl_ic_error)
                    .centerCrop()
                    .into(exhibitImage)
            }
        }
    }

    private class ExhibitDiffCallback : DiffUtil.ItemCallback<Exhibit>() {
        override fun areItemsTheSame(oldItem: Exhibit, newItem: Exhibit): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Exhibit, newItem: Exhibit): Boolean {
            return oldItem == newItem
        }
    }
} 
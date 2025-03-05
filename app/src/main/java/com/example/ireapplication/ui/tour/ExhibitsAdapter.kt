package com.example.ireapplication.ui.tour

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.ireapplication.R
import com.example.ireapplication.data.models.Exhibit
import com.example.ireapplication.databinding.ItemExhibitBinding
import android.graphics.drawable.Drawable

class ExhibitsAdapter(
    private val onExhibitClick: (Exhibit) -> Unit
) : ListAdapter<Exhibit, ExhibitsAdapter.ExhibitViewHolder>(ExhibitDiffCallback()) {

    private val TAG = "ExhibitsAdapter"

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
                
                Log.d(TAG, "Loading image for exhibit: ${exhibit.name}, Resource ID: ${exhibit.imageResourceId}")
                
                Glide.with(exhibitImage)
                    .load(exhibit.imageResourceId)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .skipMemoryCache(false)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>,
                            isFirstResource: Boolean
                        ): Boolean {
                            Log.e(TAG, "Failed to load image for exhibit ${exhibit.name}: ${e?.message}")
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable,
                            model: Any,
                            target: Target<Drawable>,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            Log.d(TAG, "Successfully loaded image for exhibit ${exhibit.name}, Resource: $model")
                            return false
                        }
                    })
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
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
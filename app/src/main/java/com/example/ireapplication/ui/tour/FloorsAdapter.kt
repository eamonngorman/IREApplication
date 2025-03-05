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
import com.example.ireapplication.data.models.FloorWithExhibits
import com.example.ireapplication.databinding.ItemFloorBinding
import android.graphics.drawable.Drawable

class FloorsAdapter(
    private val onItemClick: (FloorWithExhibits) -> Unit
) : ListAdapter<FloorWithExhibits, FloorsAdapter.FloorViewHolder>(FloorDiffCallback()) {

    private val TAG = "FloorsAdapter"
    private var lastAnimatedPosition = -1

    private val floorImageMap: Map<String, Int> = mapOf(
        "0" to R.drawable.floor_general,  // Ground floor
        "1" to R.drawable.floor_first,
        "2" to R.drawable.floor_second,
        "3" to R.drawable.floor_third,
        "4" to R.drawable.floor_fourth,
        "6" to R.drawable.floor_sixth
        //"7" to R.drawable.floor_7
    )

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
        if (position > lastAnimatedPosition) {
            animateItem(holder.itemView, position)
            lastAnimatedPosition = position
        }
    }

    private fun animateItem(view: android.view.View, position: Int) {
        view.alpha = 0f
        view.translationY = 50f
        view.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(300)
            .setStartDelay(position * 100L)
            .start()
    }

    inner class FloorViewHolder(
        private val binding: ItemFloorBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(floorWithExhibits: FloorWithExhibits) {
            val floor = floorWithExhibits.floor
            
            // Set floor number using actual building level
            binding.floorNumber.text = floor.level.toString()
            
            // Set floor title and description
            binding.floorTitle.text = floor.name
            binding.floorDescription.text = floor.description
            
            // Log floor information
            Log.d(TAG, "Binding floor: ${floor.name}, Level: ${floor.level}")
            
            // Load the correct floor image based on level
            val floorLevel = floor.level.toString()
            val imageResource = floorImageMap[floorLevel]
            Log.d(TAG, "Floor level: $floorLevel, Mapped image resource: $imageResource")
            
            if (imageResource == null) {
                Log.e(TAG, "No image resource found for floor level: $floorLevel")
            }
            
            Glide.with(binding.floorImage)
                .load(imageResource ?: R.drawable.error_image)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .skipMemoryCache(false)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e(TAG, "Failed to load image for floor ${floor.name}: ${e?.message}")
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.d(TAG, "Successfully loaded image for floor ${floor.name}, Resource: $model")
                        return false
                    }
                })
                .error(R.drawable.error_image)
                .centerCrop()
                .into(binding.floorImage)

            // Set up explore button
            binding.exploreButton.setOnClickListener {
                onItemClick(floorWithExhibits)
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
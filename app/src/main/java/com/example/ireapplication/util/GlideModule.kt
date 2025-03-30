package com.example.ireapplication.util

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import java.io.InputStream

@GlideModule
class IREGlideModule : AppGlideModule() {
    
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        // Calculate memory cache size based on device
        val calculator = MemorySizeCalculator.Builder(context)
            .setMemoryCacheScreens(2f) // Use 2 screens worth of pixels for memory cache
            .setBitmapPoolScreens(3f) // Use 3 screens worth of pixels for bitmap pool
            .build()
        
        // Apply calculated cache sizes
        builder.setMemoryCache(LruResourceCache(calculator.memoryCacheSize.toLong()))
        builder.setBitmapPool(LruBitmapPool(calculator.bitmapPoolSize.toLong()))
        
        // Set default request options
        builder.setDefaultRequestOptions(
            RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE) // Cache final images
                .skipMemoryCache(false) // Use memory cache
                .centerCrop() // Default scaling
        )
    }

    // This is important for Glide module detection
    override fun isManifestParsingEnabled(): Boolean {
        return false
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        super.registerComponents(context, glide, registry)
        // Register any custom components if needed
    }
} 
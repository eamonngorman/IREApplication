package com.example.ireapplication.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Exhibit(
    val id: String,
    val name: String,
    val shortDescription: String,
    val fullDescription: String,
    val imageUrl: String,
    val audioUrl: String? = null
) : Parcelable 
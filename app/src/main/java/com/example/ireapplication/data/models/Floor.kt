package com.example.ireapplication.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "floors")
data class Floor(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String,
    val imageResourceId: Int,  // Reference to R.drawable.* resource
    val level: Int,
    val isGeneral: Boolean = false // To identify general category items
) 
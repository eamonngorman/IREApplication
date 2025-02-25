package com.example.ireapplication.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "exhibits",
    foreignKeys = [
        ForeignKey(
            entity = Floor::class,
            parentColumns = ["id"],
            childColumns = ["floorId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("floorId")]
)
data class Exhibit(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val shortDescription: String,
    val fullDescription: String,
    val imageResourceId: Int,  // Reference to R.drawable.* resource
    val floorId: Int
) 
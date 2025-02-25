package com.example.ireapplication.data.models

import androidx.room.Embedded
import androidx.room.Relation

data class FloorWithExhibits(
    @Embedded
    val floor: Floor,
    
    @Relation(
        parentColumn = "id",
        entityColumn = "floorId"
    )
    val exhibits: List<Exhibit>
) 
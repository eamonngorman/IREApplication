package com.example.ireapplication.data

import com.example.ireapplication.R
import com.example.ireapplication.data.models.Floor
import com.example.ireapplication.data.models.Exhibit

object SampleDataProvider {
    // Floor level constants
    object FloorLevel {
        const val GENERAL = 0
        const val FIRST = 1
        const val SECOND = 2
        const val THIRD = 3
    }

    fun getFloors(): List<Floor> = listOf(
        Floor(
            name = "General",
            description = "General exhibits and information",
            imageResourceId = R.drawable.floor_general,
            level = FloorLevel.GENERAL,
            isGeneral = true
        ),
        Floor(
            name = "First Floor",
            description = "Origins and Grassroots",
            imageResourceId = R.drawable.floor_first,
            level = FloorLevel.FIRST,
            isGeneral = false
        ),
        Floor(
            name = "Second Floor",
            description = "Skills and Techniques",
            imageResourceId = R.drawable.floor_second,
            level = FloorLevel.SECOND,
            isGeneral = false
        ),
        Floor(
            name = "Third Floor",
            description = "Team and Leadership",
            imageResourceId = R.drawable.floor_third,
            level = FloorLevel.THIRD,
            isGeneral = false
        )
    )

    fun getExhibits(): List<Exhibit> = listOf(
        // General Exhibits
        Exhibit(
            name = "Tunnel Introduction Video",
            shortDescription = "Welcome to rugby",
            fullDescription = "An immersive introduction to the world of rugby, setting the stage for your journey through this incredible sport.",
            imageResourceId = R.drawable.exhibit_tunnel,
            floorId = FloorLevel.GENERAL
        ),
        Exhibit(
            name = "Building Architecture",
            shortDescription = "Building design",
            fullDescription = "Discover the unique design and structure of the International Rugby Experience building.",
            imageResourceId = R.drawable.exhibit_architecture,
            floorId = FloorLevel.GENERAL
        ),
        // Floor 1 Exhibits
        Exhibit(
            name = "Global Grassroots",
            shortDescription = "Community impact",
            fullDescription = "Explore how rugby shapes communities and develops grassroots initiatives worldwide.",
            imageResourceId = R.drawable.exhibit_grassroots,
            floorId = FloorLevel.FIRST
        ),
        // Floor 2 Exhibits
        Exhibit(
            name = "Skills Training",
            shortDescription = "Essential skills",
            fullDescription = "Interactive displays showing the fundamental skills needed in rugby.",
            imageResourceId = R.drawable.exhibit_skills,
            floorId = FloorLevel.SECOND
        ),
        // Floor 3 Exhibits
        Exhibit(
            name = "Team Leadership",
            shortDescription = "Rugby leadership",
            fullDescription = "Discover what makes great rugby leaders and how they inspire their teams.",
            imageResourceId = R.drawable.exhibit_leadership,
            floorId = FloorLevel.THIRD
        )
    )
} 
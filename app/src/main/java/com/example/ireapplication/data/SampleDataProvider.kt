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
        const val FOURTH = 4
        const val SIXTH = 6
    }

    fun getFloors(): List<Floor> = listOf(
        Floor(
            name = "GENERAL",
            description = "General exhibits and information",
            imageResourceId = R.drawable.floor_general,
            level = FloorLevel.GENERAL,
            isGeneral = true
        ),
        Floor(
            name = "LEGENDARY BEGINNINGS",
            description = "First Floor - Origins and Grassroots",
            imageResourceId = R.drawable.floor_first,
            level = FloorLevel.FIRST,
            isGeneral = false
        ),
        Floor(
            name = "LEGENDARY SKILLS",
            description = "Second Floor - Skills and Techniques",
            imageResourceId = R.drawable.floor_second,
            level = FloorLevel.SECOND,
            isGeneral = false
        ),
        Floor(
            name = "LEGENDARY TEAMS",
            description = "Third Floor - Team and Leadership",
            imageResourceId = R.drawable.floor_third,
            level = FloorLevel.THIRD,
            isGeneral = false
        ),
        Floor(
            name = "LEGENDARY NATIONS",
            description = "Fourth Floor - International Rugby",
            imageResourceId = R.drawable.floor_fourth,
            level = FloorLevel.FOURTH,
            isGeneral = false
        ),
        Floor(
            name = "HALL OF LEGENDS",
            description = "Sixth Floor - Celebrating Rugby's Greatest",
            imageResourceId = R.drawable.floor_sixth,
            level = FloorLevel.SIXTH,
            isGeneral = false
        )
    )

    fun getExhibits(): List<Exhibit> = listOf(
        // General Exhibits
        Exhibit(
            name = "Tunnel Introduction Video",
            shortDescription = "Welcome to rugby",
            fullDescription = "An immersive introduction to the world of rugby, setting the stage for your journey through this incredible sport.",
            imageResourceId = R.drawable.floor_general,
            floorId = FloorLevel.GENERAL
        ),
        Exhibit(
            name = "Building Architecture",
            shortDescription = "Building design",
            fullDescription = "Discover the unique design and structure of the International Rugby Experience building.",
            imageResourceId = R.drawable.floor_general,
            floorId = FloorLevel.GENERAL
        ),
        Exhibit(
            name = "Legendary Encounters",
            shortDescription = "Historic moments",
            fullDescription = "Experience legendary moments that have defined the sport of rugby.",
            imageResourceId = R.drawable.floor_general,
            floorId = FloorLevel.GENERAL
        ),
        // Floor 1 Exhibits - LEGENDARY BEGINNINGS
        Exhibit(
            name = "Global Grassroots",
            shortDescription = "Community impact",
            fullDescription = "Explore how rugby shapes communities and develops grassroots initiatives worldwide.",
            imageResourceId = R.drawable.floor_general,
            floorId = FloorLevel.FIRST
        ),
        Exhibit(
            name = "Grassroots Initiatives Tablets",
            shortDescription = "Interactive learning",
            fullDescription = "Interactive tablets showcasing grassroots rugby initiatives from around the world.",
            imageResourceId = R.drawable.floor_general,
            floorId = FloorLevel.FIRST
        ),
        Exhibit(
            name = "Legendary Beginnings",
            shortDescription = "Origins of rugby",
            fullDescription = "Discover the origins and early development of rugby.",
            imageResourceId = R.drawable.floor_general,
            floorId = FloorLevel.FIRST
        ),
        Exhibit(
            name = "Ball of Fame",
            shortDescription = "Historic balls",
            fullDescription = "A showcase of historic rugby balls that tell the story of the game's evolution.",
            imageResourceId = R.drawable.floor_general,
            floorId = FloorLevel.FIRST
        ),
        // Floor 2 Exhibits - LEGENDARY SKILLS
        Exhibit(
            name = "Legendary Skills",
            shortDescription = "Core techniques",
            fullDescription = "Master the fundamental skills: Handling, Strength & Technique, Running, and Kicking.",
            imageResourceId = R.drawable.floor_general,
            floorId = FloorLevel.SECOND
        ),
        Exhibit(
            name = "Laws of the Game with Nigel Owens",
            shortDescription = "Rules presentation",
            fullDescription = "Learn the laws of rugby through an interactive presentation with legendary referee Nigel Owens.",
            imageResourceId = R.drawable.floor_general,
            floorId = FloorLevel.SECOND
        ),
        Exhibit(
            name = "Be the Referee",
            shortDescription = "Decision making",
            fullDescription = "Test your knowledge of rugby laws by making real-time refereeing decisions.",
            imageResourceId = R.drawable.floor_general,
            floorId = FloorLevel.SECOND
        ),
        Exhibit(
            name = "Find your Position",
            shortDescription = "Player roles",
            fullDescription = "Discover which rugby position best suits your skills and attributes.",
            imageResourceId = R.drawable.floor_general,
            floorId = FloorLevel.SECOND
        ),
        Exhibit(
            name = "Legendary XV",
            shortDescription = "Dream team",
            fullDescription = "Create your ultimate dream team from rugby's greatest players.",
            imageResourceId = R.drawable.floor_general,
            floorId = FloorLevel.SECOND
        ),
        Exhibit(
            name = "Measure Up",
            shortDescription = "Player comparison",
            fullDescription = "Compare yourself to rugby's elite athletes.",
            imageResourceId = R.drawable.floor_general,
            floorId = FloorLevel.SECOND
        ),
        // Floor 3 Exhibits - LEGENDARY TEAMS
        Exhibit(
            name = "Whole Squad",
            shortDescription = "Team dynamics",
            fullDescription = "Understanding the importance of every team member in rugby.",
            imageResourceId = R.drawable.floor_general,
            floorId = FloorLevel.THIRD
        ),
        Exhibit(
            name = "Legendary Leaders",
            shortDescription = "Rugby leadership",
            fullDescription = "Discover what makes great rugby leaders and how they inspire their teams.",
            imageResourceId = R.drawable.floor_general,
            floorId = FloorLevel.THIRD
        ),
        Exhibit(
            name = "Teamwork on the Pitch",
            shortDescription = "Team coordination",
            fullDescription = "Experience how teams work together during crucial moments in the game.",
            imageResourceId = R.drawable.floor_general,
            floorId = FloorLevel.THIRD
        ),
        Exhibit(
            name = "Team Spirit Lockers",
            shortDescription = "Team culture",
            fullDescription = "Explore the unique traditions and spirit of rugby teams through their locker rooms.",
            imageResourceId = R.drawable.floor_general,
            floorId = FloorLevel.THIRD
        ),
        Exhibit(
            name = "Club Culture Wall",
            shortDescription = "Club traditions",
            fullDescription = "Discover the rich traditions and cultures of rugby clubs worldwide.",
            imageResourceId = R.drawable.floor_general,
            floorId = FloorLevel.THIRD
        ),
        // Floor 4 Exhibits - LEGENDARY NATIONS
        Exhibit(
            name = "Legendary Nations Tablets",
            shortDescription = "National teams",
            fullDescription = "Interactive exploration of rugby's greatest national teams and their achievements.",
            imageResourceId = R.drawable.floor_general,
            floorId = FloorLevel.FOURTH
        ),
        Exhibit(
            name = "Global Growth Touchscreens",
            shortDescription = "Rugby worldwide",
            fullDescription = "Track the global expansion and development of rugby through interactive displays.",
            imageResourceId = R.drawable.floor_general,
            floorId = FloorLevel.FOURTH
        ),
        Exhibit(
            name = "Unity & Respect Wall",
            shortDescription = "Rugby values",
            fullDescription = "Celebrating rugby's core values of unity and respect across nations.",
            imageResourceId = R.drawable.floor_general,
            floorId = FloorLevel.FOURTH
        ),
        Exhibit(
            name = "The Cube",
            shortDescription = "Immersive experience",
            fullDescription = "An immersive audio-visual experience showcasing international rugby's greatest moments.",
            imageResourceId = R.drawable.floor_general,
            floorId = FloorLevel.FOURTH
        ),
        Exhibit(
            name = "Haka Presentation",
            shortDescription = "Cultural tradition",
            fullDescription = "Experience the power and significance of the Haka in rugby.",
            imageResourceId = R.drawable.floor_general,
            floorId = FloorLevel.FOURTH
        ),
        // Floor 6 Exhibits - HALL OF LEGENDS
        Exhibit(
            name = "Limerick Skyline QR Tour",
            shortDescription = "City views",
            fullDescription = "Explore Limerick's landmarks through an interactive QR-guided tour of the skyline.",
            imageResourceId = R.drawable.floor_general,
            floorId = FloorLevel.SIXTH
        ),
        Exhibit(
            name = "Wall of Fame",
            shortDescription = "Rugby legends",
            fullDescription = "Honor wall celebrating the greatest legends of rugby.",
            imageResourceId = R.drawable.floor_general,
            floorId = FloorLevel.SIXTH
        ),
        Exhibit(
            name = "Hall of Fame Digital Archives",
            shortDescription = "Historic archives",
            fullDescription = "Digital collection preserving rugby's most memorable moments and legendary figures.",
            imageResourceId = R.drawable.floor_general,
            floorId = FloorLevel.SIXTH
        )
    )
} 
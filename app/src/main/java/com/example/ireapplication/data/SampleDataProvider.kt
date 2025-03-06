package com.example.ireapplication.data

import android.content.Context
import com.example.ireapplication.R
import com.example.ireapplication.data.models.Exhibit
import com.example.ireapplication.data.models.Floor
import com.example.ireapplication.data.models.FloorWithExhibits

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

    private var sampleData: List<FloorWithExhibits>? = null
    private var cachedLanguage: String? = null

    fun clearCache() {
        sampleData = null
        cachedLanguage = null
        // Force garbage collection to ensure complete cache clear
        System.gc()
    }

    fun getFloors(context: Context): List<Floor> {
        return getSampleFloors(context).map { it.floor }
    }

    fun getExhibits(context: Context): List<Exhibit> {
        return getSampleFloors(context).flatMap { it.exhibits }
    }

    fun getSampleFloors(context: Context): List<FloorWithExhibits> {
        // Get current language
        val currentLanguage = context.resources.configuration.locales[0].language

        // If language changed or no cache, reload data
        if (sampleData == null || cachedLanguage != currentLanguage) {
            cachedLanguage = currentLanguage
            sampleData = null
        }

        // Return cached data if available
        if (sampleData != null) {
            return sampleData!!
        }

        sampleData = listOf(
            FloorWithExhibits(
                floor = Floor(
                    id = 1,
                    name = context.getString(R.string.floor_general),
                    description = context.getString(R.string.floor_general_desc),
                    level = FloorLevel.GENERAL,
                    imageResourceId = R.drawable.floor_general
                ),
                exhibits = listOf(
                    Exhibit(
                        name = context.getString(R.string.exhibit_tunnel_intro),
                        shortDescription = context.getString(R.string.exhibit_tunnel_intro_short),
                        fullDescription = context.getString(R.string.exhibit_tunnel_intro_full),
                        imageResourceId = R.drawable.exhibit_tunnel2,
                        floorId = FloorLevel.GENERAL
                    ),
                    Exhibit(
                        name = context.getString(R.string.exhibit_building_architecture),
                        shortDescription = context.getString(R.string.exhibit_building_architecture_short),
                        fullDescription = context.getString(R.string.exhibit_building_architecture_full),
                        imageResourceId = R.drawable.exhibit_architecture,
                        floorId = FloorLevel.GENERAL
                    ),
                    Exhibit(
                        name = context.getString(R.string.exhibit_legendary_encounters),
                        shortDescription = context.getString(R.string.exhibit_legendary_encounters_short),
                        fullDescription = context.getString(R.string.exhibit_legendary_encounters_full),
                        imageResourceId = R.drawable.exhibit_encounters,
                        floorId = FloorLevel.GENERAL
                    )
                )
            ),
            FloorWithExhibits(
                floor = Floor(
                    id = 2,
                    name = context.getString(R.string.floor_legendary_beginnings),
                    description = context.getString(R.string.floor_legendary_beginnings_desc),
                    level = FloorLevel.FIRST,
                    imageResourceId = R.drawable.floor_first
                ),
                exhibits = listOf(
                    Exhibit(
                        name = context.getString(R.string.exhibit_global_grassroots),
                        shortDescription = context.getString(R.string.exhibit_global_grassroots_short),
                        fullDescription = context.getString(R.string.exhibit_global_grassroots_full),
                        imageResourceId = R.drawable.exhibit_grassroots,
                        floorId = FloorLevel.FIRST
                    ),
                    Exhibit(
                        name = context.getString(R.string.exhibit_grassroots_tablets),
                        shortDescription = context.getString(R.string.exhibit_grassroots_tablets_short),
                        fullDescription = context.getString(R.string.exhibit_grassroots_tablets_full),
                        imageResourceId = R.drawable.exhibit_tablets,
                        floorId = FloorLevel.FIRST
                    ),
                    Exhibit(
                        name = context.getString(R.string.exhibit_legendary_beginnings),
                        shortDescription = context.getString(R.string.exhibit_legendary_beginnings_short),
                        fullDescription = context.getString(R.string.exhibit_legendary_beginnings_full),
                        imageResourceId = R.drawable.exhibit_beginnings,
                        floorId = FloorLevel.FIRST
                    ),
                    Exhibit(
                        name = context.getString(R.string.exhibit_ball_of_fame),
                        shortDescription = context.getString(R.string.exhibit_ball_of_fame_short),
                        fullDescription = context.getString(R.string.exhibit_ball_of_fame_full),
                        imageResourceId = R.drawable.exhibit_bof,
                        floorId = FloorLevel.FIRST
                    )
                )
            ),
            FloorWithExhibits(
                floor = Floor(
                    id = 3,
                    name = context.getString(R.string.floor_legendary_skills),
                    description = context.getString(R.string.floor_legendary_skills_desc),
                    level = FloorLevel.SECOND,
                    imageResourceId = R.drawable.floor_second
                ),
                exhibits = listOf(
                    Exhibit(
                        name = context.getString(R.string.exhibit_handling_skills),
                        shortDescription = context.getString(R.string.exhibit_handling_skills_short),
                        fullDescription = context.getString(R.string.exhibit_handling_skills_full),
                        imageResourceId = R.drawable.exhibit_handling,
                        floorId = FloorLevel.SECOND
                    ),
                    Exhibit(
                        name = context.getString(R.string.exhibit_strength_technique),
                        shortDescription = context.getString(R.string.exhibit_strength_technique_short),
                        fullDescription = context.getString(R.string.exhibit_strength_technique_full),
                        imageResourceId = R.drawable.exhibit_strength,
                        floorId = FloorLevel.SECOND
                    ),
                    Exhibit(
                        name = context.getString(R.string.exhibit_running_skills),
                        shortDescription = context.getString(R.string.exhibit_running_skills_short),
                        fullDescription = context.getString(R.string.exhibit_running_skills_full),
                        imageResourceId = R.drawable.exhibit_running,
                        floorId = FloorLevel.SECOND
                    ),
                    Exhibit(
                        name = context.getString(R.string.exhibit_kicking_skills),
                        shortDescription = context.getString(R.string.exhibit_kicking_skills_short),
                        fullDescription = context.getString(R.string.exhibit_kicking_skills_full),
                        imageResourceId = R.drawable.exhibit_kicking,
                        floorId = FloorLevel.SECOND
                    ),
                    Exhibit(
                        name = context.getString(R.string.exhibit_laws_game),
                        shortDescription = context.getString(R.string.exhibit_laws_game_short),
                        fullDescription = context.getString(R.string.exhibit_laws_game_full),
                        imageResourceId = R.drawable.exhibit_laws,
                        floorId = FloorLevel.SECOND
                    ),
                    Exhibit(
                        name = context.getString(R.string.exhibit_be_referee),
                        shortDescription = context.getString(R.string.exhibit_be_referee_short),
                        fullDescription = context.getString(R.string.exhibit_be_referee_full),
                        imageResourceId = R.drawable.exhibit_referee,
                        floorId = FloorLevel.SECOND
                    ),
                    Exhibit(
                        name = context.getString(R.string.exhibit_find_position),
                        shortDescription = context.getString(R.string.exhibit_find_position_short),
                        fullDescription = context.getString(R.string.exhibit_find_position_full),
                        imageResourceId = R.drawable.exhibit_fyp,
                        floorId = FloorLevel.SECOND
                    ),
                    Exhibit(
                        name = context.getString(R.string.exhibit_measure_up),
                        shortDescription = context.getString(R.string.exhibit_measure_up_short),
                        fullDescription = context.getString(R.string.exhibit_measure_up_full),
                        imageResourceId = R.drawable.exhibit_measure,
                        floorId = FloorLevel.SECOND
                    )
                )
            ),
            FloorWithExhibits(
                floor = Floor(
                    id = 4,
                    name = context.getString(R.string.floor_legendary_teams),
                    description = context.getString(R.string.floor_legendary_teams_desc),
                    level = FloorLevel.THIRD,
                    imageResourceId = R.drawable.floor_third
                ),
                exhibits = listOf(
                    Exhibit(
                        name = context.getString(R.string.exhibit_whole_squad),
                        shortDescription = context.getString(R.string.exhibit_whole_squad_short),
                        fullDescription = context.getString(R.string.exhibit_whole_squad_full),
                        imageResourceId = R.drawable.exhibit_wholesquad,
                        floorId = FloorLevel.THIRD
                    ),
                    Exhibit(
                        name = context.getString(R.string.exhibit_legendary_leaders),
                        shortDescription = context.getString(R.string.exhibit_legendary_leaders_short),
                        fullDescription = context.getString(R.string.exhibit_legendary_leaders_full),
                        imageResourceId = R.drawable.exhibit_leadership,
                        floorId = FloorLevel.THIRD
                    ),
                    Exhibit(
                        name = context.getString(R.string.exhibit_teamwork_pitch),
                        shortDescription = context.getString(R.string.exhibit_teamwork_pitch_short),
                        fullDescription = context.getString(R.string.exhibit_teamwork_pitch_full),
                        imageResourceId = R.drawable.error_image,
                        floorId = FloorLevel.THIRD
                    ),
                    Exhibit(
                        name = context.getString(R.string.exhibit_team_spirit),
                        shortDescription = context.getString(R.string.exhibit_team_spirit_short),
                        fullDescription = context.getString(R.string.exhibit_team_spirit_full),
                        imageResourceId = R.drawable.error_image,
                        floorId = FloorLevel.THIRD
                    ),
                    Exhibit(
                        name = context.getString(R.string.exhibit_club_culture),
                        shortDescription = context.getString(R.string.exhibit_club_culture_short),
                        fullDescription = context.getString(R.string.exhibit_club_culture_full),
                        imageResourceId = R.drawable.exhibit_clubculture,
                        floorId = FloorLevel.THIRD
                    )
                )
            ),
            FloorWithExhibits(
                floor = Floor(
                    id = 5,
                    name = context.getString(R.string.floor_legendary_nations),
                    description = context.getString(R.string.floor_legendary_nations_desc),
                    level = FloorLevel.FOURTH,
                    imageResourceId = R.drawable.floor_fourth
                ),
                exhibits = listOf(
                    Exhibit(
                        name = context.getString(R.string.exhibit_legendary_nations_tablets),
                        shortDescription = context.getString(R.string.exhibit_legendary_nations_tablets_short),
                        fullDescription = context.getString(R.string.exhibit_legendary_nations_tablets_full),
                        imageResourceId = R.drawable.error_image,
                        floorId = FloorLevel.FOURTH
                    ),
                    Exhibit(
                        name = context.getString(R.string.exhibit_global_growth),
                        shortDescription = context.getString(R.string.exhibit_global_growth_short),
                        fullDescription = context.getString(R.string.exhibit_global_growth_full),
                        imageResourceId = R.drawable.exhibit_globalgrowth,
                        floorId = FloorLevel.FOURTH
                    ),
                    Exhibit(
                        name = context.getString(R.string.exhibit_unity_respect),
                        shortDescription = context.getString(R.string.exhibit_unity_respect_short),
                        fullDescription = context.getString(R.string.exhibit_unity_respect_full),
                        imageResourceId = R.drawable.exhibit_unity,
                        floorId = FloorLevel.FOURTH
                    ),
                    Exhibit(
                        name = context.getString(R.string.exhibit_cube),
                        shortDescription = context.getString(R.string.exhibit_cube_short),
                        fullDescription = context.getString(R.string.exhibit_cube_full),
                        imageResourceId = R.drawable.exhibit_cube,
                        floorId = FloorLevel.FOURTH
                    ),
                    Exhibit(
                        name = context.getString(R.string.exhibit_haka),
                        shortDescription = context.getString(R.string.exhibit_haka_short),
                        fullDescription = context.getString(R.string.exhibit_haka_full),
                        imageResourceId = R.drawable.exhibit_haka,
                        floorId = FloorLevel.FOURTH
                    )
                )
            ),
            FloorWithExhibits(
                floor = Floor(
                    id = 7,
                    name = context.getString(R.string.floor_hall_of_legends),
                    description = context.getString(R.string.floor_hall_of_legends_desc),
                    level = FloorLevel.SIXTH,
                    imageResourceId = R.drawable.floor_sixth
                ),
                exhibits = listOf(

                    Exhibit(
                        name = context.getString(R.string.exhibit_limerick_skyline),
                        shortDescription = context.getString(R.string.exhibit_limerick_skyline_short),
                        fullDescription = context.getString(R.string.exhibit_limerick_skyline_full),
                        imageResourceId = R.drawable.exhibit_limerickskyline,
                        floorId = FloorLevel.SIXTH
                    ),
                    Exhibit(
                        name = context.getString(R.string.exhibit_wall_of_fame),
                        shortDescription = context.getString(R.string.exhibit_wall_of_fame_short),
                        fullDescription = context.getString(R.string.exhibit_wall_of_fame_full),
                        imageResourceId = R.drawable.exhibit_wall,
                        floorId = FloorLevel.SIXTH
                    ),
                    Exhibit(
                        name = context.getString(R.string.exhibit_hall_of_fame),
                        shortDescription = context.getString(R.string.exhibit_hall_of_fame_short),
                        fullDescription = context.getString(R.string.exhibit_hall_of_fame_full),
                        imageResourceId = R.drawable.exhibit_archives,
                        floorId = FloorLevel.SIXTH
                    )
                )
            )
        )

        return sampleData!!
    }
} 
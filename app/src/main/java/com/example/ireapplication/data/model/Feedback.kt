package com.example.ireapplication.data.model

import com.google.firebase.firestore.PropertyName

data class Feedback(
    @PropertyName("exhibit_id")
    val exhibitId: String = "",
    
    @PropertyName("rating")
    val rating: Int = 0,
    
    @PropertyName("comment")
    val comment: String = "",
    
    @PropertyName("timestamp")
    val timestamp: Long = System.currentTimeMillis(),
    
    @PropertyName("exhibit_name")
    val exhibitName: String = ""
) 
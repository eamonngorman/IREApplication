package com.example.ireapplication.data.repository

import android.util.Log
import com.example.ireapplication.data.model.Feedback
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FeedbackRepository {
    private val db: FirebaseFirestore = Firebase.firestore
    private val feedbackCollection = db.collection("feedback")

    suspend fun submitFeedback(feedback: Feedback): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Log.d("FeedbackRepository", "Attempting to submit feedback for exhibit: ${feedback.exhibitId}")
            
            // Create a subcollection for each exhibit's feedback
            feedbackCollection
                .document(feedback.exhibitId)
                .collection("feedback")
                .add(feedback)
                .await()
            
            Log.d("FeedbackRepository", "Feedback submitted successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FeedbackRepository", "Error submitting feedback", e)
            Result.failure(e)
        }
    }
} 
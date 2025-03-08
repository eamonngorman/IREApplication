package com.example.ireapplication.ui.components

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.ireapplication.R
import com.example.ireapplication.data.model.Feedback
import com.example.ireapplication.data.repository.FeedbackRepository
import com.example.ireapplication.databinding.DialogFeedbackBinding
import kotlinx.coroutines.launch

class FeedbackDialog : DialogFragment() {
    private var _binding: DialogFeedbackBinding? = null
    private val binding get() = _binding!!
    
    private val feedbackRepository = FeedbackRepository()
    private var exhibitId: String = ""
    private var exhibitName: String = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.Theme_IREApplication_Dialog)
        
        arguments?.let {
            exhibitId = it.getString(ARG_EXHIBIT_ID, "")
            exhibitName = it.getString(ARG_EXHIBIT_NAME, "")
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFeedbackBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRatingBar()
        setupButtons()
    }
    
    private fun setupRatingBar() {
        binding.ratingBar.setOnRatingBarChangeListener { _, rating, fromUser ->
            if (fromUser) {
                binding.submitButton.isEnabled = rating > 0
            }
        }
    }
    
    private fun setupButtons() {
        binding.submitButton.setOnClickListener {
            submitFeedback()
        }
        
        binding.cancelButton.setOnClickListener {
            dismiss()
        }
    }
    
    private fun submitFeedback() {
        val feedback = Feedback(
            exhibitId = exhibitId,
            rating = binding.ratingBar.rating.toInt(),
            comment = binding.commentInput.text.toString(),
            exhibitName = exhibitName
        )
        
        lifecycleScope.launch {
            feedbackRepository.submitFeedback(feedback)
                .onSuccess {
                    dismiss()
                    Toast.makeText(context, "Feedback submitted successfully", Toast.LENGTH_SHORT).show()
                }
                .onFailure { e ->
                    Toast.makeText(context, "Failed to submit feedback: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    companion object {
        private const val ARG_EXHIBIT_ID = "exhibit_id"
        private const val ARG_EXHIBIT_NAME = "exhibit_name"
        
        fun newInstance(exhibitId: String, exhibitName: String): FeedbackDialog {
            return FeedbackDialog().apply {
                arguments = Bundle().apply {
                    putString(ARG_EXHIBIT_ID, exhibitId)
                    putString(ARG_EXHIBIT_NAME, exhibitName)
                }
            }
        }
    }
} 
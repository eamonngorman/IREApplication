package com.example.ireapplication.ui.tour

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.ireapplication.R
import com.example.ireapplication.databinding.FragmentExhibitDetailBinding
import com.example.ireapplication.ui.components.FeedbackDialog
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExhibitDetailFragment : Fragment() {
    private var _binding: FragmentExhibitDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ExhibitDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExhibitDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupObservers()
        setupButtons()
        
        // Load exhibit data based on ID from arguments
        arguments?.getInt("exhibitId")?.let { exhibitId ->
            viewModel.loadExhibit(exhibitId)
        }
    }

    private fun setupObservers() {
        viewModel.exhibit.observe(viewLifecycleOwner) { exhibit ->
            exhibit?.let { updateUI(it) }
        }
    }

    private fun updateUI(exhibit: com.example.ireapplication.data.models.Exhibit) {
        binding.apply {
            exhibitTitle.text = exhibit.name
            exhibitDescription.text = exhibit.fullDescription
            
            Glide.with(this@ExhibitDetailFragment)
                .load(exhibit.imageResourceId)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(exhibitImage)
        }
    }

    private fun setupButtons() {
        binding.feedbackButton.setOnClickListener {
            showFeedbackDialog()
        }
    }

    private fun showFeedbackDialog() {
        viewModel.exhibit.value?.let { exhibit ->
            FeedbackDialog.newInstance(
                exhibitId = exhibit.id.toString(),
                exhibitName = exhibit.name
            ).show(childFragmentManager, "feedback_dialog")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 
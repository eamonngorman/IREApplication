package com.example.ireapplication.ui.tour

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.ireapplication.R
import com.example.ireapplication.databinding.FragmentExhibitDetailBinding
import com.example.ireapplication.ui.components.FeedbackDialog
import com.example.ireapplication.util.ErrorHandler
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import android.graphics.drawable.Drawable
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
        setupAnimations()
        
        // Load exhibit data based on ID from arguments
        arguments?.getInt("exhibitId", -1)?.let { exhibitId ->
            if (exhibitId == -1) {
                ErrorHandler.logDebug("No exhibit ID provided in arguments")
                Toast.makeText(context, "Error: No exhibit ID provided", Toast.LENGTH_SHORT).show()
                return@let
            }
            
            ErrorHandler.logDebug("Loading exhibit with ID: $exhibitId")
            viewModel.loadExhibit(exhibitId)
        } ?: run {
            ErrorHandler.logDebug("No arguments provided to ExhibitDetailFragment")
            Toast.makeText(context, "Error: No arguments provided", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupObservers() {
        viewModel.exhibit.observe(viewLifecycleOwner) { exhibit ->
            if (exhibit != null) {
                ErrorHandler.logDebug("Exhibit loaded: ${exhibit.name}, ID: ${exhibit.id}")
                updateUI(exhibit)
            } else {
                ErrorHandler.logDebug("Failed to load exhibit")
                Toast.makeText(context, "Error: Failed to load exhibit", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupButtons() {
        binding.feedbackButton.setOnClickListener {
            showFeedbackDialog()
        }
    }

    private fun setupAnimations() {
        postponeEnterTransition()
        binding.exhibitImage.viewTreeObserver.addOnPreDrawListener {
            startPostponedEnterTransition()
            true
        }

        // Animate feedback button
        binding.feedbackButton.alpha = 0f
        binding.feedbackButton.translationY = 100f
        binding.feedbackButton.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(300)
            .setStartDelay(300)
            .start()

        // Animate description text
        binding.exhibitDescription.alpha = 0f
        binding.exhibitDescription.animate()
            .alpha(1f)
            .setDuration(300)
            .setStartDelay(200)
            .start()
    }

    private fun updateUI(exhibit: com.example.ireapplication.data.models.Exhibit) {
        binding.apply {
            exhibitTitle.text = exhibit.name
            exhibitDescription.text = exhibit.fullDescription
            
            // Show loading indicator
            progressBar.visibility = View.VISIBLE
            
            try {
                // Use a simpler approach without complex listeners
                Glide.with(requireContext())
                    .load(exhibit.imageResourceId)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .centerCrop()
                    .into(object : com.bumptech.glide.request.target.CustomTarget<Drawable>() {
                        override fun onResourceReady(resource: Drawable, transition: com.bumptech.glide.request.transition.Transition<in Drawable>?) {
                            exhibitImage.setImageDrawable(resource)
                            progressBar.visibility = View.GONE
                            ErrorHandler.logDebug("Image loaded successfully: ${exhibit.name}")
                        }
                        
                        override fun onLoadFailed(errorDrawable: Drawable?) {
                            progressBar.visibility = View.GONE
                            ErrorHandler.logDebug("Image load failed for: ${exhibit.name}")
                        }
                        
                        override fun onLoadCleared(placeholder: Drawable?) {
                            // This is called when the view is detached
                            progressBar.visibility = View.GONE
                        }
                    })
                
                ErrorHandler.logDebug("Requested image loading for: ${exhibit.name}, resource ID: ${exhibit.imageResourceId}")
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                ErrorHandler.logDebug("Image loading error: ${e.message}")
                exhibitImage.setImageResource(R.drawable.placeholder)
            }
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
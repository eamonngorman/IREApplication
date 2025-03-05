package com.example.ireapplication.ui.tour

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.ireapplication.R
import com.example.ireapplication.databinding.FragmentExhibitDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExhibitDetailFragment : Fragment() {
    private var _binding: FragmentExhibitDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ExhibitDetailViewModel by viewModels()
    private val args: ExhibitDetailFragmentArgs by navArgs()

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
        setupToolbar()
        setupViews()
        observeViewModel()
        viewModel.loadExhibit(args.exhibitId)
    }

    private fun setupToolbar() {
        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    private fun setupViews() {
        binding.feedbackButton.setOnClickListener {
            sendFeedbackEmail()
        }
    }

    private fun observeViewModel() {
        viewModel.exhibit.observe(viewLifecycleOwner) { exhibit ->
            exhibit?.let {
                binding.apply {
                    exhibitTitle.text = it.name
                    exhibitDescription.text = it.fullDescription
                    
                    Glide.with(exhibitImage)
                        .load(it.imageResourceId)
                        .placeholder(com.google.android.material.R.drawable.mtrl_ic_error)
                        .error(com.google.android.material.R.drawable.mtrl_ic_error)
                        .centerCrop()
                        .into(exhibitImage)
                }
                // Update toolbar title
                (activity as? AppCompatActivity)?.supportActionBar?.title = it.name
            }
        }
    }

    private fun sendFeedbackEmail() {
        val exhibit = viewModel.exhibit.value
        if (exhibit != null) {
            val emailIntent = Intent(Intent.ACTION_SEND).apply {
                type = "message/rfc822"
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.feedback_email)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_subject, exhibit.name))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.feedback_body, exhibit.name))
            }

            try {
                startActivity(Intent.createChooser(emailIntent, getString(R.string.feedback_chooser_title)))
            } catch (e: Exception) {
                // Handle case where no email app is available
                // TODO: Show error message to user
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 
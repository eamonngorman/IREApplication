package com.example.ireapplication.ui.tour

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ireapplication.databinding.FragmentTourBinding
import dagger.hilt.android.AndroidEntryPoint
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.google.android.material.appbar.AppBarLayout
import kotlin.math.abs

@AndroidEntryPoint
class TourFragment : Fragment() {
    private var _binding: FragmentTourBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TourViewModel by viewModels()
    private lateinit var floorsAdapter: FloorsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTourBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupToolbar() {
        // Set up the collapsing toolbar animation
        binding.appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val scrollRange = appBarLayout.totalScrollRange
            val percentage = abs(verticalOffset).toFloat() / scrollRange.toFloat()
            
            // Fade out the header content as the toolbar collapses
            binding.headerContent.alpha = 1 - percentage
        })

        // Set up the toolbar
        binding.toolbar.apply {
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun setupRecyclerView() {
        floorsAdapter = FloorsAdapter { floorWithExhibits ->
            val action = TourFragmentDirections
                .actionTourToFloorExhibits(floorWithExhibits.floor.id)
            findNavController().navigate(action)
        }
        
        binding.floorsRecyclerView.apply {
            adapter = floorsAdapter
            layoutManager = LinearLayoutManager(context)
            // Add item animation
            itemAnimator?.apply {
                addDuration = 300
                moveDuration = 300
                changeDuration = 300
                removeDuration = 300
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.floors.collectLatest { floors ->
                floorsAdapter.submitList(floors)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 
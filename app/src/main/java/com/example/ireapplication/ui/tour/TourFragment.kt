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
        setupRecyclerView()
        observeViewModel()
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
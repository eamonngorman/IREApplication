package com.example.ireapplication.ui.tour

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ireapplication.databinding.FragmentFloorExhibitsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FloorExhibitsFragment : Fragment() {
    private var _binding: FragmentFloorExhibitsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FloorExhibitsViewModel by viewModels()
    private val args: FloorExhibitsFragmentArgs by navArgs()
    private lateinit var exhibitsAdapter: ExhibitsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFloorExhibitsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupRecyclerView()
        observeViewModel()
        viewModel.loadFloor(args.floorId)
    }

    private fun setupToolbar() {
        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    private fun setupRecyclerView() {
        exhibitsAdapter = ExhibitsAdapter { exhibit ->
            val action = FloorExhibitsFragmentDirections
                .actionFloorExhibitsToExhibitDetail(exhibit.id)
            findNavController().navigate(action)
        }
        
        binding.exhibitsRecyclerView.apply {
            adapter = exhibitsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun observeViewModel() {
        viewModel.floor.observe(viewLifecycleOwner) { floor ->
            floor?.let {
                binding.floorTitle.text = it.name
                binding.floorDescription.text = it.description
                (activity as? AppCompatActivity)?.supportActionBar?.title = it.name
            }
        }

        viewModel.exhibits.observe(viewLifecycleOwner) { exhibits ->
            exhibitsAdapter.submitList(exhibits)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 
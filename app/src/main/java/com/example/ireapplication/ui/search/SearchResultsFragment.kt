package com.example.ireapplication.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.ireapplication.databinding.FragmentSearchResultsBinding
import com.example.ireapplication.ui.tour.ExhibitsAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchResultsFragment : Fragment() {
    private var _binding: FragmentSearchResultsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModels()
    private val args: SearchResultsFragmentArgs by navArgs()
    private lateinit var searchResultsAdapter: ExhibitsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchResultsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
        viewModel.searchExhibits(args.query)
    }

    private fun setupRecyclerView() {
        searchResultsAdapter = ExhibitsAdapter { exhibit ->
            val action = SearchResultsFragmentDirections
                .actionSearchResultsToExhibitDetail(exhibit.id)
            findNavController().navigate(action)
        }

        binding.searchResultsRecyclerView.adapter = searchResultsAdapter
    }

    private fun observeViewModel() {
        viewModel.searchResults.observe(viewLifecycleOwner) { exhibits ->
            binding.noResultsText.isVisible = exhibits.isEmpty()
            binding.searchResultsRecyclerView.isVisible = exhibits.isNotEmpty()
            searchResultsAdapter.submitList(exhibits)
            
            binding.searchResultsTitle.text = if (exhibits.isEmpty()) {
                "No results for '${args.query}'"
            } else {
                "Search results for '${args.query}'"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 
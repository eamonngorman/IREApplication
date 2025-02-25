package com.example.ireapplication.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ireapplication.data.models.Exhibit
import com.example.ireapplication.data.repository.ExhibitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val exhibitRepository: ExhibitRepository
) : ViewModel() {

    private val _searchResults = MutableLiveData<List<Exhibit>>()
    val searchResults: LiveData<List<Exhibit>> = _searchResults

    fun searchExhibits(query: String) {
        viewModelScope.launch {
            exhibitRepository.searchExhibits(query).collectLatest { exhibits ->
                _searchResults.value = exhibits
            }
        }
    }
} 
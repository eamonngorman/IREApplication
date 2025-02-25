package com.example.ireapplication.ui.tour

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ireapplication.data.models.Exhibit
import com.example.ireapplication.data.repository.IRERepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExhibitDetailViewModel @Inject constructor(
    private val repository: IRERepository
) : ViewModel() {
    
    private val _exhibit = MutableLiveData<Exhibit>()
    val exhibit: LiveData<Exhibit> = _exhibit

    fun loadExhibit(exhibitId: Int) {
        viewModelScope.launch {
            repository.getExhibit(exhibitId)?.let { exhibit ->
                _exhibit.value = exhibit
            }
        }
    }
} 
package com.example.ireapplication.ui.tour

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ireapplication.data.models.Floor
import com.example.ireapplication.data.models.Exhibit
import com.example.ireapplication.data.repository.IRERepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FloorExhibitsViewModel @Inject constructor(
    private val repository: IRERepository
) : ViewModel() {

    private val _floor = MutableLiveData<Floor>()
    val floor: LiveData<Floor> = _floor

    private val _exhibits = MutableLiveData<List<Exhibit>>()
    val exhibits: LiveData<List<Exhibit>> = _exhibits

    fun loadFloor(floorId: Int) {
        viewModelScope.launch {
            // Load floor details
            repository.getFloorById(floorId)?.let { floor ->
                _floor.value = floor
            }

            // Load floor's exhibits
            repository.getExhibitsForFloor(floorId).collectLatest { exhibits ->
                _exhibits.value = exhibits
            }
        }
    }
} 
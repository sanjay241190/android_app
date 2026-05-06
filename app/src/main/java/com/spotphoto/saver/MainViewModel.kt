package com.spotphoto.saver

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.spotphoto.saver.data.PhotoSpot
import com.spotphoto.saver.data.PhotoSpotRepository
import com.spotphoto.saver.widget.WidgetUpdater
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PhotoSpotRepository =
        (application as PhotoSpotApp).repository

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    val spots: StateFlow<List<PhotoSpot>> = _selectedCategory
        .flatMapLatest { category ->
            if (category == null) repository.allSpots
            else repository.getSpotsByCategory(category)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedSpot = MutableStateFlow<PhotoSpot?>(null)
    val selectedSpot: StateFlow<PhotoSpot?> = _selectedSpot.asStateFlow()

    fun setCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun saveSpot(
        photoPath: String,
        latitude: Double,
        longitude: Double,
        bearing: Float,
        direction: String,
        category: String
    ) {
        viewModelScope.launch {
            repository.insert(
                PhotoSpot(
                    photoPath = photoPath,
                    latitude = latitude,
                    longitude = longitude,
                    compassBearing = bearing,
                    compassDirection = direction,
                    category = category
                )
            )
            WidgetUpdater.updateWidget(getApplication())
        }
    }

    fun deleteSpot(spot: PhotoSpot) {
        viewModelScope.launch {
            repository.delete(spot)
            java.io.File(spot.photoPath).delete()
            WidgetUpdater.updateWidget(getApplication())
        }
    }

    fun updateNote(spot: PhotoSpot, note: String) {
        viewModelScope.launch {
            val updated = spot.copy(note = note)
            repository.update(updated)
            _selectedSpot.value = updated
        }
    }

    fun updateCategory(spot: PhotoSpot, category: String) {
        viewModelScope.launch {
            val updated = spot.copy(category = category)
            repository.update(updated)
            _selectedSpot.value = updated
        }
    }

    fun selectSpot(spot: PhotoSpot) {
        _selectedSpot.value = spot
    }

    fun clearSelection() {
        _selectedSpot.value = null
    }
}

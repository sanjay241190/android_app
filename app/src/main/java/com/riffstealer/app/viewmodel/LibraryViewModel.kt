package com.riffstealer.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.riffstealer.app.data.RiffDatabase
import com.riffstealer.app.data.RiffRepository
import com.riffstealer.app.music.AbcParser
import com.riffstealer.app.music.ToneSynthesizer
import com.riffstealer.app.ui.screens.LibraryMelodyUiState
import com.riffstealer.app.ui.screens.LibraryVariationUiState
import com.riffstealer.app.ui.screens.VariationUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LibraryViewModel(application: Application) : AndroidViewModel(application) {

    private val db = RiffDatabase.getDatabase(application)
    private val repository = RiffRepository(db.riffDao())
    private val abcParser = AbcParser()
    private val synthesizer = ToneSynthesizer()

    private val _melodies = MutableStateFlow<List<LibraryMelodyUiState>>(emptyList())
    val melodies: StateFlow<List<LibraryMelodyUiState>> = _melodies

    private val _favorites = MutableStateFlow<List<LibraryVariationUiState>>(emptyList())
    val favorites: StateFlow<List<LibraryVariationUiState>> = _favorites

    private val _melodyVariations = MutableStateFlow<List<VariationUiState>>(emptyList())
    val melodyVariations: StateFlow<List<VariationUiState>> = _melodyVariations

    private val _playingIndex = MutableStateFlow<Int?>(null)
    val playingIndex: StateFlow<Int?> = _playingIndex

    private var currentVariationAbcs = listOf<String>()

    init {
        viewModelScope.launch {
            repository.allMelodies.collectLatest { entities ->
                _melodies.value = entities.map { e ->
                    val noteCount = e.noteSequence.split(",").filter { it.isNotBlank() }.size
                    LibraryMelodyUiState(
                        id = e.id,
                        name = e.name,
                        noteCount = noteCount,
                        bpm = e.bpm,
                        createdAt = e.createdAt
                    )
                }
            }
        }
        viewModelScope.launch {
            repository.getFavorites().collectLatest { entities ->
                _favorites.value = entities.map { e ->
                    LibraryVariationUiState(
                        id = e.id,
                        melodyId = e.melodyId,
                        genre = e.genre,
                        mood = e.mood,
                        tempo = e.tempo
                    )
                }
            }
        }
    }

    fun loadVariationsForMelody(melodyId: Long) {
        viewModelScope.launch {
            repository.getVariations(melodyId).collectLatest { entities ->
                currentVariationAbcs = entities.map { it.abcNotation }
                _melodyVariations.value = entities.map { e ->
                    VariationUiState(
                        genre = e.genre,
                        mood = e.mood,
                        tempo = e.tempo,
                        description = "",
                        isFavorite = e.isFavorite
                    )
                }
            }
        }
    }

    fun playVariation(index: Int) {
        val abc = currentVariationAbcs.getOrNull(index) ?: return

        if (_playingIndex.value == index && synthesizer.isPlaying.value) {
            synthesizer.stop()
            _playingIndex.value = null
            return
        }

        _playingIndex.value = index
        viewModelScope.launch {
            val melody = abcParser.parse(abc)
            synthesizer.play(melody)
            _playingIndex.value = null
        }
    }

    fun toggleFavorite(index: Int) {
        val current = _melodyVariations.value.toMutableList()
        if (index in current.indices) {
            current[index] = current[index].copy(isFavorite = !current[index].isFavorite)
            _melodyVariations.value = current
        }
    }

    override fun onCleared() {
        super.onCleared()
        synthesizer.release()
    }
}

package com.riffstealer.app.viewmodel

import android.app.Application
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.riffstealer.app.data.MelodyEntity
import com.riffstealer.app.data.RiffDatabase
import com.riffstealer.app.data.RiffRepository
import com.riffstealer.app.data.VariationEntity
import com.riffstealer.app.music.ToneSynthesizer
import com.riffstealer.app.music.Variation
import com.riffstealer.app.ui.screens.VariationUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class VariationsViewModel(application: Application) : AndroidViewModel(application) {

    private val synthesizer = ToneSynthesizer()
    private val db = RiffDatabase.getDatabase(application)
    private val repository = RiffRepository(db.riffDao())

    private val variationData = mutableListOf<Variation>()
    private var abcNotation: String = ""
    private var bpm: Int = 120
    private var savedMelodyId: Long? = null

    private val _variationStates = MutableStateFlow<List<VariationUiState>>(emptyList())
    val variations: StateFlow<List<VariationUiState>> = _variationStates

    private val _currentlyPlayingIndex = MutableStateFlow<Int?>(null)
    val currentlyPlayingIndex: StateFlow<Int?> = _currentlyPlayingIndex

    val isPlaying: StateFlow<Boolean> = synthesizer.isPlaying
    val playbackProgress: StateFlow<Float> = synthesizer.progress

    fun loadFromRecording(recordingViewModel: RecordingViewModel) {
        val generated = recordingViewModel.generatedVariations.value
        variationData.clear()
        variationData.addAll(generated)
        abcNotation = recordingViewModel.abcNotation.value
        bpm = recordingViewModel.detectedBpm.value

        _variationStates.value = variationData.map { v ->
            VariationUiState(
                genre = v.genre,
                mood = v.mood,
                tempo = v.tempo,
                description = v.description,
                isFavorite = false
            )
        }
    }

    fun getVariation(index: Int) = MutableStateFlow(
        variationData.getOrNull(index)
    )

    fun playVariation(index: Int) {
        if (_currentlyPlayingIndex.value == index && synthesizer.isPlaying.value) {
            synthesizer.stop()
            _currentlyPlayingIndex.value = null
            return
        }

        val variation = variationData.getOrNull(index) ?: return
        _currentlyPlayingIndex.value = index

        viewModelScope.launch {
            synthesizer.play(variation.melody)
            _currentlyPlayingIndex.value = null
        }
    }

    fun toggleFavorite(index: Int) {
        val current = _variationStates.value.toMutableList()
        if (index in current.indices) {
            current[index] = current[index].copy(isFavorite = !current[index].isFavorite)
            _variationStates.value = current
        }
    }

    fun saveAll() {
        viewModelScope.launch(Dispatchers.IO) {
            val melodyEntity = MelodyEntity(
                name = "Melody ${System.currentTimeMillis() / 1000}",
                abcNotation = abcNotation,
                bpm = bpm,
                noteSequence = "",
                durationMs = variationData.firstOrNull()?.melody?.durationMs ?: 0L
            )
            val melodyId = repository.saveMelody(melodyEntity)
            savedMelodyId = melodyId

            val entities = variationData.mapIndexed { i, v ->
                VariationEntity(
                    melodyId = melodyId,
                    genre = v.genre,
                    mood = v.mood,
                    tempo = v.tempo,
                    abcNotation = v.abcNotation,
                    description = v.description,
                    isFavorite = _variationStates.value.getOrNull(i)?.isFavorite ?: false
                )
            }
            repository.saveVariations(entities)
        }
    }

    fun saveVariation(index: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val v = variationData.getOrNull(index) ?: return@launch

            val melodyId = savedMelodyId ?: run {
                val melodyEntity = MelodyEntity(
                    name = "Melody ${System.currentTimeMillis() / 1000}",
                    abcNotation = abcNotation,
                    bpm = bpm,
                    noteSequence = "",
                    durationMs = v.melody.durationMs
                )
                repository.saveMelody(melodyEntity).also { savedMelodyId = it }
            }

            val entity = VariationEntity(
                melodyId = melodyId,
                genre = v.genre,
                mood = v.mood,
                tempo = v.tempo,
                abcNotation = v.abcNotation,
                description = v.description,
                isFavorite = _variationStates.value.getOrNull(index)?.isFavorite ?: false
            )
            repository.saveVariations(listOf(entity))
        }
    }

    fun exportVariation(index: Int) {
        val v = variationData.getOrNull(index) ?: return
        val context = getApplication<Application>()

        viewModelScope.launch(Dispatchers.IO) {
            val file = File(context.cacheDir, "${v.genre.lowercase()}_variation.abc")
            file.writeText(v.abcNotation)

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "RiffStealer - ${v.genre} Variation")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, "Export Variation").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
    }

    override fun onCleared() {
        super.onCleared()
        synthesizer.release()
    }
}

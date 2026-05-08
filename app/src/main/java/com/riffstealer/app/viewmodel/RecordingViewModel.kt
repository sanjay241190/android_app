package com.riffstealer.app.viewmodel

import android.Manifest
import android.app.Application
import android.content.SharedPreferences
import androidx.annotation.RequiresPermission
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.riffstealer.app.BuildConfig
import com.riffstealer.app.ai.GeminiApiClient
import com.riffstealer.app.ai.VariationGenerator
import com.riffstealer.app.audio.AudioCaptureManager
import com.riffstealer.app.audio.PitchDetector
import com.riffstealer.app.audio.RhythmAnalyzer
import com.riffstealer.app.data.MelodyEntity
import com.riffstealer.app.data.RiffDatabase
import com.riffstealer.app.data.RiffRepository
import com.riffstealer.app.data.VariationEntity
import com.riffstealer.app.music.MelodyEncoder
import com.riffstealer.app.music.Variation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class RecordingViewModel(application: Application) : AndroidViewModel(application) {

    private val audioCaptureManager = AudioCaptureManager()
    private val pitchDetector = PitchDetector()
    private val rhythmAnalyzer = RhythmAnalyzer()
    private val melodyEncoder = MelodyEncoder()
    private val geminiApiClient = GeminiApiClient()
    private val variationGenerator = VariationGenerator(geminiApiClient)

    private val db = RiffDatabase.getDatabase(application)
    private val repository = RiffRepository(db.riffDao())

    private val prefs: SharedPreferences = createEncryptedPrefs(application)

    val amplitudes: StateFlow<FloatArray> = audioCaptureManager.amplitudes
    val isRecording: StateFlow<Boolean> = audioCaptureManager.isRecording

    private val _detectedNoteNames = MutableStateFlow<List<String>>(emptyList())
    val detectedNoteNames: StateFlow<List<String>> = _detectedNoteNames

    private val _elapsedMs = MutableStateFlow(0L)
    val elapsedMs: StateFlow<Long> = _elapsedMs

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating

    private val _detectedBpm = MutableStateFlow(120)
    val detectedBpm: StateFlow<Int> = _detectedBpm

    private val _melodyDurationMs = MutableStateFlow(0L)
    val melodyDurationMs: StateFlow<Long> = _melodyDurationMs

    private val _abcNotation = MutableStateFlow("")
    val abcNotation: StateFlow<String> = _abcNotation

    private val _generatedVariations = MutableStateFlow<List<Variation>>(emptyList())
    val generatedVariations: StateFlow<List<Variation>> = _generatedVariations

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _apiKey = MutableStateFlow(
        prefs.getString("api_key", null) ?: BuildConfig.GEMINI_API_KEY
    )
    val apiKey: StateFlow<String> = _apiKey

    private val pitchBuffer = mutableListOf<RhythmAnalyzer.TimedPitch>()
    private var recordingStartMs = 0L
    private var recordingJob: Job? = null
    private var timerJob: Job? = null

    init {
        geminiApiClient.setApiKey(_apiKey.value)
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun startRecording() {
        pitchBuffer.clear()
        _detectedNoteNames.value = emptyList()
        _elapsedMs.value = 0L
        _isAnalyzing.value = false
        _isGenerating.value = false
        _abcNotation.value = ""
        _generatedVariations.value = emptyList()
        _error.value = null
        recordingStartMs = System.currentTimeMillis()

        timerJob = viewModelScope.launch {
            while (isActive && audioCaptureManager.isRecording.value) {
                _elapsedMs.value = System.currentTimeMillis() - recordingStartMs
                delay(100)
            }
        }

        recordingJob = viewModelScope.launch(Dispatchers.Default) {
            audioCaptureManager.captureAudioStream { buffer, size ->
                val result = pitchDetector.detectPitch(buffer, size)
                val timestampMs = System.currentTimeMillis() - recordingStartMs

                pitchBuffer.add(
                    RhythmAnalyzer.TimedPitch(
                        frequency = result.frequency,
                        confidence = result.confidence,
                        timestampMs = timestampMs,
                        isPitched = result.isPitched
                    )
                )

                if (result.isPitched && result.confidence > 0.5f) {
                    val note = com.riffstealer.app.music.Note.fromFrequency(result.frequency, 0)
                    val currentNames = _detectedNoteNames.value.toMutableList()
                    val fullName = note.fullName
                    if (currentNames.lastOrNull() != fullName) {
                        currentNames.add(fullName)
                        if (currentNames.size > 50) {
                            currentNames.removeAt(0)
                        }
                        _detectedNoteNames.value = currentNames
                    }
                }
            }
        }
    }

    fun stopRecording() {
        audioCaptureManager.stopRecording()
        recordingJob?.cancel()
        timerJob?.cancel()
        analyzeMelody()
    }

    private fun analyzeMelody() {
        viewModelScope.launch(Dispatchers.Default) {
            _isAnalyzing.value = true

            val notes = rhythmAnalyzer.analyzePitches(pitchBuffer)
            val bpm = rhythmAnalyzer.estimateBpm(notes)
            val melody = rhythmAnalyzer.buildMelody(notes, bpm)

            _detectedBpm.value = bpm
            _melodyDurationMs.value = melody.durationMs
            _detectedNoteNames.value = notes
                .filter { it.pitchName != "R" }
                .map { it.fullName }

            val abc = melodyEncoder.encode(melody)
            _abcNotation.value = abc

            _isAnalyzing.value = false

            if (geminiApiClient.hasApiKey()) {
                generateVariations(abc, bpm)
            }
        }
    }

    private suspend fun generateVariations(abc: String, bpm: Int) {
        _isGenerating.value = true

        val result = variationGenerator.generateVariations(abc, bpm)

        result.fold(
            onSuccess = { variations ->
                _generatedVariations.value = variations
            },
            onFailure = { error ->
                _error.value = error.message
            }
        )

        _isGenerating.value = false
    }

    fun updateApiKey(key: String) {
        _apiKey.value = key
    }

    fun saveApiKey() {
        prefs.edit().putString("api_key", _apiKey.value).apply()
        geminiApiClient.setApiKey(_apiKey.value)
    }

    suspend fun saveMelodyToLibrary(name: String): Long {
        val melodyEntity = MelodyEntity(
            name = name,
            abcNotation = _abcNotation.value,
            bpm = _detectedBpm.value,
            noteSequence = _detectedNoteNames.value.joinToString(","),
            durationMs = _melodyDurationMs.value
        )
        val melodyId = repository.saveMelody(melodyEntity)

        val variationEntities = _generatedVariations.value.map { v ->
            VariationEntity(
                melodyId = melodyId,
                genre = v.genre,
                mood = v.mood,
                tempo = v.tempo,
                abcNotation = v.abcNotation,
                description = v.description
            )
        }
        repository.saveVariations(variationEntities)

        return melodyId
    }

    override fun onCleared() {
        super.onCleared()
        audioCaptureManager.release()
    }

    companion object {
        private fun createEncryptedPrefs(application: Application): SharedPreferences {
            val masterKey = MasterKey.Builder(application)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            return EncryptedSharedPreferences.create(
                application,
                "riffstealer_secure_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }
    }
}

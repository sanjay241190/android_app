package com.riffstealer.app.audio

import android.Manifest
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext

class AudioCaptureManager {

    companion object {
        const val SAMPLE_RATE = 44100
        const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        const val BUFFER_OVERLAP_FACTOR = 2
    }

    private var audioRecord: AudioRecord? = null
    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording

    private val _amplitudes = MutableStateFlow(FloatArray(0))
    val amplitudes: StateFlow<FloatArray> = _amplitudes

    val bufferSize: Int = AudioRecord.getMinBufferSize(
        SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT
    ) * BUFFER_OVERLAP_FACTOR

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun startRecording(): Boolean {
        if (_isRecording.value) return false

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT,
            bufferSize
        )

        if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
            audioRecord?.release()
            audioRecord = null
            return false
        }

        audioRecord?.startRecording()
        _isRecording.value = true
        return true
    }

    fun stopRecording() {
        _isRecording.value = false
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }

    suspend fun captureAudioStream(
        onBuffer: (ShortArray, Int) -> Unit
    ) = withContext(Dispatchers.IO) {
        if (!_isRecording.value) return@withContext

        val buffer = ShortArray(bufferSize / 2)

        while (isActive && _isRecording.value) {
            val readCount = audioRecord?.read(buffer, 0, buffer.size) ?: -1
            if (readCount > 0) {
                onBuffer(buffer.copyOf(readCount), readCount)
                updateAmplitudes(buffer, readCount)
            }
        }
    }

    private fun updateAmplitudes(buffer: ShortArray, size: Int) {
        val step = maxOf(1, size / 100)
        val amps = FloatArray(minOf(100, size)) { i ->
            val idx = i * step
            if (idx < size) buffer[idx].toFloat() / Short.MAX_VALUE else 0f
        }
        _amplitudes.value = amps
    }

    fun release() {
        stopRecording()
    }
}

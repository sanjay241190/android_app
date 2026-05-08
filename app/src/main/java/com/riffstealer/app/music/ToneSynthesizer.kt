package com.riffstealer.app.music

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.PI
import kotlin.math.sin

class ToneSynthesizer {

    companion object {
        private const val SAMPLE_RATE = 44100
    }

    private var audioTrack: AudioTrack? = null
    private var playbackJob: Job? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress

    fun generateAudio(melody: Melody): ShortArray {
        val totalSamples = melody.notes.sumOf { noteSampleCount(it.durationMs) }
        val buffer = ShortArray(totalSamples)
        var offset = 0

        for (note in melody.notes) {
            val samples = noteSampleCount(note.durationMs)
            if (note.pitchName == "R" || note.frequency <= 0f) {
                // Rest — leave as silence (zeros)
                offset += samples
                continue
            }
            synthesizeNote(buffer, offset, note.frequency, samples, note.velocity)
            offset += samples
        }

        return buffer
    }

    suspend fun play(melody: Melody) = coroutineScope {
        stop()
        val buffer = withContext(Dispatchers.Default) { generateAudio(melody) }
        playBuffer(buffer)
    }

    suspend fun playBuffer(buffer: ShortArray) = coroutineScope {
        stop()

        val minBuf = AudioTrack.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setSampleRate(SAMPLE_RATE)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(maxOf(minBuf, buffer.size * 2))
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        audioTrack?.write(buffer, 0, buffer.size)

        playbackJob = launch(Dispatchers.IO) {
            _isPlaying.value = true
            audioTrack?.play()

            val totalMs = (buffer.size.toLong() * 1000) / SAMPLE_RATE
            val startTime = System.currentTimeMillis()
            while (isActive && _isPlaying.value) {
                val elapsed = System.currentTimeMillis() - startTime
                _progress.value = (elapsed.toFloat() / totalMs).coerceIn(0f, 1f)
                if (elapsed >= totalMs) break
                kotlinx.coroutines.delay(50)
            }

            _isPlaying.value = false
            _progress.value = 0f
        }
    }

    fun stop() {
        playbackJob?.cancel()
        playbackJob = null
        _isPlaying.value = false
        _progress.value = 0f
        try {
            audioTrack?.stop()
        } catch (_: IllegalStateException) { }
        audioTrack?.release()
        audioTrack = null
    }

    private fun synthesizeNote(
        buffer: ShortArray,
        offset: Int,
        frequency: Float,
        samples: Int,
        velocity: Int
    ) {
        val amplitude = (Short.MAX_VALUE * (velocity / 127f) * 0.8f).toInt()
        val attackSamples = minOf(samples / 10, SAMPLE_RATE / 50)
        val releaseSamples = minOf(samples / 5, SAMPLE_RATE / 20)

        for (i in 0 until samples) {
            if (offset + i >= buffer.size) break

            val time = i.toDouble() / SAMPLE_RATE
            // Fundamental + soft harmonics for a warmer tone
            var sample = sin(2.0 * PI * frequency * time)
            sample += 0.3 * sin(4.0 * PI * frequency * time) // 2nd harmonic
            sample += 0.1 * sin(6.0 * PI * frequency * time) // 3rd harmonic
            sample /= 1.4 // normalize

            // ADSR-lite envelope
            val envelope = when {
                i < attackSamples -> i.toFloat() / attackSamples
                i > samples - releaseSamples -> (samples - i).toFloat() / releaseSamples
                else -> 1f
            }

            buffer[offset + i] = (sample * amplitude * envelope).toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                .toShort()
        }
    }

    private fun noteSampleCount(durationMs: Long): Int =
        (SAMPLE_RATE * durationMs / 1000).toInt()

    fun release() {
        stop()
    }
}

package com.riffstealer.app.audio

import com.riffstealer.app.music.Melody
import com.riffstealer.app.music.Note

class RhythmAnalyzer {

    data class TimedPitch(
        val frequency: Float,
        val confidence: Float,
        val timestampMs: Long,
        val isPitched: Boolean
    )

    /**
     * Converts a stream of raw pitch detections into quantized notes
     * with proper durations, merging consecutive same-pitch frames.
     */
    fun analyzePitches(
        pitches: List<TimedPitch>,
        minNoteDurationMs: Long = 80
    ): List<Note> {
        if (pitches.isEmpty()) return emptyList()

        val notes = mutableListOf<Note>()
        var currentNote: String? = null
        var currentOctave: Int = 0
        var noteStartMs: Long = pitches.first().timestampMs
        var currentFreq: Float = 0f

        for (pitch in pitches) {
            val detected = if (pitch.isPitched && pitch.confidence > 0.5f) {
                Note.fromFrequency(pitch.frequency, 0)
            } else null

            val detectedName = detected?.pitchName
            val detectedOctave = detected?.octave ?: 0

            if (detectedName != currentNote || detectedOctave != currentOctave) {
                if (currentNote != null) {
                    val duration = pitch.timestampMs - noteStartMs
                    if (duration >= minNoteDurationMs) {
                        notes.add(Note(currentNote, currentOctave, currentFreq, duration))
                    }
                }
                currentNote = detectedName
                currentOctave = detectedOctave
                currentFreq = pitch.frequency
                noteStartMs = pitch.timestampMs
            }
        }

        // Add the last note
        if (currentNote != null && pitches.isNotEmpty()) {
            val duration = pitches.last().timestampMs - noteStartMs + 50
            if (duration >= minNoteDurationMs) {
                notes.add(Note(currentNote, currentOctave, currentFreq, duration))
            }
        }

        return notes
    }

    /**
     * Estimates BPM from note onset intervals using autocorrelation.
     */
    fun estimateBpm(notes: List<Note>): Int {
        if (notes.size < 3) return 120

        val intervals = mutableListOf<Long>()
        var accumulatedMs: Long = 0
        for (note in notes) {
            intervals.add(accumulatedMs)
            accumulatedMs += note.durationMs
        }

        // Compute inter-onset intervals
        val iois = mutableListOf<Long>()
        for (i in 1 until intervals.size) {
            iois.add(intervals[i] - intervals[i - 1])
        }

        if (iois.isEmpty()) return 120

        // Find the most common interval via clustering
        val medianIoi = iois.sorted()[iois.size / 2]

        // Convert to BPM (one beat = one note onset interval)
        val bpm = (60000.0 / medianIoi).toInt().coerceIn(40, 240)
        return quantizeBpm(bpm)
    }

    /**
     * Snap to common BPM values for cleaner output.
     */
    private fun quantizeBpm(bpm: Int): Int {
        val commonBpms = intArrayOf(60, 70, 80, 90, 100, 110, 120, 130, 140, 150, 160, 170, 180, 200)
        return commonBpms.minByOrNull { kotlin.math.abs(it - bpm) } ?: bpm
    }

    fun buildMelody(notes: List<Note>, bpm: Int): Melody {
        return Melody(notes = notes, bpm = bpm)
    }
}

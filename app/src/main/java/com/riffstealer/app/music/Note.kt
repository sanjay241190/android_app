package com.riffstealer.app.music

data class Note(
    val pitchName: String,
    val octave: Int,
    val frequency: Float,
    val durationMs: Long,
    val velocity: Int = 80
) {
    val midiNumber: Int
        get() {
            val baseNote = when (pitchName.uppercase().trimEnd('#', 'b')) {
                "C" -> 0; "D" -> 2; "E" -> 4; "F" -> 5
                "G" -> 7; "A" -> 9; "B" -> 11
                else -> 0
            }
            val modifier = when {
                pitchName.endsWith("#") -> 1
                pitchName.endsWith("b") -> -1
                else -> 0
            }
            return (octave + 1) * 12 + baseNote + modifier
        }

    val fullName: String get() = "$pitchName$octave"

    companion object {
        private val NOTE_NAMES = arrayOf(
            "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"
        )

        fun fromFrequency(frequency: Float, durationMs: Long): Note {
            if (frequency <= 0f) return Note("R", 0, 0f, durationMs) // rest
            val midiNum = (12 * Math.log(frequency / 440.0) / Math.log(2.0) + 69).toInt()
            val noteIndex = ((midiNum % 12) + 12) % 12
            val octave = (midiNum / 12) - 1
            return Note(NOTE_NAMES[noteIndex], octave, frequency, durationMs)
        }

        fun fromMidiNumber(midi: Int, durationMs: Long): Note {
            val noteIndex = ((midi % 12) + 12) % 12
            val octave = (midi / 12) - 1
            val freq = (440.0 * Math.pow(2.0, (midi - 69.0) / 12.0)).toFloat()
            return Note(NOTE_NAMES[noteIndex], octave, freq, durationMs)
        }
    }
}

data class Melody(
    val notes: List<Note>,
    val bpm: Int,
    val timeSignatureNumerator: Int = 4,
    val timeSignatureDenominator: Int = 4
) {
    val durationMs: Long get() = notes.sumOf { it.durationMs }
}

data class Variation(
    val melody: Melody,
    val genre: String,
    val mood: String,
    val tempo: Int,
    val abcNotation: String,
    val description: String
)

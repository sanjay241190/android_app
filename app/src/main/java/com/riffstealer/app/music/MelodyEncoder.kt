package com.riffstealer.app.music

/**
 * Encodes a Melody into ABC notation format and vice versa.
 * ABC notation reference: https://abcnotation.com/wiki/abc:standard:v2.1
 */
class MelodyEncoder {

    fun encode(melody: Melody): String {
        val sb = StringBuilder()
        sb.appendLine("X:1")
        sb.appendLine("T:Captured Melody")
        sb.appendLine("M:${melody.timeSignatureNumerator}/${melody.timeSignatureDenominator}")
        sb.appendLine("L:1/8")
        sb.appendLine("Q:1/4=${melody.bpm}")
        sb.appendLine("K:C")

        val beatDurationMs = 60000.0 / melody.bpm
        val eighthNoteDurationMs = beatDurationMs / 2.0

        for (note in melody.notes) {
            if (note.pitchName == "R") {
                val abcDuration = noteDurationToAbc(note.durationMs, eighthNoteDurationMs)
                sb.append("z$abcDuration ")
                continue
            }
            val abcPitch = pitchToAbc(note.pitchName, note.octave)
            val abcDuration = noteDurationToAbc(note.durationMs, eighthNoteDurationMs)
            sb.append("$abcPitch$abcDuration ")
        }

        return sb.toString().trim()
    }

    fun noteSequenceString(melody: Melody): String {
        return melody.notes
            .filter { it.pitchName != "R" }
            .joinToString(", ") { it.fullName }
    }

    private fun pitchToAbc(name: String, octave: Int): String {
        val baseName = name.trimEnd('#', 'b')
        val accidental = when {
            name.endsWith("#") -> "^"
            name.endsWith("b") -> "_"
            else -> ""
        }

        // ABC notation: C D E F G A B = octave 4
        // c d e f g a b = octave 5
        // C, D, = octave 3, c' d' = octave 6
        return when {
            octave <= 3 -> {
                val commas = ",".repeat(4 - octave)
                "$accidental${baseName.uppercase()}$commas"
            }
            octave == 4 -> "$accidental${baseName.uppercase()}"
            octave == 5 -> "$accidental${baseName.lowercase()}"
            else -> {
                val apostrophes = "'".repeat(octave - 5)
                "$accidental${baseName.lowercase()}$apostrophes"
            }
        }
    }

    private fun noteDurationToAbc(durationMs: Long, eighthNoteDurationMs: Double): String {
        val ratio = durationMs / eighthNoteDurationMs
        val quantized = when {
            ratio < 0.75 -> 1
            ratio < 1.5 -> 1
            ratio < 3.0 -> 2
            ratio < 6.0 -> 4
            ratio < 12.0 -> 8
            else -> 16
        }
        return if (quantized == 1) "" else quantized.toString()
    }
}

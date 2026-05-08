package com.riffstealer.app.music

/**
 * Parses ABC notation back into a Melody object.
 */
class AbcParser {

    fun parse(abc: String): Melody {
        val lines = abc.lines()
        var bpm = 120
        var timeSigNum = 4
        var timeSigDen = 4
        var defaultNoteLength = 8 // denominator: 1/8
        val noteLines = mutableListOf<String>()

        for (line in lines) {
            val trimmed = line.trim()
            when {
                trimmed.startsWith("Q:") -> {
                    val qMatch = Regex("""1/4=(\d+)""").find(trimmed)
                    qMatch?.let { bpm = it.groupValues[1].toInt() }
                }
                trimmed.startsWith("M:") -> {
                    val mMatch = Regex("""(\d+)/(\d+)""").find(trimmed)
                    mMatch?.let {
                        timeSigNum = it.groupValues[1].toInt()
                        timeSigDen = it.groupValues[2].toInt()
                    }
                }
                trimmed.startsWith("L:") -> {
                    val lMatch = Regex("""1/(\d+)""").find(trimmed)
                    lMatch?.let { defaultNoteLength = it.groupValues[1].toInt() }
                }
                !trimmed.startsWith("X:") && !trimmed.startsWith("T:") &&
                    !trimmed.startsWith("K:") && trimmed.isNotEmpty() -> {
                    noteLines.add(trimmed)
                }
            }
        }

        val beatDurationMs = 60000.0 / bpm
        val baseNoteDurationMs = (beatDurationMs * 4.0 / defaultNoteLength).toLong()
        val noteStr = noteLines.joinToString(" ")
        val notes = parseNotes(noteStr, baseNoteDurationMs)

        return Melody(notes, bpm, timeSigNum, timeSigDen)
    }

    private fun parseNotes(noteString: String, baseNoteDurationMs: Long): List<Note> {
        val notes = mutableListOf<Note>()
        val tokens = tokenize(noteString)

        for (token in tokens) {
            val note = parseToken(token, baseNoteDurationMs)
            if (note != null) notes.add(note)
        }
        return notes
    }

    private fun tokenize(noteString: String): List<String> {
        val tokens = mutableListOf<String>()
        val pattern = Regex("""[_^=]?[A-Ga-gz][,']*\d*/??\d*|z\d*/??\d*|\|""")
        pattern.findAll(noteString).forEach { match ->
            val t = match.value
            if (t != "|") tokens.add(t)
        }
        return tokens
    }

    private fun parseToken(token: String, baseMs: Long): Note? {
        if (token.startsWith("z")) {
            val dur = parseDuration(token.removePrefix("z"), baseMs)
            return Note("R", 0, 0f, dur)
        }

        var idx = 0
        // Accidental
        val accidental = when {
            token.startsWith("^") -> { idx++; "#" }
            token.startsWith("_") -> { idx++; "b" }
            token.startsWith("=") -> { idx++; "" }
            else -> ""
        }

        if (idx >= token.length) return null
        val pitchChar = token[idx]
        idx++

        val isLower = pitchChar.isLowerCase()
        val basePitch = pitchChar.uppercaseChar().toString() + accidental
        var octave = if (isLower) 5 else 4

        while (idx < token.length) {
            when (token[idx]) {
                '\'' -> { octave++; idx++ }
                ',' -> { octave--; idx++ }
                else -> break
            }
        }

        val durStr = token.substring(idx)
        val duration = parseDuration(durStr, baseMs)
        val freq = midiToFrequency(pitchToMidi(basePitch, octave))

        return Note(basePitch, octave, freq, duration)
    }

    private fun parseDuration(durStr: String, baseMs: Long): Long {
        if (durStr.isEmpty()) return baseMs
        return try {
            if (durStr.contains("/")) {
                val parts = durStr.split("/")
                val num = parts[0].toIntOrNull() ?: 1
                val den = parts[1].toIntOrNull() ?: 2
                baseMs * num / den
            } else {
                baseMs * durStr.toInt()
            }
        } catch (e: Exception) {
            baseMs
        }
    }

    private fun pitchToMidi(name: String, octave: Int): Int {
        val base = when (name.uppercase().trimEnd('#', 'b')) {
            "C" -> 0; "D" -> 2; "E" -> 4; "F" -> 5
            "G" -> 7; "A" -> 9; "B" -> 11; else -> 0
        }
        val mod = when {
            name.endsWith("#") -> 1
            name.endsWith("b") -> -1
            else -> 0
        }
        return (octave + 1) * 12 + base + mod
    }

    private fun midiToFrequency(midi: Int): Float =
        (440.0 * Math.pow(2.0, (midi - 69.0) / 12.0)).toFloat()
}

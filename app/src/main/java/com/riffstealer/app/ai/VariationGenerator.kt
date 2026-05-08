package com.riffstealer.app.ai

import android.util.Log
import com.riffstealer.app.music.AbcParser
import com.riffstealer.app.music.Variation

class VariationGenerator(private val apiClient: GeminiApiClient) {

    private val abcParser = AbcParser()

    suspend fun generateVariations(abcNotation: String, bpm: Int): Result<List<Variation>> {
        Log.d("VariationGenerator", "Starting generation with ABC: $abcNotation, BPM: $bpm")
        val prompt = buildPrompt(abcNotation, bpm)
        val response = apiClient.sendMessage(prompt)

        return response.fold(
            onSuccess = { text ->
                Log.d("VariationGenerator", "API response received (${text.length} chars)")
                try {
                    val variations = parseVariations(text)
                    Log.d("VariationGenerator", "Parsed ${variations.size} variations")
                    if (variations.isEmpty()) {
                        Log.w("VariationGenerator", "No variations parsed. Raw response:\n${text.take(500)}")
                        Result.failure(Exception("AI returned a response but no valid variations could be parsed. Please try again."))
                    } else {
                        Result.success(variations)
                    }
                } catch (e: Exception) {
                    Log.e("VariationGenerator", "Parse error: ${e.message}")
                    Result.failure(Exception("Failed to parse variations: ${e.message}"))
                }
            },
            onFailure = { error ->
                Log.e("VariationGenerator", "API call failed: ${error.message}")
                Result.failure(error)
            }
        )
    }

    private fun buildPrompt(abc: String, bpm: Int): String = """
You are a music composition expert. I have a melody captured in ABC notation:

```
$abc
```

Original BPM: $bpm

Generate exactly 10 creative variations of this melody, each in a DIFFERENT genre/style. For each variation:

1. Transform the melody to fit the genre authentically (change rhythm, add genre-appropriate ornaments, adjust intervals)
2. Keep the core melodic contour recognizable
3. Adjust tempo to suit the genre

Format your response as exactly 10 blocks, each block structured EXACTLY like this:

---VARIATION---
GENRE: [genre name]
MOOD: [mood descriptor]
TEMPO: [BPM as integer]
DESCRIPTION: [one sentence about this variation]
ABC:
[complete valid ABC notation starting with X: header]
---END---

Use these 10 genres: Jazz, Blues, Classical, Rock, Electronic, Latin, Reggae, Country, Hip-Hop, Folk

Important rules:
- Each ABC block must be complete and valid with X:, T:, M:, L:, Q:, K: headers
- Use only standard ABC notation characters
- Keep melodies playable (reasonable range C3-C6)
- Ensure each variation sounds distinctly different
""".trimIndent()

    private fun parseVariations(response: String): List<Variation> {
        val variations = mutableListOf<Variation>()
        val blocks = response.split("---VARIATION---").drop(1)

        for (block in blocks) {
            val endIndex = block.indexOf("---END---")
            val content = if (endIndex >= 0) block.substring(0, endIndex) else block

            val genre = extractField(content, "GENRE") ?: continue
            val mood = extractField(content, "MOOD") ?: "Unknown"
            val tempo = extractField(content, "TEMPO")?.toIntOrNull() ?: 120
            val description = extractField(content, "DESCRIPTION") ?: ""

            val abcStart = content.indexOf("ABC:")
            if (abcStart < 0) continue
            var abc = content.substring(abcStart + 4).trim()

            // Clean up markdown code fences if present
            abc = abc.replace("```abc", "").replace("```", "").trim()

            if (!abc.contains("X:")) continue

            try {
                val melody = abcParser.parse(abc)
                variations.add(
                    Variation(
                        melody = melody,
                        genre = genre,
                        mood = mood,
                        tempo = tempo,
                        abcNotation = abc,
                        description = description
                    )
                )
            } catch (e: Exception) {
                // Skip unparseable variations
            }
        }

        return variations
    }

    private fun extractField(text: String, field: String): String? {
        val regex = Regex("""$field:\s*(.+)""")
        return regex.find(text)?.groupValues?.get(1)?.trim()
    }
}

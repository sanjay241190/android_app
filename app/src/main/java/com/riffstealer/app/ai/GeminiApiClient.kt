package com.riffstealer.app.ai

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiApiClient(private var apiKey: String = "") {

    companion object {
        private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"
        private const val MODEL = "gemini-2.5-flash-lite"
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    fun setApiKey(key: String) {
        apiKey = key
    }

    fun hasApiKey(): Boolean = apiKey.isNotBlank()

    suspend fun sendMessage(prompt: String): Result<String> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            Log.e("GeminiApiClient", "API key is blank")
            return@withContext Result.failure(IllegalStateException("API key not set"))
        }

        Log.d("GeminiApiClient", "Sending request to Gemini API (key length: ${apiKey.length})")
        try {
            val parts = JSONArray().apply {
                put(JSONObject().apply {
                    put("text", prompt)
                })
            }

            val contents = JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", parts)
                })
            }

            val generationConfig = JSONObject().apply {
                put("maxOutputTokens", 8192)
                put("temperature", 0.9)
            }

            val body = JSONObject().apply {
                put("contents", contents)
                put("generationConfig", generationConfig)
            }

            val url = "$BASE_URL/$MODEL:generateContent?key=$apiKey"

            val request = Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .post(body.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            Log.d("GeminiApiClient", "Response code: ${response.code}")
            if (!response.isSuccessful) {
                val errorMsg = try {
                    val errorJson = JSONObject(responseBody)
                    errorJson.optJSONObject("error")?.optString("message")
                        ?: "API error: ${response.code}"
                } catch (e: Exception) {
                    "API error: ${response.code}"
                }
                Log.e("GeminiApiClient", "API error: $errorMsg")
                return@withContext Result.failure(Exception(errorMsg))
            }

            val json = JSONObject(responseBody)
            val candidates = json.getJSONArray("candidates")
            val firstCandidate = candidates.getJSONObject(0)
            val content = firstCandidate.getJSONObject("content")
            val responseParts = content.getJSONArray("parts")
            val text = responseParts.getJSONObject(0).getString("text")

            Result.success(text)
        } catch (e: Exception) {
            Log.e("GeminiApiClient", "Exception: ${e.message}", e)
            Result.failure(e)
        }
    }
}

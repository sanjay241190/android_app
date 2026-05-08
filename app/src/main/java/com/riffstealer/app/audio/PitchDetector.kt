package com.riffstealer.app.audio

import kotlin.math.abs
import kotlin.math.pow

/**
 * YIN pitch detection algorithm implementation.
 * Based on: "YIN, a fundamental frequency estimator for speech and music"
 * by Alain de Cheveigné and Hideki Kawahara.
 */
class PitchDetector(
    private val sampleRate: Int = AudioCaptureManager.SAMPLE_RATE,
    private val threshold: Float = 0.15f,
    private val minFrequency: Float = 60f,
    private val maxFrequency: Float = 2000f
) {
    private val maxPeriod = (sampleRate / minFrequency).toInt()
    private val minPeriod = (sampleRate / maxFrequency).toInt()

    data class PitchResult(
        val frequency: Float,
        val confidence: Float,
        val isPitched: Boolean
    )

    fun detectPitch(audioBuffer: ShortArray, size: Int): PitchResult {
        val floatBuffer = FloatArray(size) { audioBuffer[it].toFloat() / Short.MAX_VALUE }
        return detectPitch(floatBuffer)
    }

    fun detectPitch(buffer: FloatArray): PitchResult {
        if (buffer.size < maxPeriod * 2) {
            return PitchResult(0f, 0f, false)
        }

        val rmsLevel = calculateRMS(buffer)
        if (rmsLevel < 0.01f) {
            return PitchResult(0f, 0f, false)
        }

        val halfLen = buffer.size / 2
        val yinBuffer = FloatArray(halfLen)

        // Step 2: Difference function
        for (tau in 0 until halfLen) {
            var sum = 0f
            for (i in 0 until halfLen) {
                val delta = buffer[i] - buffer[i + tau]
                sum += delta * delta
            }
            yinBuffer[tau] = sum
        }

        // Step 3: Cumulative mean normalized difference
        yinBuffer[0] = 1f
        var runningSum = 0f
        for (tau in 1 until halfLen) {
            runningSum += yinBuffer[tau]
            yinBuffer[tau] = yinBuffer[tau] * tau / runningSum
        }

        // Step 4: Absolute threshold
        var tauEstimate = -1
        for (tau in minPeriod until minOf(maxPeriod, halfLen)) {
            if (yinBuffer[tau] < threshold) {
                while (tau + 1 < halfLen && yinBuffer[tau + 1] < yinBuffer[tau]) {
                    // walk to the trough
                }
                tauEstimate = tau
                break
            }
        }

        if (tauEstimate == -1) {
            // No pitch found below threshold; find global minimum instead
            var minVal = Float.MAX_VALUE
            for (tau in minPeriod until minOf(maxPeriod, halfLen)) {
                if (yinBuffer[tau] < minVal) {
                    minVal = yinBuffer[tau]
                    tauEstimate = tau
                }
            }
            if (minVal > 0.5f) {
                return PitchResult(0f, 0f, false)
            }
        }

        // Step 5: Parabolic interpolation for sub-sample accuracy
        val betterTau = parabolicInterpolation(yinBuffer, tauEstimate)
        val frequency = sampleRate / betterTau
        val confidence = 1f - (yinBuffer.getOrElse(tauEstimate) { 1f })

        return PitchResult(frequency, confidence.coerceIn(0f, 1f), true)
    }

    private fun parabolicInterpolation(buffer: FloatArray, tau: Int): Float {
        if (tau <= 0 || tau >= buffer.size - 1) return tau.toFloat()
        val s0 = buffer[tau - 1]
        val s1 = buffer[tau]
        val s2 = buffer[tau + 1]
        val adjustment = (s2 - s0) / (2f * (2f * s1 - s2 - s0))
        return tau + adjustment
    }

    private fun calculateRMS(buffer: FloatArray): Float {
        var sum = 0f
        for (sample in buffer) {
            sum += sample * sample
        }
        return kotlin.math.sqrt(sum / buffer.size)
    }
}

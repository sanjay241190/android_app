package com.riffstealer.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun WaveformVisualizer(
    amplitudes: FloatArray,
    modifier: Modifier = Modifier
) {
    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(amplitudes) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 300)
        )
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        val barCount = amplitudes.size
        if (barCount == 0) return@Canvas

        val canvasWidth = size.width
        val canvasHeight = size.height
        val barSpacing = 2.dp.toPx()
        val barWidth = ((canvasWidth - barSpacing * (barCount - 1)) / barCount)
            .coerceAtLeast(1f)
        val progress = animationProgress.value

        amplitudes.forEachIndexed { index, amplitude ->
            val clampedAmplitude = amplitude.coerceIn(0f, 1f) * progress
            val barHeight = (clampedAmplitude * canvasHeight).coerceAtLeast(2f)
            val x = index * (barWidth + barSpacing)
            val y = (canvasHeight - barHeight) / 2f

            val fraction = index.toFloat() / barCount.coerceAtLeast(1)
            val brush = Brush.verticalGradient(
                colors = listOf(
                    lerp(primaryColor, tertiaryColor, fraction),
                    lerp(tertiaryColor, primaryColor, fraction)
                ),
                startY = y,
                endY = y + barHeight
            )

            drawRect(
                brush = brush,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight)
            )
        }
    }
}

private fun lerp(start: Color, stop: Color, fraction: Float): Color {
    val f = fraction.coerceIn(0f, 1f)
    return Color(
        red = start.red + (stop.red - start.red) * f,
        green = start.green + (stop.green - start.green) * f,
        blue = start.blue + (stop.blue - start.blue) * f,
        alpha = start.alpha + (stop.alpha - start.alpha) * f
    )
}

package com.spotphoto.saver.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

/**
 * Static map preview using OpenStreetMap tile server (no API key required).
 * Shows a single tile centered on the given coordinates at zoom level 15.
 */
@Composable
fun MapPreview(
    latitude: Double,
    longitude: Double,
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    cornerRadius: Dp = 12.dp
) {
    val zoom = 15
    val tileUrl = buildStaticMapUrl(latitude, longitude, zoom)

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(cornerRadius))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = tileUrl,
            contentDescription = "Map preview",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Pin overlay
        Icon(
            Icons.Default.Place,
            contentDescription = null,
            tint = Color(0xFFE53935),
            modifier = Modifier
                .size(24.dp)
                .offset(y = (-4).dp)
        )
    }
}

@Composable
fun MapPreviewWide(
    latitude: Double,
    longitude: Double,
    modifier: Modifier = Modifier,
    height: Dp = 160.dp,
    cornerRadius: Dp = 16.dp
) {
    val zoom = 16
    val tileUrl = buildStaticMapUrl(latitude, longitude, zoom)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(cornerRadius))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = tileUrl,
            contentDescription = "Map preview",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Icon(
            Icons.Default.Place,
            contentDescription = null,
            tint = Color(0xFFE53935),
            modifier = Modifier
                .size(36.dp)
                .offset(y = (-6).dp)
        )
    }
}

private fun buildStaticMapUrl(lat: Double, lng: Double, zoom: Int): String {
    // Convert lat/lng to tile numbers
    val n = 1 shl zoom
    val xTile = ((lng + 180.0) / 360.0 * n).toInt()
    val latRad = Math.toRadians(lat)
    val yTile = ((1.0 - Math.log(Math.tan(latRad) + 1.0 / Math.cos(latRad)) / Math.PI) / 2.0 * n).toInt()

    return "https://tile.openstreetmap.org/$zoom/$xTile/$yTile.png"
}

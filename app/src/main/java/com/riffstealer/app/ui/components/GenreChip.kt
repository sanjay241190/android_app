package com.riffstealer.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Piano
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun GenreChip(
    genre: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text = genre,
                style = MaterialTheme.typography.labelLarge
            )
        },
        modifier = modifier,
        leadingIcon = {
            Icon(
                imageVector = genreIcon(genre),
                contentDescription = null
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

private fun genreIcon(genre: String): ImageVector {
    return when (genre.lowercase()) {
        "rock" -> Icons.Filled.ElectricBolt
        "jazz" -> Icons.Filled.Piano
        "blues" -> Icons.Filled.Audiotrack
        "classical" -> Icons.Filled.LibraryMusic
        "electronic", "edm" -> Icons.Filled.Headphones
        "pop" -> Icons.Filled.Album
        "hip-hop", "rap" -> Icons.Filled.QueueMusic
        else -> Icons.Filled.MusicNote
    }
}

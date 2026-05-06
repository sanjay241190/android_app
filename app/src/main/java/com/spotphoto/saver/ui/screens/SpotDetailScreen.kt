package com.spotphoto.saver.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.spotphoto.saver.data.PhotoSpot
import com.spotphoto.saver.data.SpotCategory
import com.spotphoto.saver.ui.components.CategoryPickerRow
import com.spotphoto.saver.ui.components.MapPreviewWide
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpotDetailScreen(
    spot: PhotoSpot,
    onBack: () -> Unit,
    onUpdateNote: (String) -> Unit,
    onUpdateCategory: (String) -> Unit
) {
    val context = LocalContext.current
    var noteText by remember { mutableStateOf(spot.note) }
    var isEditing by remember { mutableStateOf(false) }
    val category = SpotCategory.fromTag(spot.category)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(category.emoji)
                        Text("Spot Details")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Photo
            AsyncImage(
                model = File(spot.photoPath),
                contentDescription = "Spot photo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            // Map preview (clickable — opens in Maps)
            MapPreviewWide(
                latitude = spot.latitude,
                longitude = spot.longitude,
                height = 140.dp
            )

            // Category selector
            CategoryPickerRow(
                selectedCategory = spot.category,
                onCategorySelected = { onUpdateCategory(it) }
            )

            // Note / Label
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Note",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        IconButton(
                            onClick = {
                                if (isEditing) onUpdateNote(noteText)
                                isEditing = !isEditing
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                                contentDescription = "Edit note",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    if (isEditing) {
                        OutlinedTextField(
                            value = noteText,
                            onValueChange = { noteText = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Add a note...") },
                            maxLines = 3
                        )
                    } else {
                        Text(
                            text = noteText.ifBlank { "Tap edit to add a note" },
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (noteText.isBlank())
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Location info
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Location",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )

                    InfoRow(
                        icon = Icons.Default.MyLocation,
                        label = "Coordinates",
                        value = "%.6f, %.6f".format(spot.latitude, spot.longitude)
                    )

                    InfoRow(
                        icon = Icons.Default.Explore,
                        label = "Facing",
                        value = "${spot.compassBearing.toInt()}° ${spot.compassDirection}"
                    )

                    InfoRow(
                        icon = Icons.Default.Schedule,
                        label = "Saved",
                        value = SimpleDateFormat("MMM dd, yyyy 'at' h:mm a", Locale.getDefault())
                            .format(Date(spot.timestamp))
                    )
                }
            }

            // Navigate button
            Button(
                onClick = {
                    val uri = Uri.parse("google.navigation:q=${spot.latitude},${spot.longitude}")
                    val mapIntent = Intent(Intent.ACTION_VIEW, uri).apply {
                        setPackage("com.google.android.apps.maps")
                    }
                    if (mapIntent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(mapIntent)
                    } else {
                        val webUri = Uri.parse(
                            "https://www.google.com/maps/dir/?api=1&destination=${spot.latitude},${spot.longitude}"
                        )
                        context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Navigation, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Navigate Here", fontWeight = FontWeight.SemiBold)
            }

            // Share button
            OutlinedButton(
                onClick = {
                    val shareText = buildString {
                        append("${category.emoji} Photo Spot")
                        if (spot.note.isNotBlank()) append(": ${spot.note}")
                        append("\nCategory: ${category.label}")
                        append("\n\nLocation: ${spot.latitude}, ${spot.longitude}")
                        append("\nFacing: ${spot.compassBearing.toInt()}° ${spot.compassDirection}")
                        append("\n\nGoogle Maps: https://maps.google.com/?q=${spot.latitude},${spot.longitude}")
                    }
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, shareText)
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share Spot"))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Share Location", fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

package com.spotphoto.saver.widget

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.glance.*
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.color.ColorProvider
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.spotphoto.saver.MainActivity
import com.spotphoto.saver.data.PhotoSpot
import com.spotphoto.saver.data.PhotoSpotDatabase
import com.spotphoto.saver.data.SpotCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class LastSpotWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val spot = withContext(Dispatchers.IO) {
            PhotoSpotDatabase.getDatabase(context).photoSpotDao().getLatestSpot()
        }

        provideContent {
            WidgetContent(spot = spot)
        }
    }
}

@Composable
private fun WidgetContent(spot: PhotoSpot?) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(12.dp)
            .background(ColorProvider(day = Color.White, night = Color(0xFF1E1E1E)))
            .cornerRadius(16.dp)
            .clickable(actionStartActivity<MainActivity>()),
        contentAlignment = Alignment.Center
    ) {
        if (spot == null) {
            EmptyWidget()
        } else {
            SpotWidget(spot)
        }
    }
}

@Composable
private fun SpotWidget(spot: PhotoSpot) {
    val category = SpotCategory.fromTag(spot.category)
    val timeText = SimpleDateFormat("MMM dd, h:mm a", Locale.getDefault()).format(Date(spot.timestamp))

    Row(
        modifier = GlanceModifier.fillMaxSize(),
        horizontalAlignment = Alignment.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Photo thumbnail
        val photoFile = File(spot.photoPath)
        if (photoFile.exists()) {
            val bitmap = BitmapFactory.decodeFile(spot.photoPath)
            if (bitmap != null) {
                Image(
                    provider = ImageProvider(bitmap),
                    contentDescription = "Spot photo",
                    modifier = GlanceModifier
                        .size(80.dp)
                        .cornerRadius(12.dp)
                )
            }
        }

        Spacer(modifier = GlanceModifier.width(12.dp))

        // Info
        Column(
            modifier = GlanceModifier.defaultWeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${category.emoji} ${spot.note.ifBlank { category.label }}",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = ColorProvider(day = Color.Black, night = Color.White)
                ),
                maxLines = 1
            )

            Spacer(modifier = GlanceModifier.height(4.dp))

            Text(
                text = "📍 %.4f, %.4f".format(spot.latitude, spot.longitude),
                style = TextStyle(
                    fontSize = 11.sp,
                    color = ColorProvider(day = Color.Gray, night = Color.LightGray)
                ),
                maxLines = 1
            )

            Text(
                text = "🧭 ${spot.compassBearing.toInt()}° ${spot.compassDirection}",
                style = TextStyle(
                    fontSize = 11.sp,
                    color = ColorProvider(day = Color.Gray, night = Color.LightGray)
                )
            )

            Spacer(modifier = GlanceModifier.height(4.dp))

            Text(
                text = timeText,
                style = TextStyle(
                    fontSize = 10.sp,
                    color = ColorProvider(day = Color(0xFF999999), night = Color(0xFF888888))
                )
            )
        }

        // Navigate button
        Box(
            modifier = GlanceModifier
                .size(40.dp)
                .cornerRadius(20.dp)
                .background(ColorProvider(day = Color(0xFF1976D2), night = Color(0xFF64B5F6)))
                .clickable(
                    actionStartActivity(
                        Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("google.navigation:q=${spot.latitude},${spot.longitude}")
                            setPackage("com.google.android.apps.maps")
                        }
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "→",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorProvider(day = Color.White, night = Color.White)
                )
            )
        }
    }
}

@Composable
private fun EmptyWidget() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "📷",
            style = TextStyle(fontSize = 28.sp)
        )
        Spacer(modifier = GlanceModifier.height(8.dp))
        Text(
            text = "No spots saved yet",
            style = TextStyle(
                fontSize = 13.sp,
                color = ColorProvider(day = Color.Gray, night = Color.LightGray)
            )
        )
        Text(
            text = "Tap to open app",
            style = TextStyle(
                fontSize = 11.sp,
                color = ColorProvider(day = Color(0xFF999999), night = Color(0xFF888888))
            )
        )
    }
}

class LastSpotWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = LastSpotWidget()
}

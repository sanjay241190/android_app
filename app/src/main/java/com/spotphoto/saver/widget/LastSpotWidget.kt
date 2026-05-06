package com.spotphoto.saver.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
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
            try {
                PhotoSpotDatabase.getDatabase(context).photoSpotDao().getLatestSpot()
            } catch (e: Exception) {
                null
            }
        }

        val thumbnail: Bitmap? = if (spot != null) {
            withContext(Dispatchers.IO) {
                loadThumbnail(spot.photoPath, 200, 200)
            }
        } else null

        provideContent {
            WidgetContent(spot = spot, thumbnail = thumbnail)
        }
    }
}

private fun loadThumbnail(path: String, reqWidth: Int, reqHeight: Int): Bitmap? {
    return try {
        val file = File(path)
        if (!file.exists()) return null

        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(path, options)

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
        options.inJustDecodeBounds = false
        BitmapFactory.decodeFile(path, options)
    } catch (e: Exception) {
        null
    }
}

private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
    val (height, width) = options.outHeight to options.outWidth
    var inSampleSize = 1
    if (height > reqHeight || width > reqWidth) {
        val halfHeight = height / 2
        val halfWidth = width / 2
        while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
            inSampleSize *= 2
        }
    }
    return inSampleSize
}

@Composable
private fun WidgetContent(spot: PhotoSpot?, thumbnail: Bitmap?) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(12.dp)
            .background(day = android.graphics.Color.WHITE, night = android.graphics.Color.parseColor("#1E1E1E"))
            .clickable(actionStartActivity<MainActivity>()),
        contentAlignment = Alignment.Center
    ) {
        if (spot == null) {
            EmptyWidget()
        } else {
            SpotWidget(spot, thumbnail)
        }
    }
}

@Composable
private fun SpotWidget(spot: PhotoSpot, thumbnail: Bitmap?) {
    val category = SpotCategory.fromTag(spot.category)
    val timeText = SimpleDateFormat("MMM dd, h:mm a", Locale.getDefault()).format(Date(spot.timestamp))

    Row(
        modifier = GlanceModifier.fillMaxSize(),
        horizontalAlignment = Alignment.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (thumbnail != null) {
            Image(
                provider = ImageProvider(thumbnail),
                contentDescription = "Spot photo",
                modifier = GlanceModifier.size(72.dp)
            )
            Spacer(modifier = GlanceModifier.width(12.dp))
        }

        Column(
            modifier = GlanceModifier.defaultWeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${category.emoji} ${spot.note.ifBlank { category.label }}",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                ),
                maxLines = 1
            )

            Spacer(modifier = GlanceModifier.height(4.dp))

            Text(
                text = "%.4f, %.4f".format(spot.latitude, spot.longitude),
                style = TextStyle(fontSize = 11.sp),
                maxLines = 1
            )

            Text(
                text = "${spot.compassBearing.toInt()}° ${spot.compassDirection}",
                style = TextStyle(fontSize = 11.sp)
            )

            Spacer(modifier = GlanceModifier.height(4.dp))

            Text(
                text = timeText,
                style = TextStyle(fontSize = 10.sp)
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
            text = "Photo Spot Saver",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        )
        Spacer(modifier = GlanceModifier.height(8.dp))
        Text(
            text = "No spots saved yet",
            style = TextStyle(fontSize = 12.sp)
        )
        Text(
            text = "Tap to open app",
            style = TextStyle(fontSize = 11.sp)
        )
    }
}

class LastSpotWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = LastSpotWidget()
}

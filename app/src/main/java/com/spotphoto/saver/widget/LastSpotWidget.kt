package com.spotphoto.saver.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.spotphoto.saver.MainActivity
import com.spotphoto.saver.R
import com.spotphoto.saver.data.PhotoSpotDatabase
import com.spotphoto.saver.data.SpotCategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import android.app.PendingIntent
import android.graphics.BitmapFactory
import java.io.File

class LastSpotWidgetReceiver : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val spot = try {
                PhotoSpotDatabase.getDatabase(context).photoSpotDao().getLatestSpot()
            } catch (e: Exception) {
                null
            }

            for (appWidgetId in appWidgetIds) {
                val views = RemoteViews(context.packageName, R.layout.widget_last_spot)

                // Tap widget to open app
                val openIntent = Intent(context, MainActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(
                    context, 0, openIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

                if (spot != null) {
                    val category = SpotCategory.fromTag(spot.category)
                    val timeText = SimpleDateFormat("MMM dd, h:mm a", Locale.getDefault())
                        .format(Date(spot.timestamp))

                    views.setTextViewText(R.id.widget_title, "${category.emoji} ${spot.note.ifBlank { category.label }}")
                    views.setTextViewText(R.id.widget_coords, "%.4f, %.4f".format(spot.latitude, spot.longitude))
                    views.setTextViewText(R.id.widget_bearing, "${spot.compassBearing.toInt()}° ${spot.compassDirection}")
                    views.setTextViewText(R.id.widget_time, timeText)

                    // Load thumbnail
                    try {
                        val file = File(spot.photoPath)
                        if (file.exists()) {
                            val options = BitmapFactory.Options().apply { inSampleSize = 4 }
                            val bitmap = BitmapFactory.decodeFile(spot.photoPath, options)
                            if (bitmap != null) {
                                views.setImageViewBitmap(R.id.widget_photo, bitmap)
                            }
                        }
                    } catch (_: Exception) {}
                } else {
                    views.setTextViewText(R.id.widget_title, "Photo Spot Saver")
                    views.setTextViewText(R.id.widget_coords, "No spots saved yet")
                    views.setTextViewText(R.id.widget_bearing, "")
                    views.setTextViewText(R.id.widget_time, "Tap to open app")
                }

                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }

    companion object {
        fun triggerUpdate(context: Context) {
            val intent = Intent(context, LastSpotWidgetReceiver::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            }
            val ids = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(ComponentName(context, LastSpotWidgetReceiver::class.java))
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            context.sendBroadcast(intent)
        }
    }
}

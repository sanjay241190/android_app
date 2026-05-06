package com.spotphoto.saver.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll

object WidgetUpdater {

    suspend fun updateWidget(context: Context) {
        LastSpotWidget().updateAll(context)
    }
}

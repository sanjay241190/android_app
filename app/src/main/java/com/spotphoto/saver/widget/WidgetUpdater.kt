package com.spotphoto.saver.widget

import android.content.Context

object WidgetUpdater {

    fun updateWidget(context: Context) {
        LastSpotWidgetReceiver.triggerUpdate(context)
    }
}

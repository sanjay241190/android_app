package com.spotphoto.saver

import android.app.Application
import com.spotphoto.saver.data.PhotoSpotDatabase
import com.spotphoto.saver.data.PhotoSpotRepository

class PhotoSpotApp : Application() {

    val database by lazy { PhotoSpotDatabase.getDatabase(this) }
    val repository by lazy { PhotoSpotRepository(database.photoSpotDao()) }
}

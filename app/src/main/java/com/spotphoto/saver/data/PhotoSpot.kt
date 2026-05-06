package com.spotphoto.saver.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photo_spots")
data class PhotoSpot(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val photoPath: String,
    val latitude: Double,
    val longitude: Double,
    val compassBearing: Float,
    val compassDirection: String,
    val note: String = "",
    val category: String = SpotCategory.GENERAL.tag,
    val timestamp: Long = System.currentTimeMillis()
)

enum class SpotCategory(val tag: String, val label: String, val emoji: String) {
    GENERAL("general", "General", "📍"),
    PARKING("parking", "Parking", "🅿️"),
    FOOD("food", "Food & Drink", "🍕"),
    NATURE("nature", "Nature", "🌿"),
    SHOPPING("shopping", "Shopping", "🛍️"),
    PHOTO_OP("photo_op", "Photo Op", "📸"),
    TRAVEL("travel", "Travel", "✈️"),
    WORK("work", "Work", "💼");

    companion object {
        fun fromTag(tag: String): SpotCategory =
            entries.find { it.tag == tag } ?: GENERAL
    }
}

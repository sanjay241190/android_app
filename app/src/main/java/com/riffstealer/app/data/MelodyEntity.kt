package com.riffstealer.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "melodies")
data class MelodyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val abcNotation: String,
    val bpm: Int,
    val noteSequence: String,
    val createdAt: Long = System.currentTimeMillis(),
    val durationMs: Long
)

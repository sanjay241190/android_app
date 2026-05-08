package com.riffstealer.app.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "variations",
    foreignKeys = [
        ForeignKey(
            entity = MelodyEntity::class,
            parentColumns = ["id"],
            childColumns = ["melodyId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("melodyId")]
)
data class VariationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val melodyId: Long,
    val genre: String,
    val mood: String,
    val tempo: Int,
    val abcNotation: String,
    val description: String,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

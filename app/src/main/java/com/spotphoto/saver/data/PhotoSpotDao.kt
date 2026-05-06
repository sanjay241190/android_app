package com.spotphoto.saver.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoSpotDao {

    @Query("SELECT * FROM photo_spots ORDER BY timestamp DESC")
    fun getAllSpots(): Flow<List<PhotoSpot>>

    @Query("SELECT * FROM photo_spots WHERE category = :category ORDER BY timestamp DESC")
    fun getSpotsByCategory(category: String): Flow<List<PhotoSpot>>

    @Query("SELECT * FROM photo_spots WHERE id = :id")
    suspend fun getSpotById(id: Long): PhotoSpot?

    @Query("SELECT * FROM photo_spots ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestSpot(): PhotoSpot?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpot(spot: PhotoSpot): Long

    @Update
    suspend fun updateSpot(spot: PhotoSpot)

    @Delete
    suspend fun deleteSpot(spot: PhotoSpot)

    @Query("SELECT COUNT(*) FROM photo_spots")
    fun getSpotCount(): Flow<Int>

    @Query("SELECT DISTINCT category FROM photo_spots")
    fun getUsedCategories(): Flow<List<String>>
}

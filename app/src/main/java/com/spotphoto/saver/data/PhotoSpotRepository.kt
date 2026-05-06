package com.spotphoto.saver.data

import kotlinx.coroutines.flow.Flow

class PhotoSpotRepository(private val dao: PhotoSpotDao) {

    val allSpots: Flow<List<PhotoSpot>> = dao.getAllSpots()
    val spotCount: Flow<Int> = dao.getSpotCount()
    val usedCategories: Flow<List<String>> = dao.getUsedCategories()

    fun getSpotsByCategory(category: String): Flow<List<PhotoSpot>> =
        dao.getSpotsByCategory(category)

    suspend fun insert(spot: PhotoSpot): Long = dao.insertSpot(spot)

    suspend fun update(spot: PhotoSpot) = dao.updateSpot(spot)

    suspend fun delete(spot: PhotoSpot) = dao.deleteSpot(spot)

    suspend fun getById(id: Long): PhotoSpot? = dao.getSpotById(id)

    suspend fun getLatest(): PhotoSpot? = dao.getLatestSpot()
}

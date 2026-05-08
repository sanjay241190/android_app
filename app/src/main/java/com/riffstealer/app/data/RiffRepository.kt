package com.riffstealer.app.data

import kotlinx.coroutines.flow.Flow

class RiffRepository(private val dao: RiffDao) {

    val allMelodies: Flow<List<MelodyEntity>> = dao.getAllMelodies()

    suspend fun saveMelody(melody: MelodyEntity): Long = dao.insertMelody(melody)

    suspend fun getMelody(id: Long): MelodyEntity? = dao.getMelodyById(id)

    suspend fun deleteMelody(melody: MelodyEntity) = dao.deleteMelody(melody)

    fun getVariations(melodyId: Long): Flow<List<VariationEntity>> =
        dao.getVariationsForMelody(melodyId)

    suspend fun saveVariations(variations: List<VariationEntity>) =
        dao.insertVariations(variations)

    suspend fun getVariation(id: Long): VariationEntity? = dao.getVariationById(id)

    suspend fun toggleFavorite(id: Long, isFavorite: Boolean) =
        dao.setFavorite(id, isFavorite)

    fun getFavorites(): Flow<List<VariationEntity>> = dao.getFavoriteVariations()
}

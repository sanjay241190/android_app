package com.riffstealer.app.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RiffDao {

    @Insert
    suspend fun insertMelody(melody: MelodyEntity): Long

    @Insert
    suspend fun insertVariation(variation: VariationEntity): Long

    @Insert
    suspend fun insertVariations(variations: List<VariationEntity>)

    @Update
    suspend fun updateVariation(variation: VariationEntity)

    @Delete
    suspend fun deleteMelody(melody: MelodyEntity)

    @Delete
    suspend fun deleteVariation(variation: VariationEntity)

    @Query("SELECT * FROM melodies ORDER BY createdAt DESC")
    fun getAllMelodies(): Flow<List<MelodyEntity>>

    @Query("SELECT * FROM melodies WHERE id = :id")
    suspend fun getMelodyById(id: Long): MelodyEntity?

    @Query("SELECT * FROM variations WHERE melodyId = :melodyId ORDER BY createdAt ASC")
    fun getVariationsForMelody(melodyId: Long): Flow<List<VariationEntity>>

    @Query("SELECT * FROM variations WHERE id = :id")
    suspend fun getVariationById(id: Long): VariationEntity?

    @Query("SELECT * FROM variations WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteVariations(): Flow<List<VariationEntity>>

    @Query("UPDATE variations SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: Long, isFavorite: Boolean)
}

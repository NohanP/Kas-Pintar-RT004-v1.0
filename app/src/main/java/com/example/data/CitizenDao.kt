package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.CitizenEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CitizenDao {
    @Query("SELECT * FROM citizens WHERE isActive = 1 ORDER BY houseNumber ASC")
    fun getAllActiveCitizens(): Flow<List<CitizenEntity>>

    @Query("SELECT * FROM citizens ORDER BY houseNumber ASC")
    fun getAllCitizens(): Flow<List<CitizenEntity>>

    @Query("SELECT * FROM citizens WHERE id = :id")
    suspend fun getCitizenById(id: Long): CitizenEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCitizen(citizen: CitizenEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(citizens: List<CitizenEntity>)

    @Update
    suspend fun updateCitizen(citizen: CitizenEntity)

    @Delete
    suspend fun deleteCitizen(citizen: CitizenEntity)

    @Query("SELECT COUNT(*) FROM citizens WHERE isActive = 1")
    suspend fun getActiveCitizenCount(): Int
}

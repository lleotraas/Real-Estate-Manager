package com.openclassrooms.realestatemanager.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.openclassrooms.realestatemanager.model.RealEstate
import kotlinx.coroutines.flow.Flow


@Dao
interface RealEstateDao {

    @Query("SELECT * FROM real_estate")
    fun getAllRealEstate(): Flow<List<RealEstate>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(realEstate: RealEstate)

    @Query("DELETE FROM real_estate")
    suspend fun deleteAll()
}
package com.openclassrooms.realestatemanager.database.dao

import androidx.room.*
import com.openclassrooms.realestatemanager.model.RealEstate
import kotlinx.coroutines.flow.Flow


@Dao
interface RealEstateDao {

    @Query("SELECT * FROM real_estate")
    fun getAllRealEstate(): Flow<List<RealEstate>>

    @Query("SELECT * FROM real_estate WHERE address = :address")
    fun getRealEstateByAddress(address: String): Flow<RealEstate>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(realEstate: RealEstate)

    @Update
    suspend fun updateRealEstate(realEstate: RealEstate)

    @Query("DELETE FROM real_estate")
    suspend fun deleteAll()
}
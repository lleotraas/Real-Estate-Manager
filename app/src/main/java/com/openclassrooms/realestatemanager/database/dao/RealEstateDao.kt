package com.openclassrooms.realestatemanager.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.openclassrooms.realestatemanager.model.RealEstate
import com.openclassrooms.realestatemanager.repository.RealEstateRepository
import kotlinx.coroutines.flow.Flow


@Dao
interface RealEstateDao {

    @Query("SELECT * FROM real_estate")
    fun getAllRealEstate(): Flow<List<RealEstate>>

    @Query("SELECT * FROM real_estate WHERE address = :address")
    fun getRealEstateByAddress(address: String): Flow<RealEstate>

    @Query("SELECT * FROM real_estate WHERE id = :id")
    fun getRealEstateById(id: Long): Flow<RealEstate>

    @RawQuery
    suspend fun searchRealEstateWithParameters(query: SupportSQLiteQuery): List<RealEstate>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(realEstate: RealEstate): Long

    @Update
    suspend fun updateRealEstate(realEstate: RealEstate)

    @Query("DELETE FROM real_estate")
    suspend fun deleteAll()
}
package com.openclassrooms.realestatemanager.features_real_estate.data.data_source.dao

import android.database.Cursor
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.openclassrooms.realestatemanager.features_real_estate.domain.model.RealEstate
import kotlinx.coroutines.flow.Flow


@Dao
interface RealEstateDao {

    @Query("SELECT * FROM real_estate WHERE address = :address")
    fun getRealEstateByAddress(address: String): Flow<RealEstate>

    @Query("SELECT * FROM real_estate WHERE id = :id")
    fun getRealEstateById(id: Long): Flow<RealEstate>

    @RawQuery(observedEntities = [RealEstate::class])
    suspend fun searchRealEstateWithParameters(query: SupportSQLiteQuery): List<RealEstate>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(realEstate: RealEstate): Long

    @Update
    suspend fun updateRealEstate(realEstate: RealEstate)

    @Query("SELECT * FROM real_estate WHERE id = :id")
    fun getRealEstateWithCursor(id: Long): Cursor
}
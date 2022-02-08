package com.openclassrooms.realestatemanager.ui.real_estate

import androidx.lifecycle.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.openclassrooms.realestatemanager.model.RealEstate
import com.openclassrooms.realestatemanager.model.RealEstateImage
import com.openclassrooms.realestatemanager.repository.FilterRepository
import com.openclassrooms.realestatemanager.repository.RealEstateImageRepository
import com.openclassrooms.realestatemanager.repository.RealEstateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class RealEstateViewModel(
    private val realEstateRepository: RealEstateRepository,
    private val realEstateImageRepository: RealEstateImageRepository,
    private val filterRepository: FilterRepository
) : ViewModel() {

    // REAL ESTATE
    suspend fun insert(realEstate: RealEstate) {
        realEstateRepository.insert(realEstate)
    }
    val getAllRealEstate: LiveData<List<RealEstate>> = realEstateRepository.getAllRealEstate.asLiveData()
    suspend fun searchRealEstateWithParameters(query: SupportSQLiteQuery): List<RealEstate> {
        return realEstateRepository.searchRealEstateWithParameters(query)
    }

    // FILTER
    val getFilteredRealEstate: LiveData<List<RealEstate>> = filterRepository.getFilteredRealEstate()
}
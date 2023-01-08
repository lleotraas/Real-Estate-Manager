package com.openclassrooms.realestatemanager.features_real_estate.presentation

import androidx.lifecycle.*
import androidx.sqlite.db.SimpleSQLiteQuery
import com.openclassrooms.realestatemanager.features_real_estate.domain.model.RealEstate
import com.openclassrooms.realestatemanager.features_real_estate.domain.model.RealEstatePhoto
import com.openclassrooms.realestatemanager.features_real_estate.data.repository.FilterRepositoryImpl
import com.openclassrooms.realestatemanager.features_real_estate.data.repository.RealEstatePhotoRepositoryImpl
import com.openclassrooms.realestatemanager.features_real_estate.data.repository.RealEstateRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RealEstateViewModel @Inject constructor(
    private val realEstateRepository: RealEstateRepositoryImpl,
    private val realEstatePhotoRepository: RealEstatePhotoRepositoryImpl,
    private val filterRepository: FilterRepositoryImpl
    ) : ViewModel() {

    // REAL ESTATE
    suspend fun insert(realEstate: RealEstate) {
        realEstateRepository.insert(realEstate)
    }
    val getAllRealEstate: LiveData<List<RealEstate>> = realEstateRepository.getAllRealEstate.asLiveData()

    suspend fun searchRealEstateWithParameters(query: SimpleSQLiteQuery): List<RealEstate> {
        return realEstateRepository.searchRealEstateWithParameters(query)
    }

    fun getRealEstateById(id: Long): LiveData<RealEstate> {
        return realEstateRepository.getRealEstateById(id).asLiveData()
    }

    suspend fun updateRealEstate(realEstate: RealEstate) {
        realEstateRepository.update(realEstate)
    }

    // REAL ESTATE PHOTO
    fun getAllRealEstatePhoto(id: Long): LiveData<List<RealEstatePhoto>> {
        return realEstatePhotoRepository.getRealEstatePhotos(id).asLiveData()
    }

    // FILTER
    fun getFilteredRealEstate(): LiveData<List<RealEstate>> {
        return filterRepository.getFilteredRealEstate()
    }

    fun isFilteredListIsEmpty(): MutableLiveData<Boolean> {
        return filterRepository.isFilteredListIsEmpty()
    }

   fun setFilteredListNotEmpty() {
        filterRepository.setFilteredListNotEmpty()
    }
    fun setFilteredListEmpty() {
        filterRepository.setFilteredListEmpty()
    }
    fun setFilteredList(filteredList: List<RealEstate>) {
        filterRepository.setFilteredList(filteredList)
    }
}
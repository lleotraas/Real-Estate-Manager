package com.openclassrooms.realestatemanager.ui.activity

import androidx.lifecycle.*
import androidx.sqlite.db.SimpleSQLiteQuery
import com.openclassrooms.realestatemanager.model.RealEstate
import com.openclassrooms.realestatemanager.model.RealEstatePhoto
import com.openclassrooms.realestatemanager.repository.FilterRepository
import com.openclassrooms.realestatemanager.repository.RealEstatePhotoRepository
import com.openclassrooms.realestatemanager.repository.RealEstateRepository
import kotlinx.coroutines.flow.Flow

class RealEstateViewModel(
    private val realEstateRepository: RealEstateRepository ,
    private val realEstatePhotoRepository: RealEstatePhotoRepository,
    private val filterRepository: FilterRepository
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
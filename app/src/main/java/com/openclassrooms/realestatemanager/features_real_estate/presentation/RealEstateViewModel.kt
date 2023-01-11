package com.openclassrooms.realestatemanager.features_real_estate.presentation

import androidx.lifecycle.*
import androidx.sqlite.db.SimpleSQLiteQuery
import com.openclassrooms.realestatemanager.features_real_estate.domain.model.RealEstate
import com.openclassrooms.realestatemanager.features_real_estate.domain.model.RealEstatePhoto
import com.openclassrooms.realestatemanager.features_real_estate.domain.use_case.filter.FilterUseCases
import com.openclassrooms.realestatemanager.features_real_estate.domain.use_case.real_estate.RealEstateUseCases
import com.openclassrooms.realestatemanager.features_real_estate.domain.use_case.real_estate_photo.RealEstatePhotoUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RealEstateViewModel @Inject constructor(
    private val realEstateUseCases: RealEstateUseCases,
    private val realEstatePhotoUseCases: RealEstatePhotoUseCases,
    private val filterUseCases: FilterUseCases
    ) : ViewModel() {

    // REAL ESTATE
    suspend fun insert(realEstate: RealEstate) {
        realEstateUseCases.addRealEstate(realEstate)
    }
    val getAllRealEstate: LiveData<List<RealEstate>> = realEstateUseCases.getAllRelEstate().asLiveData()

    suspend fun searchRealEstateWithParameters(query: SimpleSQLiteQuery): List<RealEstate> = realEstateUseCases.searchRealEstateWithParameters(query)

    fun getRealEstateById(id: Long): LiveData<RealEstate> = realEstateUseCases.getRealEstateById(id).asLiveData()

    suspend fun updateRealEstate(realEstate: RealEstate) {
        realEstateUseCases.updateRealEstate(realEstate)
    }

    // REAL ESTATE PHOTO
    fun getAllRealEstatePhoto(id: Long): LiveData<List<RealEstatePhoto>> = realEstatePhotoUseCases.getAllRealEstatePhoto(id).asLiveData()

    // FILTER
    fun getFilteredRealEstate(): LiveData<List<RealEstate>> = filterUseCases.getFilteredRealEstate()
    fun isFilteredListIsEmpty(): MutableLiveData<Boolean> = filterUseCases.isEmpty()

   fun setFilteredListNotEmpty() {
       filterUseCases.setFilteredListNotEmpty()
    }
    fun setFilteredListEmpty() {
        filterUseCases.setFilteredListEmpty()
    }
    fun setFilteredList(filteredList: List<RealEstate>) {
        filterUseCases.setFilteredList(filteredList)
    }
}
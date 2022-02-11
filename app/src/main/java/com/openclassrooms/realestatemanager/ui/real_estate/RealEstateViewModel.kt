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
    private val realEstateRepository: RealEstateRepository ,
    private val filterRepository: FilterRepository
    ) : ViewModel() {
//    private val filteredRealEstate = MutableLiveData<List<RealEstate>>()
//    private var isFilteredListEmpty = MutableLiveData<Boolean>()

    // REAL ESTATE
    suspend fun insert(realEstate: RealEstate) {
        realEstateRepository.insert(realEstate)
    }
    val getAllRealEstate: LiveData<List<RealEstate>> = realEstateRepository.getAllRealEstate.asLiveData()

//    suspend fun searchRealEstateWithParameters(query: SupportSQLiteQuery): List<RealEstate> {
//        return realEstateRepository.searchRealEstateWithParameters(query)
//    }
    fun getRealEstateById(id: Long): LiveData<RealEstate> {
        return realEstateRepository.getRealEstateById(id).asLiveData()
    }

    suspend fun updateRealEstate(realEstate: RealEstate) {
        realEstateRepository.update(realEstate)
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
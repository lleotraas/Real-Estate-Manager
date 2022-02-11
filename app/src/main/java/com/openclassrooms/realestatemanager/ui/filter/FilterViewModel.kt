package com.openclassrooms.realestatemanager.ui.filter

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.sqlite.db.SupportSQLiteQuery
import com.openclassrooms.realestatemanager.model.RealEstate
import com.openclassrooms.realestatemanager.model.RealEstateImage
import com.openclassrooms.realestatemanager.repository.FilterRepository
import com.openclassrooms.realestatemanager.repository.RealEstateImageRepository
import com.openclassrooms.realestatemanager.repository.RealEstateRepository
import kotlinx.coroutines.launch

class FilterViewModel(
    private val realEstateRepository: RealEstateRepository,
    private val filterRepository: FilterRepository
) : ViewModel() {

    // REAL ESTATE
    fun insert(realEstate: RealEstate) = viewModelScope.launch {
        realEstateRepository.insert(realEstate)
    }
    val getAllRealEstate: LiveData<List<RealEstate>> = realEstateRepository.getAllRealEstate.asLiveData()
    suspend fun searchRealEstateWithParameters(query: SupportSQLiteQuery): List<RealEstate> {
        return realEstateRepository.searchRealEstateWithParameters(query)
    }

    fun setFilteredList(filteredList: List<RealEstate>) {
        filterRepository.setFilteredList(filteredList)
    }
}
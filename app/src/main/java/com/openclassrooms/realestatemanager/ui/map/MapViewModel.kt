package com.openclassrooms.realestatemanager.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.model.RealEstate
import com.openclassrooms.realestatemanager.repository.RealEstateRepository
import kotlinx.coroutines.launch

class MapViewModel(
    private val realEstateRepository: RealEstateRepository
) : ViewModel() {

    fun insert(realEstate: RealEstate) = viewModelScope.launch {
        realEstateRepository.insert(realEstate)
    }
    val getAllRealEstate: LiveData<List<RealEstate>> = realEstateRepository.getAllRealEstate.asLiveData()
    fun getRealEstateByAddress(address: String): LiveData<RealEstate> {
        return realEstateRepository.getRealEstateByAddress(address).asLiveData()
    }

}
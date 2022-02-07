package com.openclassrooms.realestatemanager.ui

import androidx.lifecycle.*
import com.openclassrooms.realestatemanager.model.RealEstate
import com.openclassrooms.realestatemanager.model.RealEstateImage
import com.openclassrooms.realestatemanager.repository.RealEstateImageRepository
import com.openclassrooms.realestatemanager.repository.RealEstateRepository
import kotlinx.coroutines.launch

class RealEstateViewModel(
    private val realEstateRepository: RealEstateRepository,
    private val realEstateImageRepository: RealEstateImageRepository
    ) : ViewModel() {

    // REAL ESTATE
    fun insert(realEstate: RealEstate) = viewModelScope.launch {
        realEstateRepository.insert(realEstate)
    }
    val getAllRealEstate: LiveData<List<RealEstate>> = realEstateRepository.getAllRealEstate.asLiveData()
    fun getRealEstateByAddress(address: String): LiveData<RealEstate> {
        return realEstateRepository.getRealEstateByAddress(address).asLiveData()
    }


    // REAL ESTATE IMAGE
    fun insert(realEstateImage: RealEstateImage) = viewModelScope.launch {
        realEstateImageRepository.insert(realEstateImage)
    }
    fun getRealEstateAndImage(id: Long): LiveData<RealEstateImage> {
        return realEstateImageRepository.getRealEstateAndImage(id).asLiveData()
    }
}
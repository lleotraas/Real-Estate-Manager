package com.openclassrooms.realestatemanager.ui.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.model.RealEstate
import com.openclassrooms.realestatemanager.model.RealEstateImage
import com.openclassrooms.realestatemanager.repository.RealEstateImageRepository
import com.openclassrooms.realestatemanager.repository.RealEstateRepository
import kotlinx.coroutines.launch

class AddViewModel(
    private val realEstateRepository: RealEstateRepository,
    private val realEstateImageRepository: RealEstateImageRepository
) : ViewModel() {

    // REAL ESTATE
    suspend fun insert(realEstate: RealEstate): Long  {
         return realEstateRepository.insert(realEstate)
    }
    val getAllRealEstate: LiveData<List<RealEstate>> = realEstateRepository.getAllRealEstate.asLiveData()
    fun getRealEstateByAddress(address: String): LiveData<RealEstate> {
        return realEstateRepository.getRealEstateByAddress(address).asLiveData()
    }

    fun getRealEstateById(id: Long): LiveData<RealEstate> {
        return realEstateRepository.getRealEstateById(id).asLiveData()
    }

    fun update(realEstate: RealEstate) = viewModelScope.launch {
        realEstateRepository.update(realEstate)
    }

    // REAL ESTATE IMAGE
    fun insert(realEstateImage: RealEstateImage) = viewModelScope.launch {
        realEstateImageRepository.insert(realEstateImage)
    }
    fun getRealEstateAndImage(id: Long): LiveData<RealEstateImage> {
        return realEstateImageRepository.getRealEstateAndImage(id).asLiveData()
    }
}
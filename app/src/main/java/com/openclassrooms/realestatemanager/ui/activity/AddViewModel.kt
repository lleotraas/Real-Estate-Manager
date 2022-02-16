package com.openclassrooms.realestatemanager.ui.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.model.RealEstate
import com.openclassrooms.realestatemanager.model.RealEstatePhoto
import com.openclassrooms.realestatemanager.repository.RealEstatePhotoRepository
import com.openclassrooms.realestatemanager.repository.RealEstateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class AddViewModel(
    private val realEstateRepository: RealEstateRepository,
    private val realEstatePhotoRepository: RealEstatePhotoRepository
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

    //REAL ESTATE PHOTO
    suspend fun insertPhoto(realEstatePhoto: RealEstatePhoto): Long {
       return realEstatePhotoRepository.insertPhoto(realEstatePhoto)
    }

    fun getRealEstatePhotos(id: Long): LiveData<List<RealEstatePhoto>> {
        return realEstatePhotoRepository.getRealEstatePhotos(id).asLiveData()
    }

    suspend fun updateRealEstatePhoto(realEstatePhoto: RealEstatePhoto) {
        realEstatePhotoRepository.updateRealEstatePhoto(realEstatePhoto)
    }

    suspend fun deleteRealEstatePhoto(photoId: Long) {
        realEstatePhotoRepository.deleteRealEstatePhoto(photoId)
    }
}
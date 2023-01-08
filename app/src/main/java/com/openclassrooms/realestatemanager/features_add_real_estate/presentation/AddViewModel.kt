package com.openclassrooms.realestatemanager.features_add_real_estate.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.features_real_estate.domain.model.RealEstate
import com.openclassrooms.realestatemanager.features_real_estate.domain.model.RealEstatePhoto
import com.openclassrooms.realestatemanager.features_real_estate.data.repository.RealEstatePhotoRepositoryImpl
import com.openclassrooms.realestatemanager.features_real_estate.data.repository.RealEstateRepositoryImpl
import com.openclassrooms.realestatemanager.features_real_estate.domain.model.adresse.Adresse
import com.openclassrooms.realestatemanager.features_add_real_estate.data.remote.AutocompleteApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class AddViewModel @Inject constructor(
    private val realEstateRepository: RealEstateRepositoryImpl,
    private val realEstatePhotoRepository: RealEstatePhotoRepositoryImpl,
    private val api: AutocompleteApi
    ) : ViewModel() {

    // REAL ESTATE
    suspend fun insert(realEstate: RealEstate): Long  {
         return realEstateRepository.insert(realEstate)
    }
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

    fun getAllRealEstatePhoto(id: Long): LiveData<List<RealEstatePhoto>> {
        return realEstatePhotoRepository.getRealEstatePhotos(id).asLiveData()
    }

    suspend fun updateRealEstatePhoto(realEstatePhoto: RealEstatePhoto) {
        realEstatePhotoRepository.updateRealEstatePhoto(realEstatePhoto)
    }

    suspend fun deleteRealEstatePhoto(photoId: Long) {
        realEstatePhotoRepository.deleteRealEstatePhoto(photoId)
    }

    // API
    suspend fun getAutocompleteApi(input: String): Response<Adresse> = api.getPlacesAutocomplete(input)
}
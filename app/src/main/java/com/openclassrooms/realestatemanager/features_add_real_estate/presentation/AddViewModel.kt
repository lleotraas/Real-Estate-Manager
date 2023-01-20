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
import com.openclassrooms.realestatemanager.features_add_real_estate.domain.use_case.autocomplete.GetAutocompleteApi
import com.openclassrooms.realestatemanager.features_real_estate.domain.use_case.real_estate.RealEstateUseCases
import com.openclassrooms.realestatemanager.features_real_estate.domain.use_case.real_estate_photo.RealEstatePhotoUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class AddViewModel @Inject constructor(
    private val realEstateUseCases: RealEstateUseCases,
    private val realEstatePhotoUseCases: RealEstatePhotoUseCases,
    private val autocompleteApi: GetAutocompleteApi
    ) : ViewModel() {

    // REAL ESTATE
    suspend fun insert(realEstate: RealEstate): Long  {
         return realEstateUseCases.addRealEstate(realEstate)
    }
    fun getRealEstateByAddress(address: String): LiveData<RealEstate> {
        return realEstateUseCases.getRealEstateByAddress(address).asLiveData()
    }

    fun getRealEstateById(id: Long): LiveData<RealEstate> {
        return realEstateUseCases.getRealEstateById(id).asLiveData()
    }

    fun update(realEstate: RealEstate) = viewModelScope.launch {
        realEstateUseCases.updateRealEstate(realEstate)
    }

    //REAL ESTATE PHOTO
    suspend fun insertPhoto(realEstatePhoto: RealEstatePhoto): Long {
       return realEstatePhotoUseCases.insertPhoto(realEstatePhoto)
    }

    fun getAllRealEstatePhoto(id: Long): LiveData<List<RealEstatePhoto>> {
        return realEstatePhotoUseCases.getAllRealEstatePhoto(id).asLiveData()
    }

    suspend fun updateRealEstatePhoto(realEstatePhoto: RealEstatePhoto) {
        realEstatePhotoUseCases.updateRealEstatePhoto(realEstatePhoto)
    }

    suspend fun deleteRealEstatePhoto(photoId: Long) {
        realEstatePhotoUseCases.deleteRealEstatePhoto(photoId)
    }

    // API
    suspend fun getAutocompleteApi(input: String): Response<Adresse> = autocompleteApi(input)
}
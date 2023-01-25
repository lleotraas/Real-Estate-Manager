package com.openclassrooms.realestatemanager.features_add_real_estate.presentation

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.features_add_real_estate.domain.use_case.autocomplete.GetAutocompleteApi
import com.openclassrooms.realestatemanager.features_real_estate.domain.model.RealEstate
import com.openclassrooms.realestatemanager.features_real_estate.domain.model.RealEstatePhoto
import com.openclassrooms.realestatemanager.features_real_estate.domain.model.adresse.Adresse
import com.openclassrooms.realestatemanager.features_real_estate.domain.use_case.real_estate.RealEstateUseCases
import com.openclassrooms.realestatemanager.features_real_estate.domain.use_case.real_estate_photo.RealEstatePhotoUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class AddViewModel @Inject constructor(
    private val realEstateUseCases: RealEstateUseCases,
    private val realEstatePhotoUseCases: RealEstatePhotoUseCases,
    private val autocompleteApi: GetAutocompleteApi
    ) : ViewModel() {

    private val _state = MutableStateFlow(AutoCompleteState())
    val state: StateFlow<AutoCompleteState> = _state

    // REAL ESTATE
    suspend fun insert(realEstate: RealEstate): Long  {
         return realEstateUseCases.addRealEstate(realEstate)
    }
    fun getRealEstateByAddress(address: String) = realEstateUseCases.getRealEstateByAddress(address)

    fun getRealEstateById(id: Long) = realEstateUseCases.getRealEstateById(id)

    suspend fun update(realEstate: RealEstate) {
        realEstateUseCases.updateRealEstate(realEstate)
    }


    //REAL ESTATE PHOTO
    suspend fun insertPhoto(realEstatePhoto: RealEstatePhoto): Long {
       return realEstatePhotoUseCases.insertPhoto(realEstatePhoto)
    }

    fun getAllRealEstatePhoto(id: Long) = realEstatePhotoUseCases.getAllRealEstatePhoto(id)

    suspend fun updateRealEstatePhoto(realEstatePhoto: RealEstatePhoto) {
        realEstatePhotoUseCases.updateRealEstatePhoto(realEstatePhoto)
    }

    suspend fun deleteRealEstatePhoto(photoId: Long) {
        realEstatePhotoUseCases.deleteRealEstatePhoto(photoId)
    }

    // API
    suspend fun getAutocompleteApi(input: String): Int {
        return try {
            val response = autocompleteApi(input)
            _state.value = state.value.copy(
                response = handleResponse(response),
                features = response.body()?.features ?: emptyList()
            )
            R.string.autocomplete_success
        } catch (exception: IOException) {
            Log.e(
                ContentValues.TAG,
                "IOException, you might have internet connection" + exception.message
            )
            R.string.fragment_add_information_no_suggestion

        } catch (exception: HttpException) {
            Log.e(ContentValues.TAG, "HttpException, unexpected response" + exception.message)
            R.string.http_exception
        }
    }

    private fun handleResponse(response: Response<Adresse>?): MutableList<String> {
        val placeAddress = mutableListOf<String>()
        if (response?.isSuccessful == true && response.body() != null) {
            for (place in response.body()!!.features) {
                Log.e(javaClass.simpleName, "configureListener: address:${place.properties.label}", )
                placeAddress.add(place.properties.label)
            }
        }
        return placeAddress
    }

}
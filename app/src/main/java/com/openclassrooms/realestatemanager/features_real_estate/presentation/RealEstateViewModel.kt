package com.openclassrooms.realestatemanager.features_real_estate.presentation

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.*
import androidx.sqlite.db.SimpleSQLiteQuery
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.features_real_estate.domain.use_case.autocomplete.AutoCompleteUseCases
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
class RealEstateViewModel @Inject constructor(
    private val realEstateUseCases: RealEstateUseCases,
    private val realEstatePhotoUseCases: RealEstatePhotoUseCases,
    private val autocompleteApi: AutoCompleteUseCases
    ) : ViewModel() {

    private val _state = MutableStateFlow(RealEstateState())
    val state: StateFlow<RealEstateState> = _state

    init {
        viewModelScope.launch {
            searchRealEstateWithParameters(state.value.query)
        }
    }
    // REAL ESTATE
    suspend fun insert(realEstate: RealEstate): Long  {
        return realEstateUseCases.addRealEstate(realEstate)
    }

    suspend fun searchRealEstateWithParameters(query: SimpleSQLiteQuery) {
        _state.value =  state.value.copy(
            realEstates = realEstateUseCases.searchRealEstateWithParameters(query)
        )
    }

    fun getRealEstateByAddress(address: String) = realEstateUseCases.getRealEstateByAddress(address)

    fun getRealEstateById(id: Long) = realEstateUseCases.getRealEstateById(id)

    suspend fun updateRealEstate(realEstate: RealEstate) {
        realEstateUseCases.updateRealEstate(realEstate)
    }

    // REAL ESTATE PHOTO

    suspend fun insertPhoto(realEstatePhoto: RealEstatePhoto): Long {
        return realEstatePhotoUseCases.insertPhoto(realEstatePhoto)
    }

    suspend fun updateRealEstatePhoto(realEstatePhoto: RealEstatePhoto) {
        realEstatePhotoUseCases.updateRealEstatePhoto(realEstatePhoto)
    }

    suspend fun deleteRealEstatePhoto(photoId: Long) {
        realEstatePhotoUseCases.deleteRealEstatePhoto(photoId)
    }

    fun getAllRealEstatePhoto(id: Long) = realEstatePhotoUseCases.getAllRealEstatePhoto(id)

    // FILTER
    fun updateSearchQuery(query: SimpleSQLiteQuery) {
        _state.value = state.value.copy(
            query = query
        )
        viewModelScope.launch {
            searchRealEstateWithParameters(query)
        }
//        filterUseCases.updateQuery(query)
    }

    inline fun <T> MutableStateFlow<T>.update(function: (T) -> T) {
        while (true) {
            val prevValue = value
            val nextValue = function(prevValue)
            if (compareAndSet(prevValue, nextValue)) {
                return
            }
        }
    }

    // AUTOCOMPLETE API

    suspend fun getAutocompleteApi(input: String): Int {
        return try {
            val response = autocompleteApi.getAutocompleteApi(input)
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

    private fun handleResponse(response: Response<Adresse>?): MutableList<String> = autocompleteApi.handleResponse(response)
}
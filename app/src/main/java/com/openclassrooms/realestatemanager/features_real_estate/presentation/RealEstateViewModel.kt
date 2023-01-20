package com.openclassrooms.realestatemanager.features_real_estate.presentation

import androidx.lifecycle.*
import androidx.sqlite.db.SimpleSQLiteQuery
import com.openclassrooms.realestatemanager.features_real_estate.domain.model.RealEstate
import com.openclassrooms.realestatemanager.features_real_estate.domain.use_case.filter.FilterUseCases
import com.openclassrooms.realestatemanager.features_real_estate.domain.use_case.real_estate.RealEstateUseCases
import com.openclassrooms.realestatemanager.features_real_estate.domain.use_case.real_estate_photo.RealEstatePhotoUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RealEstateViewModel @Inject constructor(
    private val realEstateUseCases: RealEstateUseCases,
    private val realEstatePhotoUseCases: RealEstatePhotoUseCases,
    private val filterUseCases: FilterUseCases
    ) : ViewModel() {

    private val _realEstateState = MutableStateFlow(RealEstateState())
    val realEstateState: StateFlow<RealEstateState> = _realEstateState

    init {
        viewModelScope.launch {
            filterUseCases.getFilterState().collectLatest { filterSate ->
                searchRealEstateWithParameters(filterSate.query)
            }
        }
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

    fun searchRealEstateWithParameters(query: SimpleSQLiteQuery) {
        viewModelScope.launch {
            _realEstateState.value =  realEstateState.value.copy(
                realEstates = realEstateUseCases.searchRealEstateWithParameters(query)
            )
        }
    }

    fun getRealEstateById(id: Long) = realEstateUseCases.getRealEstateById(id)

    suspend fun updateRealEstate(realEstate: RealEstate) {
        realEstateUseCases.updateRealEstate(realEstate)
    }

    // REAL ESTATE PHOTO
    fun getAllRealEstatePhoto(id: Long) = realEstatePhotoUseCases.getAllRealEstatePhoto(id)

    // FILTER
    fun updateSearchQuery(query: SimpleSQLiteQuery, scope: LifecycleCoroutineScope) {
        filterUseCases.updateQuery(query, scope)
    }
}
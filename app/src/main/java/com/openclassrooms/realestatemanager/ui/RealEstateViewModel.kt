package com.openclassrooms.realestatemanager.ui

import androidx.lifecycle.*
import com.openclassrooms.realestatemanager.model.RealEstate
import com.openclassrooms.realestatemanager.repository.RealEstateRepository
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class RealEstateViewModel(private val realEstateRepository: RealEstateRepository) : ViewModel() {

    val getAllRealEstate: LiveData<List<RealEstate>> = realEstateRepository.getAllRealEstate.asLiveData()

    fun insert(realEstate: RealEstate) = viewModelScope.launch {
        realEstateRepository.insert(realEstate)
    }
}

class RealEstateViewModelFactory(private val realEstateRepository: RealEstateRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RealEstateViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RealEstateViewModel(realEstateRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
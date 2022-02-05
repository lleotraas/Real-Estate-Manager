package com.openclassrooms.realestatemanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.openclassrooms.realestatemanager.repository.RealEstateImageRepository
import com.openclassrooms.realestatemanager.repository.RealEstateRepository
import com.openclassrooms.realestatemanager.ui.RealEstateViewModel
import com.openclassrooms.realestatemanager.ui.filter.FilterViewModel
import com.openclassrooms.realestatemanager.ui.real_estate.MapViewModel
import java.lang.IllegalArgumentException

class RealEstateViewModelFactory (
    private val realEstateRepository: RealEstateRepository,
    private val realEstateImageRepository: RealEstateImageRepository
    ) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RealEstateViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RealEstateViewModel(realEstateRepository, realEstateImageRepository) as T
        }
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MapViewModel(realEstateRepository) as T
        }
        if (modelClass.isAssignableFrom(FilterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FilterViewModel(realEstateRepository, realEstateImageRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
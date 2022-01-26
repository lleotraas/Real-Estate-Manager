package com.openclassrooms.realestatemanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.openclassrooms.realestatemanager.repository.RealEstateImageRepository
import com.openclassrooms.realestatemanager.repository.RealEstateRepository
import com.openclassrooms.realestatemanager.ui.RealEstateViewModel
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
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
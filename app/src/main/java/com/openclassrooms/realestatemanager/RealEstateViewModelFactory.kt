package com.openclassrooms.realestatemanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.openclassrooms.realestatemanager.repository.FilterRepository
import com.openclassrooms.realestatemanager.repository.RealEstatePhotoRepository
import com.openclassrooms.realestatemanager.repository.RealEstateRepository
import com.openclassrooms.realestatemanager.ui.activity.RealEstateViewModel
import com.openclassrooms.realestatemanager.ui.activity.AddViewModel
import java.lang.IllegalArgumentException

class RealEstateViewModelFactory (
    private val realEstateRepository: RealEstateRepository,
    private val realEstatePhotoRepository: RealEstatePhotoRepository,
    private val filterRepository: FilterRepository
    ) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RealEstateViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RealEstateViewModel(realEstateRepository, realEstatePhotoRepository, filterRepository) as T
        }
        if (modelClass.isAssignableFrom(AddViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddViewModel(realEstateRepository, realEstatePhotoRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
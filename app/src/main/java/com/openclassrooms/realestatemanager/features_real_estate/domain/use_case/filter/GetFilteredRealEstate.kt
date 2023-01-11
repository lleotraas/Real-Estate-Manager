package com.openclassrooms.realestatemanager.features_real_estate.domain.use_case.filter

import androidx.lifecycle.LiveData
import com.openclassrooms.realestatemanager.features_real_estate.domain.model.RealEstate
import com.openclassrooms.realestatemanager.features_real_estate.domain.repository.FilterRepository

class GetFilteredRealEstate(
    private val repository: FilterRepository
) {

    operator fun invoke(): LiveData<List<RealEstate>> = repository.getFilteredRealEstate()

}
package com.openclassrooms.realestatemanager.features_real_estate.domain.use_case.filter

import com.openclassrooms.realestatemanager.features_real_estate.domain.model.RealEstate
import com.openclassrooms.realestatemanager.features_real_estate.domain.repository.FilterRepository

class SetFilteredList(
    private val repository: FilterRepository
) {

    operator fun invoke(filteredList: List<RealEstate>) {
        repository.setFilteredList(filteredList)
    }

}
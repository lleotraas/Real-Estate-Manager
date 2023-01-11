package com.openclassrooms.realestatemanager.features_real_estate.domain.use_case.filter

import com.openclassrooms.realestatemanager.features_real_estate.domain.repository.FilterRepository

class SetFilteredListEmpty(
    private val repository: FilterRepository
) {

    operator fun invoke() {
        repository.setFilteredListEmpty()
    }

}
package com.openclassrooms.realestatemanager.features_real_estate.domain.use_case.filter

import com.openclassrooms.realestatemanager.features_real_estate.domain.repository.FilterRepository
import com.openclassrooms.realestatemanager.features_real_estate.presentation.FilterState
import kotlinx.coroutines.flow.StateFlow

class GetFilterState(
    private val repository: FilterRepository
) {

    operator fun invoke(): StateFlow<FilterState> = repository.getFilterState()

}
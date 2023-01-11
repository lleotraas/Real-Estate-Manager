package com.openclassrooms.realestatemanager.features_real_estate.domain.use_case.filter

import androidx.lifecycle.MutableLiveData
import com.openclassrooms.realestatemanager.features_real_estate.domain.repository.FilterRepository

class IsFilteredListIsEmpty(
    private val repository: FilterRepository
) {

    operator fun invoke(): MutableLiveData<Boolean> = repository.isFilteredListIsEmpty()


}
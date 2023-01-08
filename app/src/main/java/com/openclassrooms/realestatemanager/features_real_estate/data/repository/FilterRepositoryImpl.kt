package com.openclassrooms.realestatemanager.features_real_estate.data.repository

import androidx.lifecycle.MutableLiveData
import com.openclassrooms.realestatemanager.features_real_estate.domain.model.RealEstate
import com.openclassrooms.realestatemanager.features_real_estate.domain.repository.FilterRepository
import javax.inject.Inject

class FilterRepositoryImpl @Inject constructor() : FilterRepository {

    private val filteredRealEstate = MutableLiveData<List<RealEstate>>()
    private var isFilteredListEmpty = MutableLiveData<Boolean>()

    override fun getFilteredRealEstate(): MutableLiveData<List<RealEstate>> {
        return filteredRealEstate
    }

    override fun setFilteredList(filteredList: List<RealEstate>) {
        filteredRealEstate.value = filteredList
    }

    override fun isFilteredListIsEmpty(): MutableLiveData<Boolean> {
        return isFilteredListEmpty
    }

    override fun setFilteredListNotEmpty() {
        isFilteredListEmpty.value = true
    }
    override fun setFilteredListEmpty() {
        isFilteredListEmpty.value = false
    }
}
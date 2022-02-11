package com.openclassrooms.realestatemanager.repository

import androidx.lifecycle.MutableLiveData
import com.openclassrooms.realestatemanager.model.RealEstate

class FilterRepository {
    private val filteredRealEstate = MutableLiveData<List<RealEstate>>()
    private var isFilteredListEmpty = MutableLiveData<Boolean>()

    fun getFilteredRealEstate(): MutableLiveData<List<RealEstate>> {
        return filteredRealEstate
    }

    fun setFilteredList(filteredList: List<RealEstate>) {
        filteredRealEstate.value = filteredList
    }

    fun isFilteredListIsEmpty(): MutableLiveData<Boolean> {
        return isFilteredListEmpty
    }

    fun setFilteredListNotEmpty() {
        isFilteredListEmpty.value = true
    }
    fun setFilteredListEmpty() {
        isFilteredListEmpty.value = false
    }
}
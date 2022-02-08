package com.openclassrooms.realestatemanager.repository

import androidx.lifecycle.MutableLiveData
import com.openclassrooms.realestatemanager.model.RealEstate

class FilterRepository {
    private val filteredRealEstate = MutableLiveData<List<RealEstate>>()

    fun getFilteredRealEstate(): MutableLiveData<List<RealEstate>> {
        return filteredRealEstate
    }

    fun setFilteredList(filteredList: List<RealEstate>) {
        filteredRealEstate.value = filteredList
    }
}
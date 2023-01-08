package com.openclassrooms.realestatemanager.features_real_estate.domain.repository

import androidx.lifecycle.MutableLiveData
import com.openclassrooms.realestatemanager.features_real_estate.domain.model.RealEstate

interface FilterRepository {

    fun getFilteredRealEstate(): MutableLiveData<List<RealEstate>>

    fun setFilteredList(filteredList: List<RealEstate>)

    fun isFilteredListIsEmpty(): MutableLiveData<Boolean>

    fun setFilteredListNotEmpty()

    fun setFilteredListEmpty()

}
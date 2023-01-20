package com.openclassrooms.realestatemanager.features_real_estate.domain.repository

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.MutableLiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.openclassrooms.realestatemanager.features_real_estate.domain.model.RealEstate
import com.openclassrooms.realestatemanager.features_real_estate.presentation.FilterState
import kotlinx.coroutines.flow.StateFlow

interface FilterRepository {

    fun updateQuery(query: SimpleSQLiteQuery, scope: LifecycleCoroutineScope)

    fun getFilterState(): StateFlow<FilterState>

    fun setFilteredList(filteredList: List<RealEstate>)

    fun isFilteredListIsEmpty(): MutableLiveData<Boolean>

    fun setFilteredListNotEmpty()

    fun setFilteredListEmpty()

}
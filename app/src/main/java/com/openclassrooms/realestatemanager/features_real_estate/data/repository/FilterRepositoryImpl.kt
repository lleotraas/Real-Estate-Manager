package com.openclassrooms.realestatemanager.features_real_estate.data.repository

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.MutableLiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.openclassrooms.realestatemanager.features_real_estate.domain.model.RealEstate
import com.openclassrooms.realestatemanager.features_real_estate.domain.repository.FilterRepository
import com.openclassrooms.realestatemanager.features_real_estate.presentation.FilterState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class FilterRepositoryImpl @Inject constructor() : FilterRepository {

    private val filteredRealEstate = MutableLiveData<List<RealEstate>>()
    private var isFilteredListEmpty = MutableLiveData<Boolean>()
    private val _state = MutableStateFlow(FilterState())
    val state: StateFlow<FilterState> = _state

    override fun updateQuery(query: SimpleSQLiteQuery, scope: LifecycleCoroutineScope) {
        scope.launch {
            _state.value = state.value.copy(
                query = query
            )
        }
    }

    override fun getFilterState() = state

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
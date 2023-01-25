package com.openclassrooms.realestatemanager.features_real_estate.data.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.openclassrooms.realestatemanager.features_real_estate.domain.repository.FilterRepository
import com.openclassrooms.realestatemanager.features_real_estate.presentation.FilterState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class FilterRepositoryImpl @Inject constructor() : FilterRepository {

    private val _state = MutableStateFlow(FilterState())
    val state: StateFlow<FilterState> = _state

    override fun updateQuery(query: SimpleSQLiteQuery) {
            _state.value = state.value.copy(
                query = query
            )
    }
    override fun getFilterState() = state
}
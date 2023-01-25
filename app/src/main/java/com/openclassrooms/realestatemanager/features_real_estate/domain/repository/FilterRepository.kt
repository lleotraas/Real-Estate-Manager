package com.openclassrooms.realestatemanager.features_real_estate.domain.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.openclassrooms.realestatemanager.features_real_estate.presentation.FilterState
import kotlinx.coroutines.flow.StateFlow

interface FilterRepository {

    fun updateQuery(query: SimpleSQLiteQuery)

    fun getFilterState(): StateFlow<FilterState>
}
package com.openclassrooms.realestatemanager.features_real_estate.domain.use_case.filter

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.sqlite.db.SimpleSQLiteQuery
import com.openclassrooms.realestatemanager.features_real_estate.domain.repository.FilterRepository

class UpdateQuery(
    private val repository: FilterRepository
) {

    operator fun invoke(query: SimpleSQLiteQuery) {
        repository.updateQuery(query)
    }

}
package com.openclassrooms.realestatemanager.features_real_estate.presentation

import androidx.sqlite.db.SimpleSQLiteQuery

data class FilterState(
    val query: SimpleSQLiteQuery = SimpleSQLiteQuery("SELECT * FROM real_estate")
)
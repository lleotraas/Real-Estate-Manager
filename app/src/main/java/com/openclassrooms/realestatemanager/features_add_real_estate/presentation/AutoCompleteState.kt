package com.openclassrooms.realestatemanager.features_add_real_estate.presentation

import com.openclassrooms.realestatemanager.features_real_estate.domain.model.adresse.Feature


data class AutoCompleteState(
    val response: MutableList<String> = mutableListOf(),
    val features: List<Feature> = emptyList()
)

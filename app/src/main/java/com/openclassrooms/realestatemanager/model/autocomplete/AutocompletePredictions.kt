package com.openclassrooms.realestatemanager.model.autocomplete

data class AutocompletePredictions(
    val predictions: List<Prediction>,
    val status: String
)
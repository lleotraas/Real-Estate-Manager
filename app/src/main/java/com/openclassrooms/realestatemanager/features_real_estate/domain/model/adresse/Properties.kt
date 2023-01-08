package com.openclassrooms.realestatemanager.features_real_estate.domain.model.adresse

data class Properties(
    val city: String,
    val citycode: String,
    val context: String,
    val housenumber: String,
    val id: String,
    val importance: Double,
    val label: String,
    val name: String,
    val postcode: String,
    val score: Double,
    val street: String,
    val type: String,
    val x: Double,
    val y: Double
)
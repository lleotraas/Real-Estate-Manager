package com.openclassrooms.realestatemanager.model.adresse

data class Feature(
    val geometry: Geometry,
    val properties: Properties,
    val type: String
)
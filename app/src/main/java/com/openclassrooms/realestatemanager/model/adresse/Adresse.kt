package com.openclassrooms.realestatemanager.model.adresse

data class Adresse(
    val attribution: String,
    val features: List<Feature>,
    val licence: String,
    val limit: Int,
    val query: String,
    val type: String,
    val version: String
)
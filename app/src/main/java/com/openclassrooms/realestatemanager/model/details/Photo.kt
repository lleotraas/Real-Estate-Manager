package com.openclassrooms.realestatemanager.model.details

data class Photo(
    val height: Int,
    val html_attributions: List<String>,
    val photo_reference: String,
    val width: Int
)
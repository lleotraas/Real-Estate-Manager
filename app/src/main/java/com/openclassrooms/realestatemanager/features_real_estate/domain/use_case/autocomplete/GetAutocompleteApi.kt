package com.openclassrooms.realestatemanager.features_real_estate.domain.use_case.autocomplete

import com.openclassrooms.realestatemanager.features_real_estate.data.remote.AutocompleteApi

class GetAutocompleteApi(
    private val api: AutocompleteApi
) {

    suspend operator fun invoke(input: String) = api.getPlacesAutocomplete(input)

}
package com.openclassrooms.realestatemanager.features_real_estate.domain.use_case.autocomplete

import com.openclassrooms.realestatemanager.features_real_estate.domain.model.adresse.Adresse
import retrofit2.Response

class HandleResponse(
) {

    operator fun invoke(response: Response<Adresse>?): MutableList<String> {
        val placeAddress = mutableListOf<String>()
        if (response?.isSuccessful == true && response.body() != null) {
            for (place in response.body()!!.features) {
                placeAddress.add(place.properties.label)
            }
        }
        return placeAddress
    }

}
package com.openclassrooms.realestatemanager.features_real_estate.domain.use_case.filter

class FilterUseCases(
    val getFilteredRealEstate: GetFilteredRealEstate,
    val isEmpty: IsFilteredListIsEmpty,
    val setFilteredList: SetFilteredList,
    val setFilteredListEmpty: SetFilteredListEmpty,
    val setFilteredListNotEmpty: SetFilteredListNotEmpty
)
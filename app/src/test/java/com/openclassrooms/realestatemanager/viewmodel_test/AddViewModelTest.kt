package com.openclassrooms.realestatemanager.viewmodel_test

import com.openclassrooms.realestatemanager.features_real_estate.domain.use_case.autocomplete.GetAutocompleteApi
import com.openclassrooms.realestatemanager.features_real_estate.domain.use_case.real_estate.AddRealEstate
import com.openclassrooms.realestatemanager.features_real_estate.domain.use_case.real_estate.*
import com.openclassrooms.realestatemanager.features_real_estate.domain.use_case.real_estate_photo.RealEstatePhotoUseCases
import com.openclassrooms.realestatemanager.utils.FakeRealEstateRepository
import org.junit.Before
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class AddViewModelTest {

    lateinit var viewModel: AddViewModel
    lateinit var realEstateUseCases: RealEstateUseCases
    lateinit var realEstatePhotoUseCases: RealEstatePhotoUseCases
    lateinit var autocompleteApi: GetAutocompleteApi
    lateinit var addRealEstate: AddRealEstate
    lateinit var getRealEstateById: GetRealEstateById
    lateinit var getRealEstateByAddress: GetRealEstateByAddress
    lateinit var searchRealEstateWithParameters: SearchRealEstateWithParameters
    lateinit var updateRealEstate: UpdateRealEstate


    @Before
    fun setUp() {
        getRealEstateByAddress = GetRealEstateByAddress(FakeRealEstateRepository())
        getRealEstateById = GetRealEstateById(FakeRealEstateRepository())
        searchRealEstateWithParameters = SearchRealEstateWithParameters(FakeRealEstateRepository())
        updateRealEstate = UpdateRealEstate(FakeRealEstateRepository())
        addRealEstate = AddRealEstate(FakeRealEstateRepository())
        realEstateUseCases = RealEstateUseCases(addRealEstate, getRealEstateByAddress, getRealEstateById, searchRealEstateWithParameters, updateRealEstate)
        viewModel = AddViewModel(realEstateUseCases, realEstatePhotoUseCases, autocompleteApi)
    }

}
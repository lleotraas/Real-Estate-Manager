package com.openclassrooms.realestatemanager.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.RealEstateViewModelFactory
import com.openclassrooms.realestatemanager.databinding.FragmentMapViewBinding
import com.openclassrooms.realestatemanager.dependency.RealEstateApplication
import com.openclassrooms.realestatemanager.model.RealEstate
import com.openclassrooms.realestatemanager.ui.real_estate.MapViewModel

class FragmentMapView : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mBinding: FragmentMapViewBinding
    private val mViewModel: MapViewModel by viewModels {
        RealEstateViewModelFactory(
            (requireActivity().application as RealEstateApplication).realEstateRepository,
            (requireActivity().application as RealEstateApplication).realEstateImageRepository,
            (requireActivity().application as RealEstateApplication).filterRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentMapViewBinding.inflate(inflater, container, false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.fragment_map_view_google_maps) as SupportMapFragment
        mapFragment.getMapAsync(this)
        return mBinding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        getAllRealEstate()
    }

    private fun getAllRealEstate() {
        mViewModel.getAllRealEstate.observe(viewLifecycleOwner) { addMarkerOnMap(it) }
    }

    private fun addMarkerOnMap(listOfRealEstate: List<RealEstate>) {
        for (realEstate in listOfRealEstate) {
            val location = LatLng(realEstate.latitude.toDouble(), realEstate.longitude.toDouble())
            mMap.addMarker(
                MarkerOptions()
                    .position(location)
            )
        }
    }
}


package com.openclassrooms.realestatemanager.ui.map

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.RealEstateViewModelFactory
import com.openclassrooms.realestatemanager.databinding.FragmentMapViewBinding
import com.openclassrooms.realestatemanager.dependency.RealEstateApplication
import com.openclassrooms.realestatemanager.model.RealEstate
import com.openclassrooms.realestatemanager.ui.ItemDetailHostActivity
import com.openclassrooms.realestatemanager.ui.detail.ItemDetailFragment

import kotlinx.coroutines.launch

class FragmentMapView : Fragment(),
                        OnMapReadyCallback,
                        GoogleMap.OnMarkerClickListener
{

    private lateinit var mMap: GoogleMap
    private lateinit var mBinding: FragmentMapViewBinding
    private val mViewModel: MapViewModel by viewModels {
        RealEstateViewModelFactory(
            (requireActivity().application as RealEstateApplication).realEstateRepository,
            (requireActivity().application as RealEstateApplication).realEstateImageRepository,
            (requireActivity().application as RealEstateApplication).filterRepository)
    }
    private var locationPermissionGranted = false
    private lateinit var permissionsLauncher: ActivityResultLauncher<String>
    private lateinit var lastKnownLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentMapViewBinding.inflate(inflater, container, false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.fragment_map_view_google_maps) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity().applicationContext)
        permissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            locationPermissionGranted = it ?: locationPermissionGranted
        }
        updateOrRequestPermission()
        return mBinding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        updateLocationUi()
        getDeviceLocation()
        getAllRealEstate()
        mMap.setOnMarkerClickListener(this)
    }

    private fun updateOrRequestPermission() {
        val hasRequestPermission = ContextCompat.checkSelfPermission(requireContext(),
        ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        locationPermissionGranted = hasRequestPermission

        if (!locationPermissionGranted) {
            permissionsLauncher.launch(ACCESS_FINE_LOCATION)
        }
    }

    private fun updateLocationUi() {
        try {
            if (locationPermissionGranted) {
                mMap.isMyLocationEnabled = true
                mMap.uiSettings.isMyLocationButtonEnabled = true
            } else {
                mMap.isMyLocationEnabled = false
                mMap.uiSettings.isMyLocationButtonEnabled = false
                updateOrRequestPermission()
            }
        } catch (exception: SecurityException) {
            Log.e(TAG, "Exception: ${exception.message}")
        }
    }

    private fun getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                lifecycleScope.launch {
                    val locationResult = fusedLocationProviderClient.lastLocation
                    locationResult.addOnCompleteListener(requireActivity()) { task ->
                        if (task.isSuccessful) {
                            lastKnownLocation = task.result
                            CURRENT_LOCATION = LatLng(lastKnownLocation.latitude, lastKnownLocation.longitude)
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                LatLng(lastKnownLocation.latitude,
                                    lastKnownLocation.longitude),10f))
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.")
                            Log.d(TAG, "Exception: ${task.exception}")
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 18f))
                            mMap.uiSettings.isMyLocationButtonEnabled = false
                        }
                    }
                }
            }
        } catch (exception: SecurityException) {
            Log.e(TAG, "Exception: ${exception.message}")
        }
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
                    .snippet(realEstate.id.toString())
            )
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val realEstateId = marker.snippet
//        val fragment = ItemDetailFragment()
        val bundle = Bundle()
        bundle.putString(ARG_ID, realEstateId)
//        val fragmentManager = requireActivity().supportFragmentManager
//        fragmentManager.commit {
//            setReorderingAllowed(true)
//            arguments = bundle
//            replace(R.id.activity_map_view_container, fragment)
//        }
        val intent = Intent(requireContext(), ItemDetailHostActivity::class.java)
        intent.putExtra(ARG_ID, realEstateId)
        startActivity(intent)
        return false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("current_location", CURRENT_LOCATION)
    }

    companion object {
        val ARG_FRAGMENT = "detail_fragment"
        val ARG_ID= "real_estate_id"
        var CURRENT_LOCATION: LatLng? = null
        val DEFAULT_LOCATION = LatLng(43.406656, 3.684383)
    }
}


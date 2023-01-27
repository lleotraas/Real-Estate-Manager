package com.openclassrooms.realestatemanager.features_real_estate.presentation.map

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentMapsBinding
import com.openclassrooms.realestatemanager.features_real_estate.domain.model.RealEstate
import com.openclassrooms.realestatemanager.features_real_estate.data.utils.PlaceholderContent
import com.openclassrooms.realestatemanager.features_real_estate.data.utils.UtilsKt
import com.openclassrooms.realestatemanager.features_real_estate.presentation.ItemDetailHostActivity
import com.openclassrooms.realestatemanager.features_real_estate.presentation.RealEstateViewModel
import com.openclassrooms.realestatemanager.features_real_estate.presentation.detail.DetailFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MapViewFragment : Fragment(),
                        OnMapReadyCallback,
                        GoogleMap.OnMarkerClickListener
{

    private lateinit var mMap: GoogleMap
    private lateinit var mBinding: FragmentMapsBinding
    private val mViewModel: RealEstateViewModel by viewModels()
    private var locationPermissionGranted = false
    private lateinit var permissionsLauncher: ActivityResultLauncher<String>
    private lateinit var lastKnownLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var realEstateId = 0L
    private var currentRealEstateLocation: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args: MutableMap<String, PlaceholderContent.PlaceholderItem> = PlaceholderContent.ITEM_MAP
        realEstateId = if (args.containsKey(UtilsKt.ID)) args[UtilsKt.ID].toString().toLong() else 0L
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentMapsBinding.inflate(inflater, container, false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.fragment_map_view_google_maps) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity().applicationContext)
        permissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            locationPermissionGranted = it ?: locationPermissionGranted
        }
        configureSupportNavigateUp()
        updateOrRequestPermission()
        return mBinding.root
    }

    private fun configureSupportNavigateUp() {

        requireActivity().addMenuProvider(object: MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.fragment_map_menu, menu)
                if (realEstateId > 0L) {
                    val editBtn = menu.findItem(R.id.edit_real_estate)
                    editBtn.isVisible = true
                    val sellBtn = menu.findItem(R.id.sell_real_estate)
                    sellBtn.isVisible = true
                    val loanBtn = menu.findItem(R.id.loan_simulator)
                    loanBtn.isVisible = true
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                findNavController().navigate(R.id.navigate_from_maps_to_list)
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        val isTablet = requireContext().resources.getBoolean(R.bool.isTablet)
        if (!isTablet) {
            (activity as ItemDetailHostActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
            (activity as ItemDetailHostActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
        }
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

    @SuppressLint("MissingPermission")
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

    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                lifecycleScope.launch {
                    val locationResult = fusedLocationProviderClient.lastLocation
                    locationResult.addOnCompleteListener(requireActivity()) { task ->
                        if (task.isSuccessful) {
                            lastKnownLocation = task.result
                            CURRENT_LOCATION = LatLng(lastKnownLocation.latitude, lastKnownLocation.longitude)
                            if (realEstateId > 0) {
                                Log.e(TAG, "getDeviceLocation: realEstateId:$realEstateId")
                                    mMap.moveCamera(
                                        CameraUpdateFactory.newLatLngZoom(
                                            LatLng(
                                                lastKnownLocation.latitude,
                                                lastKnownLocation.longitude
                                            ), 10f
                                        )
                                    )
                            }
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
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.state.collect{ state ->
                    addMarkerOnMap(state.realEstates)
                }
            }
        }
    }

    private fun addMarkerOnMap(listOfRealEstate: List<RealEstate>) {
        for (realEstate in listOfRealEstate) {
            var iconColor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
            if (realEstate.latitude.isNotEmpty() || realEstate.longitude.isNotEmpty()) {
                val location =
                    LatLng(realEstate.latitude.toDouble(), realEstate.longitude.toDouble())

                if (realEstateId > 0) {
                    if (realEstateId == realEstate.id) {
                        iconColor =
                            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 10F))
                        currentRealEstateLocation = location
                    }
                }

                mMap.addMarker(
                    MarkerOptions()
                        .position(location)
                        .icon(iconColor)
                        .snippet(realEstate.id.toString())
                )
            }
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val markerId = marker.snippet
        val bundle = Bundle()
        bundle.putString(DetailFragment.ARG_ITEM_ID, markerId)
        val itemListNavigationContainer: View? =
            mBinding.root.rootView.findViewById(R.id.item_detail_nav_container)
        if (itemListNavigationContainer != null) {
            itemListNavigationContainer.findNavController()
                .navigate(R.id.sub_graph_fragment_item_detail, bundle)
        } else {
            this.findNavController().navigate(R.id.navigate_from_maps_to_details, bundle)
        }
        return false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("current_location", CURRENT_LOCATION)
    }

    companion object {
        var CURRENT_LOCATION: LatLng? = null
        val DEFAULT_LOCATION = LatLng(43.406656, 3.684383)
    }
}


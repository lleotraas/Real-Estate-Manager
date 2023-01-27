package com.openclassrooms.realestatemanager.features_real_estate.presentation.detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentDetailBinding
import com.openclassrooms.realestatemanager.features_real_estate.domain.model.Image
import com.openclassrooms.realestatemanager.features_real_estate.domain.model.RealEstate
import com.openclassrooms.realestatemanager.features_real_estate.domain.model.RealEstatePhoto
import com.openclassrooms.realestatemanager.features_real_estate.data.utils.PlaceholderContent
import com.openclassrooms.realestatemanager.features_real_estate.presentation.ItemDetailHostActivity
import com.openclassrooms.realestatemanager.features_real_estate.presentation.dialog.loan.LoanSimulatorFragment
import com.openclassrooms.realestatemanager.features_real_estate.presentation.map.OnMapAndViewReadyListener
import com.openclassrooms.realestatemanager.features_real_estate.presentation.RealEstateViewModel
import com.openclassrooms.realestatemanager.features_real_estate.presentation.dialog.sell.SellFragment
import com.openclassrooms.realestatemanager.features_real_estate.data.utils.UtilsKt
import com.openclassrooms.realestatemanager.features_real_estate.data.utils.UtilsKt.Companion.ID
import com.openclassrooms.realestatemanager.features_real_estate.data.utils.UtilsKt.Companion.convertDollarToEuro
import com.stfalcon.imageviewer.StfalconImageViewer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

@AndroidEntryPoint
class DetailFragment : Fragment(), OnMapAndViewReadyListener.OnGlobalLayoutAndMapReadyListener{

    /**
     * The placeholder content this fragment is presenting.
     */
    private var realEstateId = 0L
    private var currentRealEstate: RealEstate? = null
    private lateinit var mFragmentAdapter: DetailAdapter
    private var mMap: GoogleMap? = null
    private val mViewModel: RealEstateViewModel by viewModels()
//    private val state = mViewModel.realEstatePhotoState.value
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private var location: LatLng? = null
    private var listOfRealEstatePhoto: List<RealEstatePhoto>? = null
    private var fullscreenDialog: StfalconImageViewer<Image>? = null
    private var isFullscreenOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args: MutableMap<String, PlaceholderContent.PlaceholderItem> = PlaceholderContent.ITEM_MAP
        realEstateId = if (args.containsKey(ID)) args[ID].toString().toLong() else 0L
        if (savedInstanceState != null) {
            isFullscreenOpen = savedInstanceState.getBoolean(ARG_ITEM_BOOLEAN)
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        if (UtilsKt.isConnectedToInternet(requireContext())) {
            if (googleMap != null) {
                mMap = googleMap
                updateStaticMap()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        val mapFragment = childFragmentManager.findFragmentById(binding.fragmentDetailStaticMap.id) as SupportMapFragment
        OnMapAndViewReadyListener(mapFragment, this)
        mFragmentAdapter = DetailAdapter {
            lifecycleScope.launch {
                mFragmentAdapter.onPhotoClickFullScreen = { realEstatePhoto ->
                    openPhotoInFullScreen(realEstatePhoto)
                }
            }
        }
        setupRecyclerView()
        getCurrentRealEstate()
        getListOfRealEstatePhoto()
        configureSupportNavigateUp()
        configureListeners()
        return binding.root
    }

    private fun configureSupportNavigateUp() {
        requireActivity().addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.fragment_item_details_menu, menu)
                if (realEstateId > 0L) {
                    val editBtn = menu.findItem(R.id.edit_real_estate)
                    editBtn.isVisible = true
                    val sellBtn = menu.findItem(R.id.sell_real_estate)
                    sellBtn.isVisible = true
                    val loanBtn = menu.findItem(R.id.loan_simulator)
                    loanBtn.isVisible = true
                    binding.fragmentDetailLayoutEmpty.visibility = View.GONE
                    binding.fragmentDetailLayoutFull.visibility = View.VISIBLE
                } else {
                    binding.fragmentDetailLayoutEmpty.visibility = View.VISIBLE
                    binding.fragmentDetailLayoutFull.visibility = View.GONE
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                onClickItemSelected(menuItem)
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        val isTablet = requireContext().resources.getBoolean(R.bool.isTablet)
        if (!isTablet) {
            (activity as ItemDetailHostActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
            (activity as ItemDetailHostActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun configureListeners() {
        binding.priceTitleBtn.setOnClickListener {
            if (currentRealEstate!!.price.toString() == binding.priceValueTv.text.toString().replace(Regex("[$.]"), "")) {
                binding.priceValueTv.text = String.format("€%s", UtilsKt.formatPrice(convertDollarToEuro(currentRealEstate!!.price)))
            } else {
                binding.priceValueTv.text = String.format("$%s", UtilsKt.formatPrice(currentRealEstate!!.price))
            }
        }
    }

    private fun openPhotoInFullScreen(realEstatePhoto: RealEstatePhoto) {
        val images = ArrayList<Image>()
        for (image in listOfRealEstatePhoto!!) {
            images.add(Image(image.photo, realEstatePhoto.category))
        }
        fullscreenDialog = StfalconImageViewer.Builder(requireContext(), images) { view, image ->
            Glide.with(view)
                .load(image.url)
                .into(view)
        }.withDismissListener(::onViewerDismissed)
            .show()
        isFullscreenOpen = true
    }

    private fun onViewerDismissed() {
        isFullscreenOpen = false
    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.fragment_item_details_menu, menu)
//        super.onCreateOptionsMenu(menu, inflater)
//
//    }

    fun onClickItemSelected(item: MenuItem) {
        when (item.itemId) {
            R.id.edit_real_estate -> {
                if (currentRealEstate!!.sellerName.isEmpty()) {
                    PlaceholderContent.ITEM_MAP[ID] = PlaceholderContent.PlaceholderItem(ID, currentRealEstate!!.id.toString(), "")
                    this.findNavController().navigate(R.id.navigate_from_detail_to_add_information)
                } else {
                    Toast.makeText(requireContext(), requireContext().resources.getString(R.string.fragment_detail_cannot_edit), Toast.LENGTH_SHORT).show()
                }
            }
            R.id.sell_real_estate -> {
                if (currentRealEstate!!.sellerName.isEmpty()) {
                    val sellDialog = SellFragment()
                    val bundle = Bundle()
                    bundle.putLong("id", currentRealEstate!!.id)
                    sellDialog.arguments = bundle
                    sellDialog.show(requireActivity().supportFragmentManager, sellDialog.tag)
                } else {
                    Toast.makeText(requireContext(), requireContext().resources.getString(R.string.fragment_detail_already_sold), Toast.LENGTH_SHORT).show()
                }
            }
            R.id.loan_simulator -> {
                val loanSimulator = LoanSimulatorFragment()
                val bundle = Bundle()
                val currentPrice = binding.priceValueTv.text.toString().replace(Regex("[$€,.]"), "")
                bundle.putInt("price", currentPrice.toInt())
                loanSimulator.arguments = bundle
                loanSimulator.show(requireActivity().supportFragmentManager, loanSimulator.tag)
            }
            else -> {
                findNavController().navigate(R.id.navigate_from_details_to_list)
            }
        }
    }

    private fun getCurrentRealEstate() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                if(realEstateId > 0) {
                    mViewModel.getRealEstateById(realEstateId).collect { realEstate ->
                        if (realEstateId == realEstate.id) {
                            updateTextView(realEstate)
                            currentRealEstate = realEstate
                            if (UtilsKt.isConnectedToInternet(requireContext())) {
                                if (location == null) {
                                    if (realEstate.latitude.isNotEmpty() || realEstate.longitude.isNotEmpty()) {
                                        location = LatLng(
                                            realEstate.latitude.toDouble(),
                                            realEstate.longitude.toDouble()
                                        )
                                        updateStaticMap()
                                    }
                                }
                            }
                            if (realEstate.pictureListSize > 0) {
                                binding.pictureRecyclerView.visibility = View.VISIBLE
                                binding.fragmentDetailNoPhotoAvailable.visibility = View.GONE
                            } else {
                                binding.pictureRecyclerView.visibility = View.GONE
                                binding.fragmentDetailNoPhotoAvailable.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getListOfRealEstatePhoto() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.getAllRealEstatePhoto(realEstateId).collect { realEstatePhotos ->
                    loadPhotosFromRecyclerView(realEstatePhotos)
                    listOfRealEstatePhoto = realEstatePhotos
                }
            }
        }
            if (isFullscreenOpen) {
                openPhotoInFullScreen(this.listOfRealEstatePhoto!![0])
            }
    }

    @SuppressLint("SimpleDateFormat")
    private fun updateTextView(realEstate: RealEstate) {
        binding.priceValueTv.text = String.format("$%s", UtilsKt.formatPrice(realEstate.price))
        binding.descriptionTv.text = realEstate.description
        binding.surfaceValueTv.text = String.format("%s %s", realEstate.surface, requireContext().resources.getString(R.string.item_list_fragment_surface))
        binding.roomsNumberValueTv.text = realEstate.rooms.toString()
        binding.bathroomsValueTv.text = realEstate.bathrooms.toString()
        binding.bedroomsValueTv.text = realEstate.bedrooms.toString()
        binding.poiValueTv.text = realEstate.pointOfInterest
        binding.locationValueTv.text = realEstate.address.replace(", ", "\n")
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        binding.fragmentItemDetailCreationDate.text = dateFormat.format(realEstate.creationDate)
    }

    private fun updateStaticMap() {
        if (mMap != null) {
            if (location != null) {
                mMap!!.addMarker(
                    MarkerOptions()
                        .position(location!!)
                )
                mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(location!!, 16F))
                //TODO map don't refresh when come back to detail activity after an address update
            }
        }
    }

    private fun setupRecyclerView() = binding.pictureRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext()
                , LinearLayoutManager.HORIZONTAL, false
            )
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.HORIZONTAL
                )
            )
    }

    private fun loadPhotosFromRecyclerView(listOfRealEstatePhoto: List<RealEstatePhoto>) {
        mFragmentAdapter.submitList(listOfRealEstatePhoto)
        binding.pictureRecyclerView.adapter = mFragmentAdapter
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(ARG_ITEM_BOOLEAN, isFullscreenOpen)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
       if (isFullscreenOpen) {
            fullscreenDialog!!.dismiss()
        }
    }

    companion object {
        const val ARG_ITEM_ID = "item_id"
        const val ARG_ITEM_BOOLEAN = "is_fullscreen_open"
    }
}
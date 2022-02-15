package com.openclassrooms.realestatemanager.ui.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.RealEstateViewModelFactory
import com.openclassrooms.realestatemanager.databinding.FragmentDetailBinding
import com.openclassrooms.realestatemanager.dependency.RealEstateApplication
import com.openclassrooms.realestatemanager.model.Image
import com.openclassrooms.realestatemanager.model.RealEstate
import com.openclassrooms.realestatemanager.model.RealEstatePhoto
import com.openclassrooms.realestatemanager.placeholder.PlaceholderContent
import com.openclassrooms.realestatemanager.ui.AddRealEstateActivity
import com.openclassrooms.realestatemanager.ui.loan_simulator.LoanSimulatorFragment
import com.openclassrooms.realestatemanager.ui.map.OnMapAndViewReadyListener
import com.openclassrooms.realestatemanager.ui.real_estate.RealEstateViewModel
import com.openclassrooms.realestatemanager.ui.sell_fragment.SellFragment
import com.openclassrooms.realestatemanager.utils.Utils
import com.openclassrooms.realestatemanager.utils.UtilsKt
import com.stfalcon.imageviewer.StfalconImageViewer
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat


class DetailFragment : Fragment(), OnMapAndViewReadyListener.OnGlobalLayoutAndMapReadyListener{

    /**
     * The placeholder content this fragment is presenting.
     */
    private var realEstateId: PlaceholderContent.PlaceholderItem? = null
    private var currentRealEstate: RealEstate? = null
    private lateinit var mFragmentAdapter: ItemDetailAdapter
    private var mMap: GoogleMap? = null
    private val mViewModel: RealEstateViewModel by viewModels {
        RealEstateViewModelFactory(
            (requireActivity().application as RealEstateApplication).realEstateRepository,
            (requireActivity().application as RealEstateApplication).realEstateImageRepository,
            (requireActivity().application as RealEstateApplication).filterRepository)
    }
    private var toolbarLayout: CollapsingToolbarLayout? = null
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private var location: LatLng? = null
    private var listOfRealEstatePhoto: List<RealEstatePhoto>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID)) {
                realEstateId = PlaceholderContent.ITEM_MAP[it.getString(ARG_ITEM_ID)]
            }
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
        val rootView = binding.root
        val mapFragment =childFragmentManager.findFragmentById(binding.staticMap.id) as SupportMapFragment
        OnMapAndViewReadyListener(mapFragment, this)
        mFragmentAdapter = ItemDetailAdapter {
            lifecycleScope.launch {
                mFragmentAdapter.onPhotoClickFullScreen = { realEstatePhoto ->
                    openPhotoInFullScreen(realEstatePhoto)
                }
            }
        }
        setupRecyclerView()
        if (realEstateId != null) {
            getCurrentRealEstate()
            getListOfRealEstatePhoto()
        }
        setHasOptionsMenu(true)
        configureListeners()
        return rootView
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun configureListeners() {
        binding.priceTitleBtn!!.setOnClickListener {
            if (currentRealEstate!!.price.toString() == binding.priceValueTv!!.text.toString().replace("$", "").replace(".", "")) {
                binding.priceValueTv!!.text = String.format("€%s", UtilsKt.formatPrice(Utils.convertDollarToEuro(currentRealEstate!!.price)))
            } else {
//                stringToDisplay = "$${UtilsKt.convertEuroToDollar(currentRealEstate!!.price)}"
                binding.priceValueTv!!.text = String.format("$%s", UtilsKt.formatPrice(currentRealEstate!!.price))
            }
        }
    }

    private fun openPhotoInFullScreen(realEstatePhoto: RealEstatePhoto) {
        val images = ArrayList<Image>()
        for (image in listOfRealEstatePhoto!!) {
            images.add(Image(image.photo, realEstatePhoto.category))
        }
        StfalconImageViewer.Builder(requireContext(), images) { view, image ->
            Glide.with(view)
                .load(image.url)
                .into(view)
        }.show()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_item_details_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
        if (realEstateId != null) {
            val editBtn = menu.findItem(R.id.edit_real_estate)
            editBtn.isVisible = true
            val sellBtn = menu.findItem(R.id.sell_real_estate)
            sellBtn.isVisible = true
            val loanBtn = menu.findItem(R.id.loan_simulator)
            loanBtn.isVisible = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit_real_estate -> {
                if (currentRealEstate!!.sellerName.isEmpty()) {
                    val intent = Intent(requireContext(), AddRealEstateActivity::class.java)
                    intent.putExtra("id", currentRealEstate!!.id)
                    startActivity(intent)
                } else {
                    Toast.makeText(requireContext(), requireContext().resources.getString(R.string.item_detail_fragment_cannot_edit), Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(requireContext(), requireContext().resources.getString(R.string.item_detail_fragment_already_sold), Toast.LENGTH_SHORT).show()
                }
            }
            R.id.loan_simulator -> {
                val loanSimulator = LoanSimulatorFragment()
                val bundle = Bundle()
                bundle.putInt("price", currentRealEstate!!.price)
                loanSimulator.arguments = bundle
                loanSimulator.show(requireActivity().supportFragmentManager, loanSimulator.tag)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getCurrentRealEstate() {
        mViewModel.getRealEstateById(realEstateId!!.id.toLong()).observe(viewLifecycleOwner) { realEstate ->
                if (realEstateId?.id == realEstate.id.toString()) {
                    updateTextView(realEstate)
                    currentRealEstate = realEstate
                    if (UtilsKt.isConnectedToInternet(requireContext())) {
                        if (location == null) {
                            if  (realEstate.latitude.isNotEmpty() || realEstate.longitude.isNotEmpty()) {
                                location = LatLng(
                                    realEstate.latitude.toDouble(),
                                    realEstate.longitude.toDouble()
                                )
                                updateStaticMap()
                            }
                        }
                    }
                }
//            }
        }
    }

    private fun getListOfRealEstatePhoto() {
        mViewModel.getAllRealEstatePhoto(realEstateId!!.id.toLong()).observe(viewLifecycleOwner) { listOfRealEstatePhoto ->
            loadPhotosFromRecyclerView(listOfRealEstatePhoto)
            this.listOfRealEstatePhoto = listOfRealEstatePhoto
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun updateTextView(realEstate: RealEstate) {
        binding.priceValueTv!!.text = String.format("$%s", UtilsKt.formatPrice(realEstate.price))
        binding.descriptionTv.text = realEstate.description
        binding.surfaceValueTv.text = String.format("%s %s", realEstate.surface, requireContext().resources.getString(R.string.item_list_fragment_surface))
        binding.roomsNumberValueTv.text = realEstate.rooms.toString()
        binding.bathroomsValueTv.text = realEstate.bathrooms.toString()
        binding.bedroomsValueTv.text = realEstate.bedrooms.toString()
        binding.poiValueTv!!.text = realEstate.pointOfInterest.toString().replace(",", "\n").replace("[", "").replace("]", "")
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
                mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(location!!, 15F))
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

    companion object {
        const val ARG_ITEM_ID = "item_id"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
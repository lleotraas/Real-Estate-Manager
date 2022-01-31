package com.openclassrooms.realestatemanager.ui.detail

import android.content.ContentUris
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.android.material.appbar.CollapsingToolbarLayout
import com.mapquest.mapping.MapQuest
import com.openclassrooms.realestatemanager.OnMapAndViewReadyListener
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.RealEstateViewModelFactory
import com.openclassrooms.realestatemanager.databinding.FragmentItemDetailBinding

import com.openclassrooms.realestatemanager.dependency.RealEstateApplication
import com.openclassrooms.realestatemanager.model.RealEstate
import com.openclassrooms.realestatemanager.model.RealEstateImage
import com.openclassrooms.realestatemanager.model.SharedStoragePhoto
import com.openclassrooms.realestatemanager.placeholder.PlaceholderContent
import com.openclassrooms.realestatemanager.ui.RealEstateViewModel
import com.openclassrooms.realestatemanager.utils.sdk29AndUp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a [ItemListFragment]
 * in two-pane mode (on larger screen devices) or self-contained
 * on handsets.
 */
class ItemDetailFragment : Fragment(){

    /**
     * The placeholder content this fragment is presenting.
     */
    private var realEstateId: PlaceholderContent.PlaceholderItem? = null
    private var realEstatePictureList = ArrayList<SharedStoragePhoto>()
    private lateinit var mAdapter: FragmentAddAdapter
    private lateinit var mRecyclerView: RecyclerView
    private val mViewModel: RealEstateViewModel by viewModels {
        RealEstateViewModelFactory(
            (requireActivity().application as RealEstateApplication).realEstateRepository,
            (requireActivity().application as RealEstateApplication).realEstateImageRepository)
    }
    private var toolbarLayout: CollapsingToolbarLayout? = null
    private var _binding: FragmentItemDetailBinding? = null
    private val binding get() = _binding!!
//    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID)) {
                // Load the placeholder content specified by the fragment
                // arguments. In a real-world scenario, use a Loader
                // to load content from a content provider.
                realEstateId = PlaceholderContent.ITEM_MAP[it.getString(ARG_ITEM_ID)]
//                realEstatePictureList = PlaceholderContent.ITEM_MAP[it.getStringArrayList(ARG_ITEM_IMAGE_LIST)]
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentItemDetailBinding.inflate(inflater, container, false)
        val rootView = binding.root

        var realEstateModel: RealEstate? = null
        mViewModel.getAllRealEstate.observe(viewLifecycleOwner) {
            for (realEstate in it) {
                if (realEstateId?.id == realEstate.id.toString()) {
                    realEstateModel = realEstate
                }
            }
            if (realEstateModel != null) {
                updateTextView(realEstateModel!!)
            }
        }
        getPictureList()
        updateContent(realEstateModel?.property ?: "" )
        Mapbox.start(requireContext())
//        val mapFragment = childFragmentManager.findFragmentById(R.id.fragment_detail_map_view) as SupportMapFragment
//        OnMapAndViewReadyListener(mapFragment!!, this)
        return rootView
    }

//    override fun onMapReady(googleMap: GoogleMap?) {
//        map = googleMap ?: return
//        addMarkers()
//    }

//    private fun addMarkers() {
//        map.addMarker(
//            MarkerOptions()
//                .position(SYDNEY)
//                .title("Sydney")
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
//        )
//        map.animateCamera(CameraUpdateFactory.newLatLngZoom(SYDNEY, 15F))
//    }

    private fun getPictureList() {
            realEstateId?.id?.let {
                mViewModel.getRealEstateAndImage(it.toLong())
                    .observe(viewLifecycleOwner) { realEstateImageList ->
                        updateListOfPicture(realEstateImageList as ArrayList<RealEstateImage>)
                    }
            }
    }

    private suspend fun loadPhotosFromExternalStorage(): List<SharedStoragePhoto> {
        return withContext(Dispatchers.IO) {
            val collection = sdk29AndUp {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI

            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT
            )

            val photos = mutableListOf<SharedStoragePhoto>()
            requireContext().contentResolver.query(
                collection,
                projection,
                null,
                null,
                "${MediaStore.Images.Media.DISPLAY_NAME} ASC"
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val displayName = cursor.getString(displayNameColumn)
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    photos.add(SharedStoragePhoto(id, displayName, contentUri))
                }
                photos.toList()
            } ?: listOf()
        }
    }

    private fun updateListOfPicture(realEstateImage: ArrayList<RealEstateImage>) {
        lifecycleScope.launch {
            val photos = loadPhotosFromExternalStorage()
            for (image in realEstateImage) {
                for (sharedStoragePhoto in photos) {
                    if (image.imageUri == sharedStoragePhoto.contentUri.toString()) {
                        realEstatePictureList.add(sharedStoragePhoto)
                    }
                }
            }
            mAdapter = FragmentAddAdapter(realEstatePictureList)
            mRecyclerView.adapter = mAdapter
        }
    }

    private fun updateContent(property: String) {
        toolbarLayout?.title = property
        mRecyclerView = binding.pictureRecyclerView
        mAdapter = FragmentAddAdapter(realEstatePictureList)
        mRecyclerView.layoutManager = LinearLayoutManager(requireContext()
            , LinearLayoutManager.HORIZONTAL, false
            )
        mRecyclerView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.HORIZONTAL
            )
        )
        mRecyclerView.adapter = mAdapter
    }

    private fun updateTextView(realEstate: RealEstate) {
        binding.locationValueTv.text = realEstate.address
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"
//        private val SYDNEY = LatLng(-33.87365, 151.20689)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
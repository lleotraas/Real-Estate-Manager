package com.openclassrooms.realestatemanager.ui.detail

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.openclassrooms.realestatemanager.BuildConfig
import com.openclassrooms.realestatemanager.RealEstateViewModelFactory
import com.openclassrooms.realestatemanager.databinding.FragmentItemDetailBinding
import com.openclassrooms.realestatemanager.dependency.RealEstateApplication
import com.openclassrooms.realestatemanager.model.RealEstate
import com.openclassrooms.realestatemanager.placeholder.PlaceholderContent
import com.openclassrooms.realestatemanager.retrofit.RetrofitInstance
import com.openclassrooms.realestatemanager.ui.RealEstateViewModel
import com.openclassrooms.realestatemanager.utils.UriPathHelper
import kotlinx.coroutines.launch
import java.io.File


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
    private var realEstatePictureList = ArrayList<Uri>()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID)) {
                // Load the placeholder content specified by the fragment
                // arguments. In a real-world scenario, use a Loader
                // to load content from a content provider.
                realEstateId = PlaceholderContent.ITEM_MAP[it.getString(ARG_ITEM_ID)]
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
        mViewModel.getAllRealEstate.observe(viewLifecycleOwner) { listOfRealEstate ->
            for (realEstate in listOfRealEstate) {
                if (realEstateId?.id == realEstate.id.toString()) {
                    realEstateModel = realEstate
                }
            }
            if (realEstateModel != null) {
                updateTextView(realEstateModel!!)
                val location = String.format("%s,%s", realEstateModel!!.latitude, realEstateModel!!.longitude)
                RetrofitInstance.getBitmapFrom(
                    HTTP_REQUEST,
                    location,
                    "13", "1500x1100",
                    "2", "jpg",
                    location,
                    BuildConfig.GMP_KEY)  {
                    Glide.with(binding.root)
                        .load(it).centerCrop()
                        .into(binding.staticMap)
                }
            }
        }
        getPictureList()
        updateContent(realEstateModel?.property ?: "" )
        return rootView
    }

    private fun getPictureList() {
            realEstateId?.id?.let {
                mViewModel.getRealEstateAndImage(it.toLong())
                    .observe(viewLifecycleOwner) { realEstateImageList ->
                        val uriPathHelper = UriPathHelper()
                        val list = ArrayList<String?>()
                        for (uri in realEstateImageList) {
                            list.add(uriPathHelper.getPath(requireContext(), uri.imageUri.toUri()))
                        }
                        updateListOfPicture(list)
                    }
            }
    }

    private fun loadPhotosFromAppDirectory(list: ArrayList<String?>): ArrayList<Bitmap> {
        val listOfImage = ArrayList<Bitmap>()
        for (imagePath in list) {
            val imageFile = File(imagePath!!)
            if (imageFile.exists()) {
                val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                listOfImage.add(bitmap)
            }
        }
        return listOfImage
    }

    private fun updateListOfPicture(
        list: ArrayList<String?>
    ) {
        lifecycleScope.launch {
            mRecyclerView = binding.pictureRecyclerView
            mRecyclerView.layoutManager = LinearLayoutManager(requireContext()
                , LinearLayoutManager.HORIZONTAL, false
            )
            mRecyclerView.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.HORIZONTAL
                )
            )
            mAdapter = FragmentAddAdapter(loadPhotosFromAppDirectory(list))
            mRecyclerView.adapter = mAdapter

        }
    }

    private fun updateContent(property: String) {
        toolbarLayout?.title = property
    }

    private fun updateTextView(realEstate: RealEstate) {
        binding.descriptionTv.text = realEstate.description
        binding.surfaceValueTv.text = realEstate.surface
        binding.roomsNumberValueTv.text = realEstate.rooms
        binding.bathroomsValueTv.text = realEstate.bathrooms
        binding.bedroomsValueTv.text = realEstate.bedrooms
        binding.locationValueTv.text = realEstate.address
        binding.fragmentItemDetailCreationDate?.text = realEstate.creationDate
                //TODO show POI on static map
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"
        private const val HTTP_REQUEST = "https://maps.googleapis.com/maps/api/staticmap"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
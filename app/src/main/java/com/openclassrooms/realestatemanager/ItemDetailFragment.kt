package com.openclassrooms.realestatemanager

import android.content.ClipData
import android.os.Bundle
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.openclassrooms.realestatemanager.databinding.FragmentItemDetailBinding
import com.openclassrooms.realestatemanager.dependency.RealEstateApplication
import com.openclassrooms.realestatemanager.model.RealEstate
import com.openclassrooms.realestatemanager.model.RealEstateImage
import com.openclassrooms.realestatemanager.placeholder.PlaceholderContent
import com.openclassrooms.realestatemanager.ui.FragmentAddRealEstateAdapter
import com.openclassrooms.realestatemanager.ui.RealEstateViewModel


/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a [ItemListFragment]
 * in two-pane mode (on larger screen devices) or self-contained
 * on handsets.
 */
class ItemDetailFragment : Fragment() {

    /**
     * The placeholder content this fragment is presenting.
     */
    private var realEstateId: PlaceholderContent.PlaceholderItem? = null
    private var realEstateAddress: PlaceholderContent.PlaceholderItem? = null
    private var realEstateType: PlaceholderContent.PlaceholderItem? = null
    private var realEstateState: PlaceholderContent.PlaceholderItem? = null
    private var realEstatePrice: PlaceholderContent.PlaceholderItem? = null
    private var realEstatePictureList = ArrayList<RealEstateImage>()
    private lateinit var mAdapter: FragmentAddRealEstateAdapter
    private lateinit var mRecyclerView: RecyclerView
    private val mViewModel: RealEstateViewModel by viewModels {
        RealEstateViewModelFactory(
            (requireActivity().application as RealEstateApplication).realEstateRepository,
            (requireActivity().application as RealEstateApplication).realEstateImageRepository)
    }

    lateinit var itemDetailTextView: TextView
    private var toolbarLayout: CollapsingToolbarLayout? = null

    private var _binding: FragmentItemDetailBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val dragListener = View.OnDragListener { v, event ->
        if (event.action == DragEvent.ACTION_DROP) {
            val clipDataItem: ClipData.Item = event.clipData.getItemAt(0)
            val dragData = clipDataItem.text
            realEstateId = PlaceholderContent.ITEM_MAP[dragData]
            updateContent()
        }
        true
    }

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

    private fun getPictureList() {
        realEstateId?.id?.let {
            mViewModel.getRealEstateAndImage(it.toLong()).observe(viewLifecycleOwner) { realEstateImageList ->
                updateListOfPicture(realEstateImageList as ArrayList<RealEstateImage>)
            }
        }
    }

    private fun updateListOfPicture(realEstateImage: ArrayList<RealEstateImage>) {
        for (picture in realEstateImage) {
            realEstatePictureList.add(picture)
        }
        mAdapter = FragmentAddRealEstateAdapter(realEstatePictureList)
        mRecyclerView.adapter = mAdapter
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentItemDetailBinding.inflate(inflater, container, false)
        val rootView = binding.root
        var realEstateModel: RealEstate? = null
        mViewModel.getAllRealEstate.observe(viewLifecycleOwner) {
            for (realEstate in it) {
                if (realEstateId?.id == realEstate.id.toString()) {
                    realEstateModel = realEstate
                }
            }
        }
//        realEstateModel?.let { realEstate ->
//            mViewModel.getRealEstateAndImage(realEstate.id).observe(viewLifecycleOwner) {
//                realEstatePictureList.addAll(it)
//            }
//        }
        getPictureList()
        updateContent()
        rootView.setOnDragListener(dragListener)

        return rootView
    }

    private fun updateContent() {


        toolbarLayout?.title = realEstateType?.content
//        mAdapter = FragmentAddRealEstateAdapter(realEstatePictureList)
        mRecyclerView = binding.pictureRecyclerView
        mAdapter = FragmentAddRealEstateAdapter(realEstatePictureList)
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

        // Show the placeholder content as text in a TextView.
        realEstateAddress?.let {
            itemDetailTextView.text = it.id
        }
        binding.locationValueTv.text = realEstateAddress.toString()

    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"
        const val ARG_ITEM_TYPE = "item_type"
        const val ARG_ITEM_ADDRESS = "item_address"
        const val ARG_ITEM_STATE = "item_state"
        const val ARG_ITEM_IMAGE_LIST = "item_image_list"
        const val ARG_ITEM_PRICE = "item_price"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
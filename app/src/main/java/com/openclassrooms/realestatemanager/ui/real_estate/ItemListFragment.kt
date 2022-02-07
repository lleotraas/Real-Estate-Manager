package com.openclassrooms.realestatemanager.ui.real_estate

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.RealEstateViewModelFactory
import com.openclassrooms.realestatemanager.databinding.FragmentItemListBinding
import com.openclassrooms.realestatemanager.databinding.RealEstateRowBinding
import com.openclassrooms.realestatemanager.dependency.RealEstateApplication
import com.openclassrooms.realestatemanager.model.RealEstate
import com.openclassrooms.realestatemanager.model.RealEstateImage
import com.openclassrooms.realestatemanager.ui.AddRealEstateActivity
import com.openclassrooms.realestatemanager.ui.FilterActivity
import com.openclassrooms.realestatemanager.ui.MapViewActivity
import com.openclassrooms.realestatemanager.ui.RealEstateViewModel
import com.openclassrooms.realestatemanager.ui.detail.ItemDetailFragment
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * A Fragment representing a list of Pings. This fragment
 * has different presentations for handset and larger screen devices. On
 * handsets, the fragment presents a list of items, which when touched,
 * lead to a {@link ItemDetailFragment} representing
 * item details. On larger screens, the Navigation controller presents the list of items and
 * item details side-by-side using two vertical panes.
 */

class ItemListFragment : Fragment() {

    /**
     * Method to intercept global key events in the
     * item list fragment to trigger keyboard shortcuts
     * Currently provides a toast when Ctrl + Z and Ctrl + F
     * are triggered
     */
    private val unhandledKeyEventListenerCompat =
        ViewCompat.OnUnhandledKeyEventListenerCompat { v, event ->
            if (event.keyCode == KeyEvent.KEYCODE_Z && event.isCtrlPressed) {
                Toast.makeText(
                    v.context,
                    "Undo (Ctrl + Z) shortcut triggered",
                    Toast.LENGTH_LONG
                ).show()

            } else if (event.keyCode == KeyEvent.KEYCODE_F && event.isCtrlPressed) {
                Toast.makeText(
                    v.context,
                    "Find (Ctrl + F) shortcut triggered",
                    Toast.LENGTH_LONG
                ).show()

            }
            false
        }

    private var _binding: FragmentItemListBinding? = null
    private val binding get() = _binding!!

    private val mViewModel: RealEstateViewModel by viewModels {
        RealEstateViewModelFactory(
            (requireActivity().application as RealEstateApplication).realEstateRepository,
            (requireActivity().application as RealEstateApplication).realEstateImageRepository)
    }

    private lateinit var adapter: SimpleItemRecyclerViewAdapter
    private var realEstateList = ArrayList<RealEstate>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentItemListBinding.inflate(inflater, container, false)

        mViewModel.getAllRealEstate.observe(viewLifecycleOwner) {
            if (realEstateList.isNotEmpty()) {
                realEstateList.clear()
            }
            realEstateList.addAll(it)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.addOnUnhandledKeyEventListener(view, unhandledKeyEventListenerCompat)

        val recyclerView = binding.itemList

        // Leaving this not using view binding as it relies on if the view is visible the current
        // layout configuration (layout, layout-sw600dp)
        val itemDetailFragmentContainer: View? = view.findViewById(R.id.item_detail_nav_container)

        /** Click Listener to trigger navigation based on if you have
         * a single pane layout or two pane layout
         */
        val onClickListener = View.OnClickListener { itemView ->
            val item = itemView.tag as RealEstate
            val bundle = Bundle()
            val listOfPicture = ArrayList<String>()
            mViewModel.getRealEstateAndImage(item.id).observe(viewLifecycleOwner) {
                for (realEstateImage in it) {
                    listOfPicture.add(realEstateImage.imageUri)
                }
            }
            bundle.putString(ItemDetailFragment.ARG_ITEM_ID, item.id.toString())

            if (itemDetailFragmentContainer != null) {
                itemDetailFragmentContainer.findNavController()
                    .navigate(R.id.fragment_item_detail, bundle)
            } else {
                itemView.findNavController().navigate(R.id.show_item_detail, bundle)
            }
        }

        /**
         * Context click listener to handle Right click events
         * from mice and trackpad input to provide a more native
         * experience on larger screen devices
         */
        val onContextClickListener = View.OnContextClickListener {
//                v ->
//            val item = v.tag as PlaceholderContent.PlaceholderItem

            true
        }
        setupRecyclerView(recyclerView, onClickListener, onContextClickListener)
        configureListeners()
    }

    private fun setupRecyclerView(recyclerView: RecyclerView, onClickListener: View.OnClickListener, onContextClickListener: View.OnContextClickListener) {
        adapter = SimpleItemRecyclerViewAdapter(onClickListener, onContextClickListener)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//        mViewModel.getAllRealEstate.observe(requireActivity()) {realEstate ->
//            realEstate.let { adapter.submitList(it) }
//        }
        adapter.submitList(realEstateList)
    }

    private fun configureListeners() {
        binding.addFab.setOnClickListener{
            val startForResults = Intent(requireContext(), AddRealEstateActivity::class.java)
            getAddActivityResult.launch(startForResults)
        }
        binding.mapFab.setOnClickListener {
            startActivity(Intent(requireContext(), MapViewActivity::class.java))
        }
        binding.filterFab.setOnClickListener {
            val startForResult = Intent(it.context, FilterActivity::class.java)
            getFilterActivityResult.launch(startForResult)
        }
    }

    private val getAddActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            onAddActivityResult(result.data)
        } else {
            Toast.makeText(
                requireContext(),
                requireContext().resources.getString(R.string.main_fragment_error_save_estate),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private val getFilterActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            onFilterActivityResult(result.data)
        }
    }

    private fun onFilterActivityResult(data: Intent?) {
        val listOfId = data?.getStringArrayListExtra("list_of_id")
        val filteredList = realEstateList.filter { realEstate -> listOfId?.map { it }?.contains(realEstate.id.toString()) ?: false }
        adapter.submitList(filteredList)
    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    @SuppressLint("SimpleDateFormat")
    private fun onAddActivityResult(data: Intent?) {
//        val property = data?.getStringExtra("property") ?: "?"
//        val price = data?.getStringExtra("price") ?: "0"
//        val surface = data?.getStringExtra("surface") ?: "0"
//        val rooms = data?.getStringExtra("rooms") ?: "0"
//        val bathrooms = data?.getStringExtra("bathrooms") ?: "0"
//        val bedrooms = data?.getStringExtra("bedrooms") ?: "0"
//        val description = data?.getStringExtra("description") ?: "0"
//        val address = data?.getStringExtra("address") ?: "?"
//        val latitude = data?.getStringExtra("latitude") ?: "?"
//        val longitude = data?.getStringExtra("longitude") ?: "?"
//        val pointOfInterest = data?.getStringArrayListExtra("pointOfInterest") as ArrayList<String>
//        val state = data.getStringExtra("state") ?: "?"
//        val creationDate = data.getStringExtra("creationDate") ?: "?"
//        val listOfImages = data.getStringArrayListExtra("photos") as List<String>
//
//        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
//        val date = dateFormat.parse(creationDate)
//        val creationDateInDays = date.time / 86400000 + 7



//        val realEstate = RealEstate(
//            0,
//            property,
//            price,
//            surface,
//            rooms,
//            bathrooms,
//            bedrooms,
//            description,
//            listOfImages[0],
//            address,
//            latitude,
//            longitude,
//            pointOfInterest,
//            state,
//            creationDate,
//            creationDateInDays.toString(),
//            "",
//            ""
//        )
//            realEstate.let { mViewModel.insert(it) }
//        mViewModel.getRealEstateByAddress(realEstate.address).observe(viewLifecycleOwner) {
//            for (i in listOfImages.indices) {
//                val realEstateImages = RealEstateImage(0, it.id, listOfImages[i])
//                mViewModel.insert(realEstateImages)
//            }
//        }

    }

    class SimpleItemRecyclerViewAdapter(
        private val onClickListener: View.OnClickListener,
        private val onContextClickListener: View.OnContextClickListener
    ) :
        ListAdapter<RealEstate, SimpleItemRecyclerViewAdapter.ViewHolder>(RealEstateComparator()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

            val binding =
                RealEstateRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)

        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = getItem(position)
            holder.bind(getItem(position))

            with(holder.itemView) {
                tag = item
                setOnClickListener(onClickListener)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setOnContextClickListener(onContextClickListener)
                }
            }
        }

        inner class ViewHolder(private val binding: RealEstateRowBinding) : RecyclerView.ViewHolder(binding.root) {

            fun bind(realEstate: RealEstate) {
                val numberFormat = NumberFormat.getInstance(Locale.ITALIAN)
                val formatPrice = numberFormat.format(Integer.parseInt(realEstate.price))
                binding.realEstateRowCity.text = realEstate.state
                binding.realEstateRowPrice.text = String.format("%s%s", binding.root.resources.getString(R.string.item_list_fragment_currency), formatPrice)
                binding.realEstateRowType.text = realEstate.property
                Glide.with(binding.root)
                    .load(realEstate.picture)
                    .centerCrop()
                    .into(binding.realEstateRowImageView)
            }
        }

        class RealEstateComparator : DiffUtil.ItemCallback<RealEstate>() {
            override fun areItemsTheSame(oldItem: RealEstate, newItem: RealEstate): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: RealEstate, newItem: RealEstate): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.openclassrooms.realestatemanager.ui.real_estate

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
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
import com.openclassrooms.realestatemanager.placeholder.PlaceholderContent;
import com.openclassrooms.realestatemanager.dependency.RealEstateApplication
import com.openclassrooms.realestatemanager.model.RealEstate
import com.openclassrooms.realestatemanager.model.RealEstateImage
import com.openclassrooms.realestatemanager.ui.AddRealEstateActivity
import com.openclassrooms.realestatemanager.ui.RealEstateViewModel
import com.openclassrooms.realestatemanager.ui.detail.ItemDetailFragment
import java.text.NumberFormat
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
                true
            } else if (event.keyCode == KeyEvent.KEYCODE_F && event.isCtrlPressed) {
                Toast.makeText(
                    v.context,
                    "Find (Ctrl + F) shortcut triggered",
                    Toast.LENGTH_LONG
                ).show()
                true
            }
            false
        }

    private var _binding: FragmentItemListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val newRealEstateActivityRequestCode = 123

    private lateinit var startForResult: ActivityResultLauncher<RealEstate>

    private val mViewModel: RealEstateViewModel by viewModels {
        RealEstateViewModelFactory(
            (requireActivity().application as RealEstateApplication).realEstateRepository,
            (requireActivity().application as RealEstateApplication).realEstateImageRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentItemListBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.addOnUnhandledKeyEventListener(view, unhandledKeyEventListenerCompat)

        val recyclerView: RecyclerView = binding.itemList

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
        val onContextClickListener = View.OnContextClickListener { v ->
            val item = v.tag as PlaceholderContent.PlaceholderItem

            true
        }
        setupRecyclerView(recyclerView, onClickListener, onContextClickListener)
    }

    private fun setupRecyclerView(
        recyclerView: RecyclerView,
        onClickListener: View.OnClickListener,
        onContextClickListener: View.OnContextClickListener
    ) {
        val adapter = SimpleItemRecyclerViewAdapter(
            onClickListener,
            onContextClickListener
        )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        mViewModel.getAllRealEstate.observe(requireActivity()) {realEstate ->
            realEstate.let { adapter.submitList(it) }
        }
        binding.addFab.setOnClickListener{
            val startForResults = Intent(requireContext(), AddRealEstateActivity::class.java)
            getResult.launch(startForResults)
//            startActivityForResult(Intent(requireContext(), AddRealEstateActivity::class.java), newRealEstateActivityRequestCode)
        }
    }

    private val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            onActivityResult(result.data)
        } else {
        Toast.makeText(
            requireContext(),
            requireContext().resources.getString(R.string.main_fragment_error_save_estate),
            Toast.LENGTH_LONG
        ).show()
    }
    }

     private fun onActivityResult(data: Intent?) {
        val address = data?.getStringExtra("city") ?: "?"
        val price = data?.getStringExtra("price") ?: "0"
        val type = data?.getStringExtra("type") ?: "?"
        val state = data?.getStringExtra("state") ?: "?"
         val staticMap = data?.getByteArrayExtra("static_map") as ByteArray
        val listOfImages = data.getStringArrayListExtra("photos") as List<String>
//        val listOfCategories = data.getStringArrayListExtra("categories") as List<String>
        val numberFormat = NumberFormat.getInstance(Locale.ITALIAN)
        val formatPrice = numberFormat.format(Integer.parseInt(price))
        val flag = data.flags
        flag.toString()

        val realEstate = RealEstate(
            0,
            type,
            String.format("%s%s", resources.getString(R.string.item_list_fragment_currency), formatPrice),
            listOfImages[0],
            staticMap,
            address,
            state
        )
            realEstate.let { mViewModel.insert(it) }
        mViewModel.getRealEstateByAddress(realEstate.address).observe(viewLifecycleOwner) {
            for (i in listOfImages.indices) {
                val realEstateImages = RealEstateImage(0, it.id, listOfImages[i])
                mViewModel.insert(realEstateImages)
            }
        }

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
                binding.realEstateRowCity.text = realEstate.state
                binding.realEstateRowPrice.text = realEstate.price
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
                return oldItem.address == newItem.address
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
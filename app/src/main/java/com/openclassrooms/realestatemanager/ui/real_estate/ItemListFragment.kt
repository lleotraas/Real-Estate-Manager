package com.openclassrooms.realestatemanager.ui.real_estate

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
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
import com.openclassrooms.realestatemanager.ui.AddRealEstateActivity
import com.openclassrooms.realestatemanager.ui.detail.ItemDetailFragment
import com.openclassrooms.realestatemanager.ui.filter.BottomSheetFragment
import java.text.NumberFormat
import java.util.*

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
            (requireActivity().application as RealEstateApplication).realEstateImageRepository,
            (requireActivity().application as RealEstateApplication).filterRepository)
    }

    private lateinit var adapter: SimpleItemRecyclerViewAdapter
    private lateinit var rootView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentItemListBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.addOnUnhandledKeyEventListener(view, unhandledKeyEventListenerCompat)

        val recyclerView = binding.itemList

        // Leaving this not using view binding as it relies on if the view is visible the current
        // layout configuration (layout, layout-sw600dp)
        rootView = view
        val itemDetailFragmentContainer: View? = view.findViewById(R.id.item_detail_nav_container)
        val onClickListener = View.OnClickListener { itemView ->
            val item = itemView.tag as RealEstate
            val bundle = Bundle()
            bundle.putString(ItemDetailFragment.ARG_ITEM_ID, item.id.toString())

            if (itemDetailFragmentContainer != null) {
                val currentSubView: View? =
                    itemDetailFragmentContainer.rootView.findViewById(R.id.item_detail_container) ?: itemDetailFragmentContainer.rootView.findViewById(R.id.fragment_map_view_container)
                if (currentSubView != null) {
                    if (currentSubView.id == R.id.item_detail_container) {
                        itemDetailFragmentContainer.findNavController()
                            .navigate(R.id.sub_graph_fragment_item_detail, bundle)
                    } else {
                        itemDetailFragmentContainer.findNavController()
                            .navigate(R.id.sub_graph_fragment_map_view, bundle)
                    }
                } else {
                    itemView.findNavController().navigate(R.id.navigate_to_detail_fragment, bundle)
                }
            }
        }
        val realEstateList = ArrayList<RealEstate>()
        adapter = SimpleItemRecyclerViewAdapter(onClickListener)
        mViewModel.getAllRealEstate.observe(viewLifecycleOwner) {
            realEstateList.addAll(it)
            adapter.submitList(it)
        }
        //TODO find a solution to load the good list when user quit details.
        mViewModel.getFilteredRealEstate.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                adapter.submitList(it)
                realEstateList.addAll(it)
            }
        }
        setupRecyclerView(recyclerView)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_item_list_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_real_estate -> {
                val startForResults = Intent(requireContext(), AddRealEstateActivity::class.java)
                getAddActivityResult.launch(startForResults)
            }
            R.id.go_to_map_view -> {
//                startActivity(Intent(requireContext(), MapViewActivity::class.java))
                val mapViewFragmentContainer: View? = binding.root.findViewById(R.id.item_detail_nav_container)
                if (mapViewFragmentContainer != null) {
                    mapViewFragmentContainer.findNavController()
                        .navigate(R.id.sub_graph_fragment_map_view)
                } else {
                    this.findNavController().navigate(R.id.navigate_to_maps_fragment)
                }
            }
            R.id.search_real_estate -> {
                val bottomSheetDialog = BottomSheetFragment()
                bottomSheetDialog.show(requireActivity().supportFragmentManager, bottomSheetDialog.tag)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private val getAddActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            //TODO add notification
        } else {
            Toast.makeText(
                requireContext(),
                requireContext().resources.getString(R.string.main_fragment_error_save_estate),
                Toast.LENGTH_LONG
            ).show()
        }
    }
    class SimpleItemRecyclerViewAdapter(
        private val onClickListener: View.OnClickListener,
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
            }
        }

        inner class ViewHolder(private val binding: RealEstateRowBinding) : RecyclerView.ViewHolder(binding.root) {

            fun bind(realEstate: RealEstate) {
                val numberFormat = NumberFormat.getInstance(Locale.ITALIAN)
                val formatPrice = numberFormat.format(realEstate.price)
                binding.realEstateRowState.text = realEstate.state
                binding.realEstateRowPrice.text = String.format("%s%s", binding.root.resources.getString(R.string.item_list_fragment_currency), formatPrice)
                if (realEstate.sellerName != "") {
                    val sold = binding.root.resources.getString(R.string.sell_fragment_sold)
                    val spannableString = SpannableString(sold)
                    val red = ForegroundColorSpan(Color.RED)
                    spannableString.setSpan(red, 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    val stringToBind = "${realEstate.property} $spannableString"
                    binding.realEstateRowProperty.text = stringToBind
                } else {
                    binding.realEstateRowProperty.text = realEstate.property
                }
                if (realEstate.picture.isNotEmpty()) {
                    Glide.with(binding.root)
                        .load(realEstate.picture[0])
                        .centerCrop()
                        .into(binding.realEstateRowImageView)
                }
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
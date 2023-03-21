package com.openclassrooms.realestatemanager.features_real_estate.presentation.list

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider

import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentListBinding
import com.openclassrooms.realestatemanager.databinding.FragmentListRowBinding
import com.openclassrooms.realestatemanager.features_real_estate.domain.model.RealEstate
import com.openclassrooms.realestatemanager.features_real_estate.presentation.ItemDetailHostActivity
import com.openclassrooms.realestatemanager.features_real_estate.presentation.RealEstateViewModel
import com.openclassrooms.realestatemanager.features_real_estate.presentation.dialog.search.FilterFragment
import com.openclassrooms.realestatemanager.features_real_estate.data.utils.NotificationHelper
import com.openclassrooms.realestatemanager.features_real_estate.data.utils.PlaceholderContent
import com.openclassrooms.realestatemanager.features_real_estate.data.utils.UtilsKt
import com.openclassrooms.realestatemanager.features_real_estate.data.utils.UtilsKt.Companion.ID
import com.openclassrooms.realestatemanager.features_real_estate.data.utils.UtilsKt.Companion.RESULT_CODE
import com.openclassrooms.realestatemanager.features_real_estate.data.utils.UtilsKt.Companion.addDataToPlaceHolder
import com.openclassrooms.realestatemanager.features_real_estate.data.utils.UtilsKt.Companion.getPlaceHolderContent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
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
@AndroidEntryPoint
class ListFragment : Fragment() {

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

    private lateinit var binding: FragmentListBinding
    private val mViewModel: RealEstateViewModel by viewModels()
    private lateinit var adapter: SimpleItemRecyclerViewAdapter
    private val NOTIFICATION_ID = 0



    override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(inflater, container, false)
        configureSupportNavigateUp()
        val resultCode = getPlaceHolderContent(PlaceholderContent.ITEM_MAP, RESULT_CODE)
        sendNotificationForRealEstateCreation(resultCode)
        return binding.root
    }

    private fun configureSupportNavigateUp() {
        requireActivity().addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.fragment_item_list_menu, menu)
                if (UtilsKt.isConnectedToInternet(requireContext())) {
                    requireActivity().title = requireContext().resources.getString(R.string.app_name_offline)
                } else {
                    requireActivity().title = requireContext().resources.getString(R.string.app_name)
                }
                val itemDetailFragmentContainer: View? = view?.findViewById(R.id.item_detail_nav_container)
                if (itemDetailFragmentContainer != null) {
                    val currentSubView: View? =
                        itemDetailFragmentContainer.rootView.findViewById(R.id.item_detail_container) ?: itemDetailFragmentContainer.rootView.findViewById(R.id.fragment_map_view_container)
                    if(currentSubView?.id == R.id.fragment_map_view_container) {
                        menu.findItem(R.id.go_to_detail).isVisible = true
                        menu.findItem(R.id.go_to_map_view).isVisible = false
                    } else {
                        menu.findItem(R.id.go_to_detail).isVisible = false
                        menu.findItem(R.id.go_to_map_view).isVisible = true
                    }
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return onClickItemSelected(menuItem)
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        val title = if (UtilsKt.isConnectedToInternet(requireContext())) {
            requireContext().resources.getString(R.string.app_name)
        } else {
            requireContext().resources.getString(R.string.app_name_offline)
        }
        (activity as ItemDetailHostActivity).supportActionBar?.title = title
        val isTablet = requireContext().resources.getBoolean(R.bool.isTablet)
        if (!isTablet) {
            (activity as ItemDetailHostActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
            (activity as ItemDetailHostActivity).supportActionBar?.setDisplayShowHomeEnabled(false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.addOnUnhandledKeyEventListener(view, unhandledKeyEventListenerCompat)
        val recyclerView = binding.itemList
        var rowView: View? = null
        val onClickListener = View.OnClickListener { itemView ->
            rowView = changeRvItemColors(rowView, itemView)
            val realEstate = itemView.tag as RealEstate
            addDataToPlaceHolder(ID, realEstate.id.toString())
            if (isOnDetailFragment(view) != null) {
                goOnMapOrDetailFragment(realEstate, view)
            } else {
                itemView.findNavController().navigate(R.id.navigate_from_list_to_detail_)
            }
        }
        adapter = SimpleItemRecyclerViewAdapter(onClickListener)
        setupRecyclerView(recyclerView)
        lifecycleScope.launch {
            mViewModel.state.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .distinctUntilChanged()
                .collect { state ->
                    adapter.submitList(state.realEstates)
                }
        }
    }

    private fun changeRvItemColors(previousItemView: View?, currentItemView: View): View {
        if (previousItemView != null) {
            previousItemView.background = ContextCompat.getDrawable(requireContext(), R.drawable.squared_border)
            val priceTv = previousItemView.findViewById<TextView>(R.id.real_estate_row_price)
            priceTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorAccent))
        }
        currentItemView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorAccent))
        val priceTv = currentItemView.findViewById<TextView>(R.id.real_estate_row_price)
        priceTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        return currentItemView
    }

    private fun goOnMapOrDetailFragment(realEstate: RealEstate, view: View) {
        val itemDetailFragmentContainer: View? = view.findViewById(R.id.item_detail_nav_container)
        if (isOnDetailFragment(view) == true) {
            itemDetailFragmentContainer?.findNavController()?.navigate(R.id.sub_graph_fragment_item_detail)
        } else {
            if (realEstate.latitude.isNotEmpty() || realEstate.longitude.isNotEmpty()) {
                itemDetailFragmentContainer?.findNavController()?.navigate(R.id.sub_graph_fragment_map_view)
            } else {
                Toast.makeText(requireContext(), requireContext().resources.getString(R.string.fragment_list_no_real_estate_location), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isOnDetailFragment(view: View): Boolean? {
        val itemDetailFragmentContainer: View? = view.findViewById(R.id.item_detail_nav_container)
        if (itemDetailFragmentContainer != null) {
            val currentSubView: View? =
                itemDetailFragmentContainer.rootView.findViewById(R.id.item_detail_container)
                    ?: itemDetailFragmentContainer.rootView.findViewById(R.id.fragment_map_view_container)
            if (currentSubView != null) {
                return currentSubView.id == R.id.item_detail_container
            }
        }
        return null
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    fun onClickItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_real_estate -> {
                addDataToPlaceHolder(ID, "0")
                this.findNavController().navigate(R.id.navigate_from_list_to_add_information)
            }
            R.id.go_to_map_view -> {
                if (UtilsKt.isConnectedToInternet(requireContext())) {
                    changeFragmentContainer(
                        R.id.item_detail_nav_container,
                        R.id.sub_graph_fragment_map_view,
                        R.id.navigate_from_list_to_maps
                    )
                } else {
                    Toast.makeText(requireContext(), requireContext().resources.getString(R.string.fragment_list_no_connection), Toast.LENGTH_SHORT).show()
                }
            }
            R.id.search_real_estate -> {
                val bottomSheetDialog = FilterFragment(mViewModel)
                bottomSheetDialog.show(requireActivity().supportFragmentManager, bottomSheetDialog.tag)
            }
            R.id.go_to_detail -> {
                changeFragmentContainer(
                    R.id.item_detail_nav_container,
                    R.id.sub_graph_fragment_item_detail,
                    R.id.navigate_from_maps_to_details
                )
            }
        }
        return true
    }

    private fun changeFragmentContainer(currentView: Int, tabletDestination: Int, phoneDestination: Int) {
        val mapViewFragmentContainer: View? = binding.root.findViewById(currentView)
        if (mapViewFragmentContainer != null) {
            mapViewFragmentContainer.findNavController().navigate(tabletDestination)
        } else {
            this.findNavController().navigate(phoneDestination)
        }
    }

    private fun sendNotificationForRealEstateCreation(resultCode: String?) {
        if (resultCode != null) {
            if (resultCode == "RESULT_OK") {
                val notification = NotificationHelper(requireContext())
                notification.getNotificationManager()
                    .notify(NOTIFICATION_ID, notification.createNotification())
            } else {
                Toast.makeText(
                    requireContext(),
                    requireContext().resources.getString(R.string.fragment_list_error_save_estate),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    class SimpleItemRecyclerViewAdapter(
        private val onClickListener: View.OnClickListener,
    ) : ListAdapter<RealEstate, SimpleItemRecyclerViewAdapter.ViewHolder>(RealEstateComparator()) {

        private lateinit var binding: FragmentListRowBinding

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
             binding = FragmentListRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val realEstate = getItem(position)

            holder.bind(realEstate)

            with(holder.itemView) {
                tag = realEstate
                setOnClickListener(onClickListener)
            }
        }

        inner class ViewHolder(private val binding: FragmentListRowBinding) : RecyclerView.ViewHolder(binding.root) {

            fun bind(realEstate: RealEstate) {
                val numberFormat = NumberFormat.getInstance(Locale.ITALIAN)
                val formatPrice = numberFormat.format(realEstate.price)
                binding.realEstateRowPrice.text = String.format("%s%s", binding.root.resources.getString(R.string.item_list_fragment_currency), formatPrice)
                binding.realEstateRowState.text = realEstate.state
                if (realEstate.sellerName != "") {
                    binding.realEstateRowProperty.text = String.format("%s %s",realEstate.property ,binding.root.resources.getString(R.string.fragment_sell_sold))
                } else {
                    binding.realEstateRowProperty.text = realEstate.property
                }
                if (realEstate.picture.isNotEmpty()) {
                    glideImage(binding.root, realEstate.picture, binding.realEstateRowImageView)
                } else {
                    glideImage(binding.root, ContextCompat.getDrawable(binding.root.context, R.drawable.ic_hide_image), binding.realEstateRowImageView)
                }
            }

            private fun glideImage(rootView: View, imagePath: Any?, imageView: AppCompatImageView) {
                Glide.with(rootView)
                    .load(imagePath)
                    .centerCrop()
                    .into(imageView)
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
}
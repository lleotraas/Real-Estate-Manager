package com.openclassrooms.realestatemanager.ui.fragment_filter

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.sqlite.db.SimpleSQLiteQuery
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsMultiChoice
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.RealEstateViewModelFactory
import com.openclassrooms.realestatemanager.databinding.FragmentFilterBinding
import com.openclassrooms.realestatemanager.dependency.RealEstateApplication
import com.openclassrooms.realestatemanager.ui.activity.RealEstateViewModel
import com.openclassrooms.realestatemanager.utils.Utils
import com.openclassrooms.realestatemanager.utils.UtilsKt

class FilterFragment : BottomSheetDialogFragment() {

    private lateinit var mBinding: FragmentFilterBinding
    private val mViewModel: RealEstateViewModel by viewModels {
        RealEstateViewModelFactory(
            (requireActivity().application as RealEstateApplication).realEstateRepository,
            (requireActivity().application as RealEstateApplication).realEstateImageRepository,
            (requireActivity().application as RealEstateApplication).filterRepository)
    }
    private var query: SimpleSQLiteQuery? = null
    private var poiList = ArrayList<String>()
    private var property: String? = null
    private var propertyIndices: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentFilterBinding.inflate(inflater, container, false)
        configureListeners()
        return mBinding.root
    }

    @SuppressLint("SimpleDateFormat", "CheckResult", "SetTextI18n")
    private fun configureListeners() {
        val periodicArray = requireContext().resources.getStringArray(R.array.periodic_filter)
        var periodicDateProgress: Int? = null
        var periodicSellDateProgress: Int? = null
        var differenceDate = 0
        var differenceSellDate = 0
        var numberOfRooms = 0
        var numberOfBathrooms = 0
        var numberOfBedrooms = 0
        var numberOfPhotos = 0

        mBinding.fragmentFilterSeekBarDate.setOnSeekBarChangeListener(object  : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar?, progress: Int, fromUser: Boolean) {
                mBinding.fragmentFilterSeekBarDateTitle.text = String.format("%s %s", requireContext().resources.getString(R.string.fragment_bottom_sheet_date_title), periodicArray[progress])
                periodicDateProgress = progress
            }
            override fun onStartTrackingTouch(seek: SeekBar?) {}
            override fun onStopTrackingTouch(seek: SeekBar?) {
                differenceDate = UtilsKt.getDifference(periodicDateProgress)
            }
        })

        mBinding.fragmentFilterSeekBarSellDate.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar?, progress: Int, fromUser: Boolean) {
                mBinding.fragmentFilterSeekBarSellDateTitle.text = String.format("%s %s" , requireContext().resources.getString(R.string.fragment_bottom_sheet_sell_date_title), periodicArray[progress])
                periodicSellDateProgress = progress
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {
                differenceSellDate = UtilsKt.getDifference(periodicSellDateProgress)
            }
        })

        mBinding.fragmentFilterSeekBarRooms.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seek: SeekBar?, progress: Int, fromUser: Boolean) {
                mBinding.fragmentFilterSeekBarRoomsTitle.text = String.format("%s %s", requireContext().resources.getString(R.string.fragment_bottom_sheet_date_rooms), progress)
                numberOfRooms = progress
            }
            override fun onStartTrackingTouch(seek: SeekBar?) {}
            override fun onStopTrackingTouch(seek: SeekBar?) {}
        })

        mBinding.fragmentFilterSeekBarBathrooms.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar?, progress: Int, fromUser: Boolean) {
                mBinding.fragmentFilterSeekBarBathroomsTitle.text = String.format("%s %s", requireContext().resources.getString(R.string.fragment_bottom_sheet_bathrooms_title), progress)
                numberOfBathrooms = progress
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        mBinding.fragmentFilterSeekBarBedrooms.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar?, progress: Int, fromUser: Boolean) {
                mBinding.fragmentFilterSeekBarBedroomsTitle.text = String.format("%s %s", requireContext().resources.getString(R.string.fragment_bottom_sheet_bedrooms_title), progress)
                numberOfBedrooms = progress
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        mBinding.fragmentFilterSeekBarPhotos.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar?, progress: Int, fromUser: Boolean) {
                mBinding.fragmentFilterSeekBarPhotosTitle.text = String.format("%s %s", requireContext().resources.getString(R.string.fragment_bottom_sheet_photos_title), progress)
                numberOfPhotos = progress
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        mBinding.fragmentFilterPropertyInput.setOnClickListener {
            val alertDialog = MaterialDialog(requireContext())
            alertDialog.positiveButton {
                alertDialog.dismiss()
            }

            alertDialog.show {
                listItemsSingleChoice(R.array.property_array, initialSelection = propertyIndices) {
                        _, index, text ->
                    property = text.toString()
                    mBinding.fragmentFilterPropertyInput.setText(text)
                    propertyIndices = index
                }
            }
        }

        mBinding.fragmentFilterPoiInput.setOnClickListener {
            val alertDialog = MaterialDialog(requireContext())

            alertDialog.positiveButton {
                alertDialog.dismiss()
            }
            alertDialog.cancelOnTouchOutside.and(true)
            alertDialog.show {
                listItemsMultiChoice(R.array.point_of_interest_array) {
                        _, indices, items ->
                    poiList.clear()
                    mBinding.fragmentFilterPoiInput.setText("")
                    for (i in indices.indices) {
                        poiList.add(items[i].toString())
                        Log.i(ContentValues.TAG, "configureListener: " + items[i].toString())
                        mBinding.fragmentFilterPoiInput.setText("${mBinding.fragmentFilterPoiInput.text}${items[i]}, ")
                    }
                }
            }
        }

        mBinding.fragmentFilterSearchBtn.setOnClickListener {
            val minPrice = getMinPrice().ifEmpty { "0" }.toInt()
            val maxPrice = getMaxPrice().ifEmpty { "0" }.toInt()
            val minSurface = getMinSurface().ifEmpty { "0" }.toInt()
            val maxSurface = getMaxSurface().ifEmpty { "0" }.toInt()
            val cityName = getCityName().ifEmpty { "" }.toString()
            val stateName = getStateName().ifEmpty { "" }.toString()
            val property = getProperty().ifEmpty { "" }.toString()
            val currentDay = UtilsKt.parseDate(Utils.getTodayDate()).time
            val creationDateInMillis = UtilsKt.convertDateInDays(currentDay, differenceDate.toLong())
            val sellDateInMillis = UtilsKt.convertDateInDays(currentDay, differenceSellDate.toLong())

            query = UtilsKt.createCustomQuery(
                currentDay,
                numberOfRooms,
                minPrice,
                maxPrice,
                creationDateInMillis,
                sellDateInMillis,
                minSurface,
                maxSurface,
                numberOfBathrooms,
                numberOfBedrooms,
                numberOfPhotos,
                property,
                cityName,
                poiList,
                stateName
            )
            lifecycleScope.launchWhenCreated {
                val filteredList = mViewModel.searchRealEstateWithParameters(query!!)
                if (filteredList.isEmpty()) {
                    Toast.makeText(requireContext(), requireContext().resources.getString(R.string.fragment_bottom_sheet_no_result), Toast.LENGTH_SHORT).show()
                }
                mViewModel.setFilteredList(filteredList)
                dismiss()

            }
        }
    }

    // PRICE
    private fun getMinPrice(): String {
        return mBinding.fragmentFilterMinPrice.text.toString()
    }
    private fun getMaxPrice(): String {
        return mBinding.fragmentFilterMaxPrice.text.toString()
    }

    // SURFACE
    private fun getMinSurface(): String {
        return mBinding.fragmentFilterMinSurface.text.toString()
    }
    private fun getMaxSurface(): String {
        return mBinding.fragmentFilterMaxSurface.text.toString()
    }

    // PROPERTY
    private fun getProperty(): String {
        return mBinding.fragmentFilterPropertyInput.text.toString()
    }

    // CITY
    private fun getCityName(): String {
        return mBinding.fragmentFilterCityInput.text.toString()
    }

    // STATE
    private fun getStateName(): String {
        return mBinding.fragmentFilterStateInput.text.toString()
    }
}
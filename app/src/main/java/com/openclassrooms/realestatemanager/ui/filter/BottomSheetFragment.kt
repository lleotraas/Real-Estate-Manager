package com.openclassrooms.realestatemanager.ui.filter

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
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
import com.openclassrooms.realestatemanager.utils.Utils
import kotlin.math.abs

class BottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var mBinding: FragmentFilterBinding
    private val mViewModel: FilterViewModel by viewModels {
        RealEstateViewModelFactory(
            (requireActivity().application as RealEstateApplication).realEstateRepository,
            (requireActivity().application as RealEstateApplication).realEstateImageRepository,
            (requireActivity().application as RealEstateApplication).filterRepository)
    }
    private var query: SimpleSQLiteQuery? = null
    private var poiList = ArrayList<String>()
    private var poiIndicesArray = intArrayOf()
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

    private fun convertDateInDays(date: Long, multiplier: Long): Long {
        val daysInMillis = (multiplier * 86400000)
        return abs(date - daysInMillis)
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
                mBinding.fragmentFilterSeekBarDateTitle.text = String.format("%s %s", requireContext().resources.getString(R.string.filter_fragment_seek_bar_date_title), periodicArray[progress])
                periodicDateProgress = progress
            }
            override fun onStartTrackingTouch(seek: SeekBar?) {}
            override fun onStopTrackingTouch(seek: SeekBar?) {
                differenceDate = getDifference(periodicDateProgress)
            }
        })

        mBinding.fragmentFilterSeekBarSellDate.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar?, progress: Int, fromUser: Boolean) {
                mBinding.fragmentFilterSeekBarSellDateTitle.text = String.format("%s %s" , requireContext().resources.getString(R.string.filter_fragment_seek_bar_sell_date_title), periodicArray[progress])
                periodicSellDateProgress = progress
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {
                differenceSellDate = getDifference(periodicSellDateProgress)
            }
        })

        mBinding.fragmentFilterSeekBarRooms.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seek: SeekBar?, progress: Int, fromUser: Boolean) {
                mBinding.fragmentFilterSeekBarRoomsTitle.text = String.format("%s %s", requireContext().resources.getString(R.string.filter_fragment_seek_bar_date_rooms), progress)
                numberOfRooms = progress
            }
            override fun onStartTrackingTouch(seek: SeekBar?) {}
            override fun onStopTrackingTouch(seek: SeekBar?) {}
        })

        mBinding.fragmentFilterSeekBarBathrooms.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar?, progress: Int, fromUser: Boolean) {
                mBinding.fragmentFilterSeekBarBathroomsTitle.text = String.format("%s %s", requireContext().resources.getString(R.string.filter_fragment_seek_bar_bathrooms_title), progress)
                numberOfBathrooms = progress
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        mBinding.fragmentFilterSeekBarBedrooms.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar?, progress: Int, fromUser: Boolean) {
                mBinding.fragmentFilterSeekBarBedroomsTitle.text = String.format("%s %s", requireContext().resources.getString(R.string.filter_fragment_seek_bar_bedrooms_title), progress)
                numberOfBedrooms = progress
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        mBinding.fragmentFilterSeekBarPhotos.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar?, progress: Int, fromUser: Boolean) {
                mBinding.fragmentFilterSeekBarPhotosTitle.text = String.format("%s %s", requireContext().resources.getString(R.string.filter_fragment_seek_bar_photos_title), progress)
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
                listItemsMultiChoice(R.array.point_of_interest_array, initialSelection = poiIndicesArray) {
                        _, indices, items ->
                    poiList.clear()
                    mBinding.fragmentFilterPoiInput.setText("")
                    for (i in indices.indices) {
                        poiList.add(items[i].toString())
                        Log.i(ContentValues.TAG, "configureListener: " + items[i].toString())
                        mBinding.fragmentFilterPoiInput.setText("${mBinding.fragmentFilterPoiInput.text}${items[i]}, ")
                    }
                    //TODO create a repository to save data when screen rotate
                    //TODO when deselect all edit text already have a list.
                    poiIndicesArray = indices
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
            val currentDay = Utils.getTodayDate().time
            val creationDateInMillis = convertDateInDays(currentDay, differenceDate.toLong())
            val sellDateInMillis = convertDateInDays(currentDay, differenceSellDate.toLong())

            var queryString = ""
            val args = ArrayList<Any>()
            var containsCondition = false
            val table = "SELECT * FROM real_estate"


            queryString = "$queryString $table"

            if (numberOfRooms != 0) {
                queryString = "$queryString WHERE"
                queryString = "$queryString rooms >= ?"
                args.add(numberOfRooms)
                containsCondition = true
            }

            if (minPrice != 0) {
                if (containsCondition) {
                    queryString = "$queryString AND"
                } else {
                    queryString = "$queryString WHERE"
                    containsCondition = true
                }
                queryString = "$queryString price >= ?"
                args.add(minPrice)
            }

            if (maxPrice != 0) {
                if (containsCondition) {
                    queryString = "$queryString AND"
                } else {
                    queryString = "$queryString WHERE"
                    containsCondition = true
                }
                queryString = "$queryString price <= ?"
                args.add(maxPrice)
            }

            if (creationDateInMillis != currentDay) {
                if (containsCondition) {
                    queryString = "$queryString AND"
                } else {
                    queryString = "$queryString WHERE"
                    containsCondition = true
                }
                queryString = "$queryString creationDate >= ?"
                args.add(creationDateInMillis)
            }

            if (sellDateInMillis != currentDay) {
                if (containsCondition) {
                    queryString = "$queryString AND"
                } else {
                    queryString = "$queryString WHERE"
                    containsCondition = true
                }
                queryString = "$queryString sellDate >= ?"
                args.add(sellDateInMillis)
            }

            if (minSurface != 0) {
                if (containsCondition) {
                    queryString = "$queryString AND"
                } else {
                    queryString = "$queryString WHERE"
                    containsCondition = true
                }
                queryString = "$queryString surface >= ?"
                args.add(minSurface)
            }

            if (maxSurface != 0) {
                if (containsCondition) {
                    queryString = "$queryString AND"
                } else {
                    queryString = "$queryString WHERE"
                    containsCondition = true
                }
                queryString = "$queryString surface <= ?"
                args.add(maxSurface)
            }

            if (numberOfBathrooms != 0) {
                if (containsCondition) {
                    queryString = "$queryString AND"
                } else {
                    queryString = "$queryString WHERE"
                    containsCondition = true
                }
                queryString = "$queryString bathrooms >= ?"
                args.add(numberOfBathrooms)
            }

            if (numberOfBedrooms != 0) {
                if (containsCondition) {
                    queryString = "$queryString AND"
                } else {
                    queryString = "$queryString WHERE"
                    containsCondition = true
                }
                queryString = "$queryString bedrooms >= ?"
                args.add(numberOfBedrooms)
            }

            if (numberOfPhotos != 0) {
                if (containsCondition) {
                    queryString = "$queryString AND"
                } else {
                    queryString = "$queryString WHERE"
                    containsCondition = true
                }
                queryString = "$queryString pictureListSize >= ?"
                args.add(numberOfPhotos)
            }

            if (cityName.isNotEmpty()) {
                if (containsCondition) {
                    queryString = "$queryString AND"
                } else {
                    queryString = "$queryString WHERE"
                    containsCondition = true
                }
                queryString = "$queryString address LIKE ?"
                args.add("%$cityName%")
            }

            if (poiList.isNotEmpty()) {
                for (poi in poiList) {
                    if (containsCondition) {
                        queryString = "$queryString AND"
                    } else {
                        queryString = "$queryString WHERE"
                        containsCondition = true
                    }
                    queryString = "$queryString pointOfInterest LIKE (?)"
                    args.add("%$poi%")
                }
            }

            if (stateName.isNotEmpty()) {
                if (containsCondition) {
                    queryString = "$queryString AND"
                } else {
                    queryString = "$queryString WHERE"
                }
                queryString = "$queryString state LIKE ?"
                args.add(stateName)
            }

            query = SimpleSQLiteQuery(queryString, args.toArray())
            lifecycleScope.launchWhenCreated {
                val filteredList = mViewModel.searchRealEstateWithParameters(query!!)
                mViewModel.setFilteredList(filteredList)
                dismiss()

            }
        }
    }

    private fun getDifference(periodicProgress: Int?): Int {
        return when (periodicProgress) {
            // DAYS
            in 1..6 -> periodicProgress!!
            //WEEKS
            in 7..9 -> (periodicProgress!!.minus(6)).times(7)
            //MONTHS
            in 10..20 -> (periodicProgress!!.minus(9)).times(30)
            //YEARS
            in 21..23 -> (periodicProgress!!.minus(20)).times(365)
            else -> 0
            //TODO need to search for today too
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

    // CITY
    private fun getCityName(): String {
        return mBinding.fragmentFilterCityInput.text.toString()
    }

    // STATE
    private fun getStateName(): String {
        return mBinding.fragmentFilterStateInput.text.toString()
    }
}
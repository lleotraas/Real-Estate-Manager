package com.openclassrooms.realestatemanager.ui.filter

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.RealEstateViewModelFactory
import com.openclassrooms.realestatemanager.databinding.FragmentFilterBinding
import com.openclassrooms.realestatemanager.dependency.RealEstateApplication
import com.openclassrooms.realestatemanager.model.RealEstate
import com.openclassrooms.realestatemanager.utils.Utils
import java.text.Normalizer
import java.text.SimpleDateFormat
import java.util.*

class FilterFragment : Fragment() {

    private var _binding: FragmentFilterBinding? = null
    private val mBinding get() = _binding!!
    private val mViewModel: FilterViewModel by viewModels {
        RealEstateViewModelFactory(
            (requireActivity().application as RealEstateApplication).realEstateRepository,
            (requireActivity().application as RealEstateApplication).realEstateImageRepository)
    }
    private lateinit var filteredList: List<RealEstate>
    private lateinit var listOfRealEstate: List<RealEstate>
    private val REGEX_UNACCENT = "\\p{InCombiningDiacriticalMarks}+".toRegex()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilterBinding.inflate(inflater, container, false)
        mViewModel.getAllRealEstate.observe(viewLifecycleOwner) { realEstates ->
            listOfRealEstate = realEstates
        }
        configureListeners()
        return mBinding.root
    }

    @SuppressLint("SimpleDateFormat")
    private fun configureListeners() {
        val periodicArray = requireContext().resources.getStringArray(R.array.periodic_filter)
        var periodicProgress: Int? = null
        var difference = 0
        var numberOfRooms = 1
        var numberOfBathrooms = 1
        var numberOfBedrooms = 1
        var numberOfPhotos = 1
        mBinding.fragmentFilterSeekBarDate.setOnSeekBarChangeListener(object  : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.i(TAG, "onProgressChanged: progress = $progress, fromUser = $fromUser")
                mBinding.fragmentFilterSeekBarDateTitle.text = String.format("%s %s", requireContext().resources.getString(R.string.filter_fragment_seek_bar_title), periodicArray[progress]
                )
                periodicProgress = progress
            }

            override fun onStartTrackingTouch(seek: SeekBar?) {
                Log.i(TAG, "onStartTrackingTouch: seek = ${seek.toString()}")
            }

            override fun onStopTrackingTouch(seek: SeekBar?) {
                difference = when (periodicProgress) {

                    // DAYS
                    in 1..6 -> periodicProgress!!
                    //WEEKS
                    in 7..9 -> (periodicProgress!!.minus(6)).times(7)
                    //MONTHS
                    in 10..20 -> (periodicProgress!!.minus(9)).times(30)
                    //YEARS
                    in 21..23 -> (periodicProgress!!.minus(20)).times(365)
                    else -> 0
                }
            }
        })

        mBinding.fragmentFilterSeekBarRooms.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seek: SeekBar?, progress: Int, fromUser: Boolean) {
                mBinding.fragmentFilterSeekBarRoomsTitle.text = String.format("%s %s", requireContext().resources.getString(R.string.filter_fragment_seek_bar_date_rooms), progress + 1)
                numberOfRooms = progress +1
            }
            override fun onStartTrackingTouch(seek: SeekBar?) {}
            override fun onStopTrackingTouch(seek: SeekBar?) {}
        })

        mBinding.fragmentFilterSeekBarBathrooms.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar?, progress: Int, fromUser: Boolean) {
                mBinding.fragmentFilterSeekBarBathroomsTitle.text = String.format("%s %s", requireContext().resources.getString(R.string.filter_fragment_seek_bar_bathrooms_title), progress + 1)
                numberOfBathrooms = progress +1
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
            })

        mBinding.fragmentFilterSeekBarBedrooms.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar?, progress: Int, fromUser: Boolean) {
                mBinding.fragmentFilterSeekBarBedroomsTitle.text = String.format("%s %s", requireContext().resources.getString(R.string.filter_fragment_seek_bar_bedrooms_title), progress + 1)
                numberOfBedrooms = progress +1
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        mBinding.fragmentFilterSeekBarPhotos.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar?, progress: Int, fromUser: Boolean) {
                mBinding.fragmentFilterSeekBarPhotosTitle.text = String.format("%s %s", requireContext().resources.getString(R.string.filter_fragment_seek_bar_photos_title), progress + 1)
                numberOfPhotos = progress
                //TODO put number of photoList size in RealEstate
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        mBinding.fragmentFilterSearchBtn.setOnClickListener {
            val minPrice = getMinPrice().ifEmpty { "0" }
            val maxPrice = getMaxPrice().ifEmpty { "99999999" }
            val minSurface = getMinSurface().ifEmpty { "0" }
            val maxSurface = getMaxSurface().ifEmpty { "99999" }
            val cityName = getCityName().ifEmpty { "City" }
            val stateName = getStateName().ifEmpty { "State" }
            val dateFormat = SimpleDateFormat("dd/MM/yyyy")
            val currentDay = Utils.getTodayDate()
            val currentDayInDays = (dateFormat.parse(currentDay).time / 86400000 + 7).toInt()
            var dateToFilter = 0
            if (difference > 0) {
            dateToFilter = currentDayInDays - difference
            }
            filteredList = listOfRealEstate.filter {
                        it.price.toInt() in minPrice.toInt()..maxPrice.toInt() &&
                        it.creationDateInDays.toInt() >= dateToFilter &&
                        it.rooms.toInt() >= numberOfRooms &&
                        it.bathrooms.toInt() >= numberOfBathrooms &&
                        it.bedrooms.toInt() >= numberOfBedrooms &&
                        it.surface.toInt() in minSurface.toInt()..maxSurface.toInt() &&
                        retrieveCityName(it.address).lowercase().unaccent() == formatName(isCityNameEmpty(retrieveCityName(it.address), cityName)) &&
                        formatName(it.state) == formatName(isStateNameEmpty(it.state, stateName))
            }
            val listOfId = ArrayList<String>()
            filteredList.forEach {
                listOfId.add(it.id.toString())
            }
            val replyIntent = Intent()
            replyIntent.putStringArrayListExtra("list_of_id", listOfId)
            requireActivity().setResult(RESULT_OK, replyIntent)
            requireActivity().finish()
        }
    }

    private fun retrieveCityName(address: String): String {
        return address.substringBeforeLast(",").substringAfterLast(",")
    }

    private fun isCityNameEmpty(it: String, cityName: String): String {
        Log.i(TAG, "isCityNameEmpty: CITY NAME = " + if (cityName == "City") it.unaccent() else " ${cityName.unaccent()}")
        return if (cityName == "City") it.unaccent() else " ${cityName.unaccent()}"
    }

    private fun isStateNameEmpty(it: String, stateName: String): String {
        Log.i(TAG, "isStateNameEmpty: WHICH STATE = " + if (stateName == "State") it.unaccent() else stateName.unaccent())
        return if (stateName == "State") it.unaccent() else stateName.unaccent()
    }

    private fun formatName(name: String): String {
        Log.i(TAG, "formatName: CAPITALIZE = " + name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() })
        return name.lowercase()
    }

    fun CharSequence.unaccent(): String {
        val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
        return REGEX_UNACCENT.replace(temp, "")
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

        override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.openclassrooms.realestatemanager.ui.filter

import android.app.Activity.RESULT_OK
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
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
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

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

    private fun configureListeners() {
        val periodicArray = requireContext().resources.getStringArray(R.array.periodic_filter)
        var periodicProgress: Int? = null
        var difference = 0
        mBinding.filterFragmentSeekBar.setOnSeekBarChangeListener(object  : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.i(TAG, "onProgressChanged: progress = $progress, fromUser = $fromUser")
                mBinding.filterFragmentSeekBarTitle.text = String.format("%s %s", requireContext().resources.getString(R.string.filter_fragment_seek_bar_title), periodicArray[progress]
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

        mBinding.filterFragmentSearchBtn.setOnClickListener {
            val minPrice = getMinPrice().ifEmpty { "0" }
            val maxPrice = getMaxPrice().ifEmpty { "99999999" }
            val dateFormat = SimpleDateFormat("dd/MM/yyyy")
            val currentDay = Utils.getTodayDate()
            val currentDayInDays = (dateFormat.parse(currentDay).time / 86400000 + 7).toInt()

//            if (difference > 0) {
            val dateToFilter = currentDayInDays - difference
//            }
            filteredList = listOfRealEstate.filter {
                        it.price.toInt() in minPrice.toInt()..maxPrice.toInt() &&
                        it.creationDateInDays.toInt() >= dateToFilter
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

    private fun getMinPrice(): String {
        return mBinding.filterFragmentMinPrice.text.toString()
    }

    private fun getMaxPrice(): String {
        return mBinding.filterFragmentMaxPrice.text.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
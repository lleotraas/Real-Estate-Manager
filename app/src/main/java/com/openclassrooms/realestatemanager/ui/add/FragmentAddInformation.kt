package com.openclassrooms.realestatemanager.ui.add

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsMultiChoice
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.openclassrooms.realestatemanager.BuildConfig
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.RealEstateViewModelFactory
import com.openclassrooms.realestatemanager.databinding.FragmentAddInformationBinding
import com.openclassrooms.realestatemanager.dependency.RealEstateApplication
import com.openclassrooms.realestatemanager.model.RealEstate
import com.openclassrooms.realestatemanager.model.details.Location
import com.openclassrooms.realestatemanager.retrofit.RetrofitInstance
import com.openclassrooms.realestatemanager.utils.Utils
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException


class FragmentAddInformation : Fragment() {

    private var _binding: FragmentAddInformationBinding? = null
    private val mBinding get() = _binding!!
    private val mViewModel: AddViewModel by viewModels {
        RealEstateViewModelFactory(
            (requireActivity().application as RealEstateApplication).realEstateRepository,
            (requireActivity().application as RealEstateApplication).realEstateImageRepository,
            (requireActivity().application as RealEstateApplication).filterRepository)
    }

    private lateinit var location: Location
    private var latitude: String? = null
    private var longitude: String? = null
    private var poiList = ArrayList<String>()
    private var poiIndicesArray = intArrayOf()
    private lateinit var addInformationAdapter: AddInformationAdapter
    private var property: String? = null
    private var propertyIndices: Int = 0
    private var realEstateId: Long? = null
    private var listOfPhoto = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddInformationBinding.inflate(inflater, container, false)
        addInformationAdapter = AddInformationAdapter()
        val intentReceiver = requireActivity().intent
        if (savedInstanceState != null) {
            property = savedInstanceState.getString(BUNDLE_STATE_PROPERTY_TEXT)
            propertyIndices = savedInstanceState.getInt(BUNDLE_STATE_PROPERTY_INDICES)
            poiList = savedInstanceState.getStringArrayList(BUNDLE_STATE_POI_LIST) as ArrayList<String>
            poiIndicesArray = savedInstanceState.getIntArray(BUNDLE_STATE_POI_INDICES) ?: poiIndicesArray
            latitude = savedInstanceState.getString(BUNDLE_STATE_LOCATION_LATITUDE)
            longitude = savedInstanceState.getString(BUNDLE_STATE_LOCATION_LONGITUDE)
            loadPOIIntoRecyclerView()
            setupPOIRecyclerView()
        }
        this.configureListener()
        realEstateId = intentReceiver.getLongExtra("id", 0)
        if (realEstateId!! > 0) {
            this.loadInformation()
        }
        return mBinding.root
    }

    private fun loadInformation() {
        mViewModel.getRealEstateById(realEstateId!!).observe(viewLifecycleOwner) {
            mBinding.fragmentAddInformationProperty.setText(it.property)
            mBinding.fragmentAddInformationPrice.setText(it.price.toString())
            mBinding.fragmentAddInformationSurface.setText(it.surface.toString())
            mBinding.fragmentAddInformationRooms.setText(it.rooms.toString())
            mBinding.fragmentAddInformationBathrooms.setText(it.bathrooms.toString())
            mBinding.fragmentAddInformationBedrooms.setText(it.bedrooms.toString())
            mBinding.fragmentAddInformationDescription.setText(it.description)
            mBinding.fragmentAddInformationAddress.setText(it.address)
            poiList = it.pointOfInterest
            mBinding.fragmentAddInformationState.setText(it.state)
            mBinding.fragmentAddInformationImageBtn.visibility = View.GONE
            mBinding.fragmentAddInformationUpdateAndGoImageBtn.visibility = View.VISIBLE
            mBinding.fragmentAddInformationUpdateAndQuitBtn.visibility = View.VISIBLE
            property = it.property
            latitude = it.latitude
            longitude = it.longitude
            poiList = it.pointOfInterest
            listOfPhoto = it.picture
            var poiString = ""
            for (poi in poiList) {
                poiString = "$poiString$poi, "
                mBinding.fragmentAddInformationPointOfInterestInput.setText(poiString)
            }
        }
    }

    private fun setupPOIRecyclerView() = mBinding.fragmentAddInformationPointOfInterestRv.apply {
        adapter = addInformationAdapter
        layoutManager = StaggeredGridLayoutManager(5, LinearLayoutManager.VERTICAL)
    }

    private fun loadPOIIntoRecyclerView() {
        addInformationAdapter.submitList(poiList)
        mBinding.fragmentAddInformationPointOfInterestRv.adapter = addInformationAdapter
    }

    @SuppressLint("CheckResult")
    private fun configureListener() {
        mBinding.fragmentAddInformationImageBtn.setOnClickListener{
            goToFragmentAddImage()
        }

        mBinding.fragmentAddInformationUpdateAndQuitBtn.setOnClickListener {
            mViewModel.update(createRealEstate(realEstateId))
            requireActivity().finish()
        }

        mBinding.fragmentAddInformationUpdateAndGoImageBtn.setOnClickListener {
            val realEstate = createRealEstate(realEstateId)
            mViewModel.update(realEstate)
            goToFragmentAddImage()
        }

        mBinding.fragmentAddInformationAddress.afterTextChanged { inputText ->
            lifecycleScope.launchWhenCreated {
                val response = try {
                    RetrofitInstance.autocompleteApi.getPlacesAutocomplete(
                        inputText,
                        "fr",
                        "address",
                        BuildConfig.GMP_KEY
                    )
                } catch (exception: IOException) {
                    Log.e(TAG, "IOException, you might have internet connection" + exception.message)
                    return@launchWhenCreated
                } catch (exception: HttpException) {
                    Log.e(TAG, "HttpException, unexpected response" + exception.message)
                    return@launchWhenCreated
                }
                if (response.isSuccessful && response.body() != null) {
                    val placeAddress = ArrayList<String>()
                    for (place in response.body()!!.predictions) {
                        placeAddress.add(place.description)
                        val adapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_list_item_1,
                            placeAddress
                        )
                        mBinding.fragmentAddInformationAddress.setAdapter(adapter)
                    }
                }
                mBinding.fragmentAddInformationAddress.setOnItemClickListener { adapterView, _, i, _ ->
                    val item = adapterView.getItemAtPosition(i)
                    for (itemPredicted in response.body()!!.predictions) {
                        if (item.toString() == itemPredicted.description) {
                            getPlaceDetails(itemPredicted.place_id)
                        }
                    }
                }
            }
        }

        mBinding.fragmentAddInformationProperty.setOnClickListener {
            val alertDialog = MaterialDialog(requireContext())
            alertDialog.positiveButton {
                alertDialog.dismiss()
            }
            alertDialog.show {
                listItemsSingleChoice(R.array.property_array, initialSelection = propertyIndices) {
                        _, index, text ->
                    property = text.toString()
                    mBinding.fragmentAddInformationProperty.setText(text)
                    propertyIndices = index
                }
            }
        }

        mBinding.fragmentAddInformationPointOfInterestInput.setOnClickListener {

            val alertDialog = MaterialDialog(requireContext())
            alertDialog.positiveButton {
                alertDialog.dismiss()
            }
            alertDialog.show {
                listItemsMultiChoice(R.array.point_of_interest_array, initialSelection = poiIndicesArray) {
                    dialog, indices, items ->
                    poiList.clear()
                    for (i in indices.indices) {
                        poiList.add(items[i].toString())
                        Log.i(TAG, "configureListener: " + items[i].toString())
                    }
                    poiIndicesArray = indices
                    var poiString = ""
                    for (poi in poiList) {
                        poiString = "$poiString$poi, "
                        mBinding.fragmentAddInformationPointOfInterestInput.setText(poiString)
                    }
//                    loadPOIIntoRecyclerView()
//                    setupPOIRecyclerView()
                }
            }
        }
    }

    private fun goToFragmentAddImage() {
        val addImageFragment = FragmentAddImage()
        if  (realEstateId ?: 0 > 0) {
            val bundle = Bundle()
            bundle.putLong("id", realEstateId!!)
            addImageFragment.arguments = bundle
        } else {
            addImageFragment.arguments = this.getInformation()
        }
        val fragmentManager = requireActivity().supportFragmentManager
        fragmentManager.commit{
            setReorderingAllowed(true)
            replace(R.id.activity_add_real_estate_container, addImageFragment)
            //TODO add animation
        }
    }

    private fun getPlaceDetails(placeId: String) {
        lifecycleScope.launchWhenCreated {
            val response = try {
                RetrofitInstance.placeDetailsApi.getPlaceDetails(placeId, BuildConfig.GMP_KEY)
            } catch (exception: IOException) {
                Log.e(TAG, "IOException, you might have internet connection" + exception.message)
                return@launchWhenCreated
            } catch (exception: HttpException) {
                Log.e(TAG, "HttpException, unexpected response" + exception.message)
                return@launchWhenCreated
            }

            if (response.isSuccessful && response.body() != null) {
                location = response.body()!!.result.geometry.location
                latitude = location.lat.toString()
                longitude = location.lng.toString()
            }
        }
    }

    private fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                afterTextChanged.invoke(editableText.toString())
            }
        })
    }

    private fun getInformation(): Bundle{
        val replyIntent = Intent()
        val bundle = Bundle()
        return if (TextUtils.isEmpty(mBinding.fragmentAddInformationAddress.text)) {
            requireActivity().setResult(Activity.RESULT_CANCELED, replyIntent)
            bundle
        } else {
            val realEstate = createRealEstate(0)
            lifecycleScope.launch {
                mViewModel.insert(realEstate)
            }
            bundle.putString("address", realEstate.address)
            bundle
        }
    }

    private fun createRealEstate(realEstateId: Long?): RealEstate {

        val price = mBinding.fragmentAddInformationPrice.text.toString().toInt()
        val surface = mBinding.fragmentAddInformationSurface.text.toString().toInt()
        val rooms = mBinding.fragmentAddInformationRooms.text.toString().toInt()
        val bathrooms = mBinding.fragmentAddInformationBathrooms.text.toString().toInt()
        val bedrooms = mBinding.fragmentAddInformationBedrooms.text.toString().toInt()
        val description = mBinding.fragmentAddInformationDescription.text.toString()
        val address = mBinding.fragmentAddInformationAddress.text.toString()
        val state = mBinding.fragmentAddInformationState.text.toString()
        val creationDate = Utils.getTodayDate()

       return RealEstate(
            realEstateId!!,
            property!!,
            price,
            surface,
            rooms,
            bathrooms,
            bedrooms,
            description,
            listOfPhoto,
            listOfPhoto.size,
            address,
            latitude!!,
            longitude!!,
            poiList,
            state,
            creationDate,
            "",
            ""
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(BUNDLE_STATE_PROPERTY_TEXT, property)
        outState.putInt(BUNDLE_STATE_PROPERTY_INDICES, propertyIndices)
        outState.putStringArrayList(BUNDLE_STATE_POI_LIST, poiList)
        outState.putIntArray(BUNDLE_STATE_POI_INDICES, poiIndicesArray)
        outState.putString(BUNDLE_STATE_LOCATION_LATITUDE, latitude)
        outState.putString(BUNDLE_STATE_LOCATION_LONGITUDE, longitude)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val BUNDLE_STATE_PROPERTY_TEXT = "bundle_state_property_text"
        const val BUNDLE_STATE_PROPERTY_INDICES = "bundle_state_property_indices"
        const val BUNDLE_STATE_POI_LIST = "bundle_state_poi_list"
        const val BUNDLE_STATE_POI_INDICES = "bundle_state_poi_indices"
        const val BUNDLE_STATE_LOCATION_LATITUDE = "bundle_state_location_latitude"
        const val BUNDLE_STATE_LOCATION_LONGITUDE = "bundle_state_location_longitude"
    }

}
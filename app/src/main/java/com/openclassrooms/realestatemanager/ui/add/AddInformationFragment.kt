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
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
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
import com.openclassrooms.realestatemanager.ui.activity.AddViewModel
import com.openclassrooms.realestatemanager.utils.Utils
import com.openclassrooms.realestatemanager.utils.UtilsKt
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.util.*


class AddInformationFragment : Fragment() {

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
    private var creationDate: Date? = null
    private var property: String? = null
    private var propertyIndices: Int = 0
    private var realEstateId: Long? = null
    private var photo: String? = null
    private var photoListSize = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddInformationBinding.inflate(inflater, container, false)
        val intentReceiver = requireActivity().intent
        if (savedInstanceState != null) {
            property = savedInstanceState.getString(BUNDLE_STATE_PROPERTY_TEXT)
            propertyIndices = savedInstanceState.getInt(BUNDLE_STATE_PROPERTY_INDICES)
            poiList = savedInstanceState.getStringArrayList(BUNDLE_STATE_POI_LIST) as ArrayList<String>
            poiIndicesArray = savedInstanceState.getIntArray(BUNDLE_STATE_POI_INDICES) ?: poiIndicesArray
            latitude = savedInstanceState.getString(BUNDLE_STATE_LOCATION_LATITUDE)
            longitude = savedInstanceState.getString(BUNDLE_STATE_LOCATION_LONGITUDE)
        }
        mBinding.fragmentAddInformationImageBtn.isEnabled = false
        this.configureListener()
        this.configureTextWatchers()
//        val toolbar = mBinding.fragmentAddInformationToolbar
//        toolbar.navigationIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_back)
        setHasOptionsMenu(true)
        realEstateId = intentReceiver.getLongExtra("id", 0)
        if (realEstateId!! > 0) {
            this.getCurrentRealEstate()
        }
        return mBinding.root
    }

    private fun getCurrentRealEstate() {
        mViewModel.getRealEstateById(realEstateId!!).observe(viewLifecycleOwner) { currentRealEstate ->
            bindRealEstateToUpdateDetails(currentRealEstate)
            loadInformation(currentRealEstate)
            manageButtonForUpdate()
        }
    }

    private fun loadInformation(currentRealEstate: RealEstate) {
        poiList = currentRealEstate.pointOfInterest
        property = currentRealEstate.property
        latitude = currentRealEstate.latitude
        longitude = currentRealEstate.longitude
        poiList = currentRealEstate.pointOfInterest
        photo = currentRealEstate.picture
        photoListSize = if (currentRealEstate.pictureListSize > 0) currentRealEstate.pictureListSize else 0
        creationDate = currentRealEstate.creationDate
        var poiString = ""
        for (poi in poiList) {
            poiString = "$poiString$poi, "
            mBinding.fragmentAddInformationPointOfInterestInput.setText(poiString)
        }
    }

    private fun bindRealEstateToUpdateDetails(currentRealEstate: RealEstate) {
        mBinding.fragmentAddInformationProperty.setText(currentRealEstate.property)
        mBinding.fragmentAddInformationPrice.setText(currentRealEstate.price.toString())
        mBinding.fragmentAddInformationSurface.setText(currentRealEstate.surface.toString())
        mBinding.fragmentAddInformationRooms.setText(currentRealEstate.rooms.toString())
        mBinding.fragmentAddInformationBathrooms.setText(currentRealEstate.bathrooms.toString())
        mBinding.fragmentAddInformationBedrooms.setText(currentRealEstate.bedrooms.toString())
        mBinding.fragmentAddInformationDescription.setText(currentRealEstate.description)
        mBinding.fragmentAddInformationAddress.setText(currentRealEstate.address)
        mBinding.fragmentAddInformationState.setText(currentRealEstate.state)
    }

    private fun manageButtonForUpdate() {
        mBinding.fragmentAddInformationImageBtn.visibility = View.GONE
        mBinding.fragmentAddInformationUpdateAndGoImageBtn.visibility = View.VISIBLE
        mBinding.fragmentAddInformationUpdateAndQuitBtn.visibility = View.VISIBLE
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
                    Toast.makeText(requireContext(), requireContext().resources.getString(R.string.add_information_fragment_no_suggestion), Toast.LENGTH_SHORT).show()
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
                        enableCreateBtn()
                        mBinding.fragmentAddInformationAddress.setAdapter(adapter)
                    }
                }
                mBinding.fragmentAddInformationAddress.setOnItemClickListener { adapterView, _, i, _ ->
                    val item = adapterView.getItemAtPosition(i)
                    for (itemPredicted in response.body()!!.predictions) {
                        if (item.toString() == itemPredicted.description) {
                            if (UtilsKt.isConnectedToInternet(requireContext())) {
                                getPlaceDetails(itemPredicted.place_id)
                            }
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
                    enableCreateBtn()
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
                        _, indices, items ->
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
                }
            }
        }
    }

    private fun configureTextWatchers() {
        mBinding.fragmentAddInformationState.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                enableCreateBtn()
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
        })
        mBinding.fragmentAddInformationPrice.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                enableCreateBtn()
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    private fun goToFragmentAddImage() {
        val addImageFragment = AddImageFragment()
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
        val surface = if(mBinding.fragmentAddInformationSurface.text.toString().isEmpty()) 0 else mBinding.fragmentAddInformationSurface.text.toString().toInt()
        val rooms = if(mBinding.fragmentAddInformationRooms.text.toString().isEmpty()) 0 else mBinding.fragmentAddInformationRooms.text.toString().toInt()
        val bathrooms = if(mBinding.fragmentAddInformationBathrooms.text.toString().isEmpty()) 0 else mBinding.fragmentAddInformationBathrooms.text.toString().toInt()
        val bedrooms = if(mBinding.fragmentAddInformationBedrooms.text.toString().isEmpty()) 0 else mBinding.fragmentAddInformationBedrooms.text.toString().toInt()
        val description = mBinding.fragmentAddInformationDescription.text.toString()
        val address = mBinding.fragmentAddInformationAddress.text.toString()
        val state = mBinding.fragmentAddInformationState.text.toString()
        val creationDate = Utils.getTodayDate()
        val sellDate = Date(0)

       return RealEstate(
            realEstateId!!,
            property!!,
            price,
            surface,
            rooms,
            bathrooms,
            bedrooms,
            description,
            photo ?: "",
            photoListSize,
            address,
            latitude ?: "",
            longitude ?: "",
            poiList,
            state,
            this.creationDate ?: creationDate,
            sellDate,
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

    private fun enableCreateBtn() {
        mBinding.fragmentAddInformationImageBtn.isEnabled =
                    mBinding.fragmentAddInformationProperty.text!!.isNotEmpty() &&
                    mBinding.fragmentAddInformationAddress.text!!.isNotEmpty() &&
                    mBinding.fragmentAddInformationPrice.text!!.isNotEmpty() &&
                    mBinding.fragmentAddInformationState.text!!.isNotEmpty()
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
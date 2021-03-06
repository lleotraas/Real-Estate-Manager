package com.openclassrooms.realestatemanager.ui.fragment_add

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.*
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
import com.openclassrooms.realestatemanager.ui.activity.AddRealEstateActivity
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
    private var pointOfInterest: String? = null
    private var poiIndicesArray = intArrayOf()
    private var creationDate: Date? = null
    private var property: String? = null
    private var propertyIndices: Int = 0
    private var address: String? = null
    private var realEstateId: Long? = null
    private var photo: String? = null
    private var photoListSize = 0
    private var addPhotoBtn: MenuItem? = null

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
            pointOfInterest = savedInstanceState.getString(BUNDLE_STATE_POI)
            poiIndicesArray = savedInstanceState.getIntArray(BUNDLE_STATE_POI_INDICES) ?: poiIndicesArray
            latitude = savedInstanceState.getString(BUNDLE_STATE_LOCATION_LATITUDE)
            longitude = savedInstanceState.getString(BUNDLE_STATE_LOCATION_LONGITUDE)
            address = savedInstanceState.getString(BUNDLE_STATE_ADDRESS)
        }
        this.configureListener()
        this.configureSupportNavigateUp()
        this.configureTextWatchers()
        realEstateId = intentReceiver.getLongExtra("id", 0)
        if (realEstateId!! > 0) {
            this.getCurrentRealEstate()
        }
        return mBinding.root
    }

    private fun configureSupportNavigateUp() {
        setHasOptionsMenu(true)
        (activity as AddRealEstateActivity).supportActionBar?.title = requireContext().resources.getString(R.string.fragment_add_information_toolbar_title)
    }

    private fun getCurrentRealEstate() {
        mViewModel.getRealEstateById(realEstateId!!).observe(viewLifecycleOwner) { currentRealEstate ->
            bindRealEstateToUpdateDetails(currentRealEstate)
            loadInformation(currentRealEstate)
        }
    }

    private fun loadInformation(currentRealEstate: RealEstate) {
        property = currentRealEstate.property
        latitude = currentRealEstate.latitude
        longitude = currentRealEstate.longitude
        pointOfInterest = currentRealEstate.pointOfInterest
        photo = currentRealEstate.picture
        photoListSize = if (currentRealEstate.pictureListSize > 0) currentRealEstate.pictureListSize else 0
        creationDate = currentRealEstate.creationDate
        address = currentRealEstate.address
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
        mBinding.fragmentAddInformationPointOfInterestInput.setText(currentRealEstate.pointOfInterest)
    }

    @SuppressLint("CheckResult")
    private fun configureListener() {
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
                    Toast.makeText(requireContext(), requireContext().resources.getString(R.string.fragment_add_information_no_suggestion), Toast.LENGTH_SHORT).show()
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
                        enableCreateBtn()
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
                    pointOfInterest = ""
                    for (i in indices.indices) {
                        pointOfInterest = "$pointOfInterest".replace(".", "")
                        pointOfInterest = if (pointOfInterest!!.isEmpty()) {
                            "${items[i]}."
                        } else {
                            "$pointOfInterest, ${items[i]}."
                        }
                    }
                    poiIndicesArray = indices
                    mBinding.fragmentAddInformationPointOfInterestInput.setText(pointOfInterest)
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
        val creationDate = UtilsKt.parseDate(Utils.getTodayDate())
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
            pointOfInterest ?: "",
            state,
            this.creationDate ?: creationDate,
            sellDate,
            ""
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_add_information_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
        addPhotoBtn = menu.findItem(R.id.menu_add_photo)
        addPhotoBtn!!.isEnabled = false
        enableCreateBtn()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_add_photo) {
            if (realEstateId != null) {
                val realEstate = createRealEstate(realEstateId)
                mViewModel.update(realEstate)
            }
                goToFragmentAddImage()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(BUNDLE_STATE_PROPERTY_TEXT, property)
        outState.putInt(BUNDLE_STATE_PROPERTY_INDICES, propertyIndices)
        outState.putString(BUNDLE_STATE_POI, pointOfInterest)
        outState.putIntArray(BUNDLE_STATE_POI_INDICES, poiIndicesArray)
        outState.putString(BUNDLE_STATE_LOCATION_LATITUDE, latitude)
        outState.putString(BUNDLE_STATE_LOCATION_LONGITUDE, longitude)
    }

    private fun enableCreateBtn() {
        addPhotoBtn?.isEnabled =
                    mBinding.fragmentAddInformationProperty.text!!.isNotEmpty() &&
                    mBinding.fragmentAddInformationAddress.text?.isNotEmpty() ?: address!!.isNotEmpty() &&
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
        const val BUNDLE_STATE_POI = "bundle_state_poi"
        const val BUNDLE_STATE_POI_INDICES = "bundle_state_poi_indices"
        const val BUNDLE_STATE_LOCATION_LATITUDE = "bundle_state_location_latitude"
        const val BUNDLE_STATE_LOCATION_LONGITUDE = "bundle_state_location_longitude"
        const val BUNDLE_STATE_ADDRESS = "bundle_state_address"
    }

}
package com.openclassrooms.realestatemanager.ui.add

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
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
import androidx.fragment.app.*
import androidx.lifecycle.lifecycleScope
import com.openclassrooms.realestatemanager.BuildConfig
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentAddInformationBinding
import com.openclassrooms.realestatemanager.model.details.Location
import com.openclassrooms.realestatemanager.retrofit.RetrofitInstance
import com.openclassrooms.realestatemanager.utils.Utils
import retrofit2.HttpException
import java.io.ByteArrayOutputStream
import java.io.IOException

class FragmentAddInformation : Fragment() {

    private var _binding: FragmentAddInformationBinding? = null
    private val mBinding get() = _binding!!
    private lateinit var location: Location


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddInformationBinding.inflate(inflater, container, false)
        val view = mBinding.root
        this.configureListener()
        return view
    }

    private fun configureListener() {
        mBinding.fragmentAddInformationImageBtn.setOnClickListener{
            val addImageFragment = FragmentAddImage()
            addImageFragment.arguments = this.getInformation()
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.commit{
                setReorderingAllowed(true)
//                add(0,addImageFragment, "fragment")
                replace(R.id.activity_add_real_estate_container, addImageFragment)
//                replace<FragmentAddRealEstateImage>(R.id.activity_add_real_estate_container)
                //TODO add animation
            }
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
                mBinding.fragmentAddInformationAddress.setOnItemClickListener { adapterView, view, i, l ->
                    val item = adapterView.getItemAtPosition(i)
                    for (itemPredicted in response.body()!!.predictions) {
                        if (item.toString() == itemPredicted.description) {
                            getPlaceDetails(itemPredicted.place_id)
                        }
                    }
                }
            }
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
        if (TextUtils.isEmpty(mBinding.fragmentAddInformationAddress.text)) {
            requireActivity().setResult(Activity.RESULT_CANCELED, replyIntent)
            return bundle
        } else {
            val property = mBinding.fragmentAddInformationProperty.text.toString()
            val price = mBinding.fragmentAddInformationPrice.text.toString()
            val surface = mBinding.fragmentAddInformationSurface.text.toString()
            val rooms = mBinding.fragmentAddInformationRooms.text.toString()
            val bathrooms = mBinding.fragmentAddInformationBathrooms.text.toString()
            val bedrooms = mBinding.fragmentAddInformationBedrooms.text.toString()
            val description = mBinding.fragmentAddInformationDescription.text.toString()
            val address = mBinding.fragmentAddInformationAddress.text.toString()
            val pointOfInterest = mBinding.fragmentAddInformationPointOfInterest.text.toString()
            val state = mBinding.fragmentAddInformationState.text.toString()
            val creationDate = Utils.getTodayDate()


            bundle.putString("property", property)
            bundle.putString("price", price)
            bundle.putString("surface", surface)
            bundle.putString("rooms", rooms)
            bundle.putString("bathrooms", bathrooms)
            bundle.putString("bedrooms", bedrooms)
            bundle.putString("description", description)
            bundle.putString("address", address)
            bundle.putString("latitude", location.lat.toString())
            bundle.putString("longitude", location.lng.toString())
            bundle.putString("pointOfInterest", pointOfInterest)
            bundle.putString("state", state)
            bundle.putString("creationDate", creationDate)

            return bundle
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}
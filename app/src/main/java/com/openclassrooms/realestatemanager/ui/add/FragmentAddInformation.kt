package com.openclassrooms.realestatemanager.ui.add

import android.app.Activity
import android.content.ContentValues
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
import android.widget.EditText
import androidx.fragment.app.*
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.BuildConfig
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentAddInformationBinding
import com.openclassrooms.realestatemanager.retrofit.RetrofitInstance
import retrofit2.HttpException
import java.io.ByteArrayOutputStream
import java.io.IOException

class FragmentAddInformation : Fragment() {

    private var _binding: FragmentAddInformationBinding? = null
    private val mBinding get() = _binding!!
    private lateinit var staticMap: Bitmap


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
            try {
                RetrofitInstance.getBitmapFrom(
                    HTTP_REQUEST,
                    inputText,
                    "15",
                    "1500x1100",
                    "1",
                    "jpg",
                    inputText,
                    BuildConfig.GMP_KEY
                )  {
                    Glide.with(mBinding.root)
                        .load(it)
                        .centerCrop()
                        .into(mBinding.fragmentAddInformationStaticMap)
                    if (it != null) {
                        staticMap = it
                    }
                }
            } catch (exception: IOException) {
                Log.e(ContentValues.TAG, "IOException, you might have internet connection" + exception.message)
            } catch (exception: HttpException) {
                Log.e(ContentValues.TAG, "HttpException, unexpected response" + exception.message)
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
        val stream = ByteArrayOutputStream()
        staticMap.compress(Bitmap.CompressFormat.PNG, 90, stream)
        val image = stream.toByteArray()
        if (TextUtils.isEmpty(mBinding.fragmentAddInformationAddress.text)) {
            requireActivity().setResult(Activity.RESULT_CANCELED, replyIntent)
            return bundle
        } else {
            val city = mBinding.fragmentAddInformationAddress.text.toString()
            val price = mBinding.fragmentAddInformationPrice.text.toString()
            val type = mBinding.fragmentAddInformationProperty.text.toString()
            val state = mBinding.fragmentAddInformationState.text.toString()

            bundle.putString("city", city)
            bundle.putString("price", price)
            bundle.putString("type", type)
            bundle.putString("state", state)
            bundle.putByteArray("static_map", image)
            return bundle
        }
    }

    companion object {
        private const val HTTP_REQUEST = "https://maps.googleapis.com/maps/api/staticmap"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}
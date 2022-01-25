package com.openclassrooms.realestatemanager.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.*
import androidx.fragment.app.replace
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentAddRealEstateBinding

class FragmentAddRealEstate : Fragment() {

    private var _binding: FragmentAddRealEstateBinding? = null
    private val mBinding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddRealEstateBinding.inflate(inflater, container, false)
        val view = mBinding.root
        this.configureListener()
        return view
    }

    private fun configureListener() {
        mBinding.fragmentAddRealEstateAddImageBtn.setOnClickListener{
            val addImageFragment = FragmentAddRealEstateImage()
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
    }

    private fun getInformation(): Bundle{
        val replyIntent = Intent()
        val bundle = Bundle()
        if (TextUtils.isEmpty(mBinding.fragmentAddRealEstateAddress.text)) {
            requireActivity().setResult(Activity.RESULT_CANCELED, replyIntent)
            return bundle
        } else {
            val city = mBinding.fragmentAddRealEstateAddress.text.toString()
            val price = mBinding.fragmentAddRealEstatePrice.text.toString()
            val type = mBinding.fragmentAddRealEstateProperty.text.toString()
            val state = mBinding.fragmentAddRealEstateState.text.toString()


            bundle.putString("city", city)
            bundle.putString("price", price)
            bundle.putString("type", type)
            bundle.putString("state", state)
            return bundle
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}
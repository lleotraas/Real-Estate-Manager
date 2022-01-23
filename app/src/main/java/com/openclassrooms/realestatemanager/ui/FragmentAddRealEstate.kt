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
            this.getInformation()
            var fragmentManager = requireActivity().supportFragmentManager
            fragmentManager  .commit{
                setReorderingAllowed(true)
                replace<FragmentAddRealEstateImage>(R.id.activity_add_real_estate_container)

            }
        }
    }

    private fun getInformation() {
        val replyIntent = Intent()
        if (TextUtils.isEmpty(mBinding.fragmentAddRealEstateAddress.text)) {
            requireActivity().setResult(Activity.RESULT_CANCELED, replyIntent)
        } else {
            val city = mBinding.fragmentAddRealEstateAddress.text.toString()
            val price = mBinding.fragmentAddRealEstatePrice.text.toString()
            val type = mBinding.fragmentAddRealEstateProperty.text.toString()


            replyIntent.putExtra("city", city)
            replyIntent.putExtra("price", price)
            replyIntent.putExtra("type", type)
            requireActivity().setResult(Activity.RESULT_OK, replyIntent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}
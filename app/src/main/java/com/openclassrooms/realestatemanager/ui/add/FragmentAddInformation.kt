package com.openclassrooms.realestatemanager.ui.add

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.*
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentAddInformationBinding

class FragmentAddInformation : Fragment() {

    private var _binding: FragmentAddInformationBinding? = null
    private val mBinding get() = _binding!!


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
    }

    private fun getInformation(): Bundle{
        val replyIntent = Intent()
        val bundle = Bundle()
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
            return bundle
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}
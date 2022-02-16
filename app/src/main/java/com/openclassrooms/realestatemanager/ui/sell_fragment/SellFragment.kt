package com.openclassrooms.realestatemanager.ui.sell_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.openclassrooms.realestatemanager.RealEstateViewModelFactory
import com.openclassrooms.realestatemanager.databinding.FragmentSellDialogBinding
import com.openclassrooms.realestatemanager.dependency.RealEstateApplication
import com.openclassrooms.realestatemanager.model.RealEstate
import com.openclassrooms.realestatemanager.ui.activity.RealEstateViewModel
import com.openclassrooms.realestatemanager.utils.Utils
import kotlinx.coroutines.launch

class SellFragment : DialogFragment() {

    private lateinit var mBinding: FragmentSellDialogBinding
    private val mViewModel: RealEstateViewModel by viewModels {
        RealEstateViewModelFactory(
            (requireActivity().application as RealEstateApplication).realEstateRepository,
            (requireActivity().application as RealEstateApplication).realEstateImageRepository,
            (requireActivity().application as RealEstateApplication).filterRepository)
    }
    private var currentRealEstate: RealEstate? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentSellDialogBinding.inflate(inflater, container, false)
        configureListeners()
        val args = arguments
        val currentRealEstateId = args?.get("id") as Long?
        getRealEstate(currentRealEstateId)
        return mBinding.root
    }

    private fun getRealEstate(currentRealEstateId: Long?) {
        mViewModel.getRealEstateById(currentRealEstateId!!).observe(viewLifecycleOwner) { realEstate ->
            currentRealEstate = realEstate
        }
    }

    private fun configureListeners() {
        mBinding.fragmentSellDialogPositiveBtn.setOnClickListener {
            val sellerName = mBinding.fragmentSellDialogEnterNameInput.text.toString()
            currentRealEstate!!.sellerName = sellerName
            currentRealEstate!!.sellDate = Utils.getTodayDate()
            lifecycleScope.launch {
                mViewModel.updateRealEstate(currentRealEstate!!)
            }
            dismiss()
        }

        mBinding.fragmentSellDialogNegativeBtn.setOnClickListener {
            dismiss()
        }
    }
}
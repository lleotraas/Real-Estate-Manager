package com.openclassrooms.realestatemanager.features_real_estate.presentation.dialog.sell

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.openclassrooms.realestatemanager.databinding.FragmentSellDialogBinding
import com.openclassrooms.realestatemanager.features_real_estate.domain.model.RealEstate
import com.openclassrooms.realestatemanager.features_real_estate.presentation.RealEstateViewModel
import com.openclassrooms.realestatemanager.features_real_estate.data.utils.UtilsKt
import com.openclassrooms.realestatemanager.features_real_estate.data.utils.UtilsKt.Companion.getTodayDate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SellFragment : DialogFragment() {

    private lateinit var mBinding: FragmentSellDialogBinding
    private val mViewModel: RealEstateViewModel by viewModels()
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
//        mViewModel.getRealEstateById(currentRealEstateId!!).observe(viewLifecycleOwner) { realEstate ->
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.state.collect { state ->
                    currentRealEstate = state.realEstate
                }
            }
        }

//        }
    }

    private fun configureListeners() {
        mBinding.fragmentSellDialogPositiveBtn.setOnClickListener {
            val sellerName = mBinding.fragmentSellDialogEnterNameInput.text.toString()
            currentRealEstate!!.sellerName = sellerName
            currentRealEstate!!.sellDate = UtilsKt.parseDate(getTodayDate())
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
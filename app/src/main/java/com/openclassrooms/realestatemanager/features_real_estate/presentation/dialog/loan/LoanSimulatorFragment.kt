package com.openclassrooms.realestatemanager.features_real_estate.presentation.dialog.loan

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.SeekBar
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentLoanSimulatorBinding
import com.openclassrooms.realestatemanager.features_real_estate.data.utils.UtilsKt
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoanSimulatorFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentLoanSimulatorBinding
    private var price: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoanSimulatorBinding.inflate(inflater, container, false)
        price = arguments?.get("price") as Int
        binding.fragmentLoanDuration.isEnabled = false
        this.configureListeners()
        return binding.root
    }

    private fun configureListeners() {
        var years = 0
        binding.fragmentLoanContribution.onTextChanged {
            if(enableDurationSeekBar()) {
                binding.fragmentLoanDuration.isEnabled = true
                setResult(years)
            } else {
                binding.fragmentLoanDuration.isEnabled = false
            }
        }

        binding.fragmentLoanRate.onTextChanged {
            if(enableDurationSeekBar()) {
                binding.fragmentLoanDuration.isEnabled = true
                setResult(years)
            } else {
                binding.fragmentLoanDuration.isEnabled = false
            }
        }

        binding.fragmentLoanDuration.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.fragmentLoanDurationTitle.text = String.format(
                    "%s %s %s" ,
                    requireContext().resources.getString(R.string.fragment_loan_simulator_duration_title),
                    progress,
                    requireContext().resources.getString(R.string.fragment_loan_simulator_year)
                )
                years = progress
                setResult(years)
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })
    }

    private fun setResult(years: Int) {
        binding.fragmentLoanMonthlyPayment.text = String.format(
            "%s: %.2f",
            requireContext().resources.getString(R.string.fragment_loan_simulator_monthly_payment),
            UtilsKt.loanCalculator(
                binding.fragmentLoanContribution.text.toString().toInt(),
                binding.fragmentLoanRate.text.toString().toDouble(),
                years,
                price!!
            ))
    }

    private fun enableDurationSeekBar(): Boolean {
        return binding.fragmentLoanContribution.text!!.isNotEmpty() &&
                binding.fragmentLoanRate.text!!.isNotEmpty()
    }

    private fun EditText.onTextChanged(onTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                onTextChanged.invoke(editableText.toString())
            }
            override fun afterTextChanged(p0: Editable?) {}
        })
    }
}
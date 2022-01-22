package com.openclassrooms.realestatemanager.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentMainActivityBinding
import com.openclassrooms.realestatemanager.dependency.RealEstateApplication
import com.openclassrooms.realestatemanager.model.RealEstate

class FragmentRealEstate : Fragment() {

    private var _binding: FragmentMainActivityBinding? = null
    private val mBinding get() = _binding!!
    private val newRealEstateActivityRequestCode = 123
    private val mViewModel: RealEstateViewModel by viewModels {
        RealEstateViewModelFactory((requireActivity().application as RealEstateApplication).realEstateRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainActivityBinding.inflate(inflater, container, false)
        val view = mBinding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = mBinding.realEstateRecyclerView
        val adapter = MainActivityAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        mViewModel.getAllRealEstate.observe(requireActivity()) {realEstate ->
            realEstate.let { adapter.submitList(it) }
        }

        mBinding.addFab.setOnClickListener{
            startActivityForResult(Intent(requireContext(), AddRealEstateActivity::class.java), newRealEstateActivityRequestCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == newRealEstateActivityRequestCode && resultCode == Activity.RESULT_OK) {
            val city = data?.getStringExtra("city") ?: ""
            val price = data?.getStringExtra("price") ?: ""
            val type = data?.getStringExtra("type") ?: ""
            val priceConverted = Integer.parseInt(price)
            RealEstate(0, city, priceConverted, type).let { mViewModel.insert(it) }
        } else {
            Toast.makeText(
                requireContext(),
                requireContext().resources.getString(R.string.main_fragment_error_save_estate),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
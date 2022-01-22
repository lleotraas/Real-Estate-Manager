package com.openclassrooms.realestatemanager.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.ui.MainActivityAdapter.MainActivityViewHolder
import com.openclassrooms.realestatemanager.databinding.RealEstateRowBinding
import com.openclassrooms.realestatemanager.model.RealEstate

class MainActivityAdapter : ListAdapter<RealEstate, MainActivityViewHolder>(RealEstateComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainActivityViewHolder {
        val binding = RealEstateRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MainActivityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MainActivityViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MainActivityViewHolder(private val binding: RealEstateRowBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(realEstate: RealEstate) {
            binding.realEstateRowCity.text = realEstate.address
            binding.realEstateRowPrice.text = realEstate.price.toString()
            binding.realEstateRowType.text = realEstate.property
        }
    }

    class RealEstateComparator : DiffUtil.ItemCallback<RealEstate>() {
        override fun areItemsTheSame(oldItem: RealEstate, newItem: RealEstate): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: RealEstate, newItem: RealEstate): Boolean {
            return oldItem.address == newItem.address
        }

    }
}
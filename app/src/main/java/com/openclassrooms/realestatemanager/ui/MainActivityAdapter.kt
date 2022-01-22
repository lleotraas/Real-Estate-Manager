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
//        return MainActivityViewHolder.create(parent)
        val binding = RealEstateRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MainActivityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MainActivityViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MainActivityViewHolder(private val binding: RealEstateRowBinding) : RecyclerView.ViewHolder(binding.root) {
//        private val city: TextView = itemView.findViewById(R.id.real_estate_row_city)
//        private val price: TextView = itemView.findViewById(R.id.real_estate_row_price)
//        private val type: TextView = itemView.findViewById(R.id.real_estate_row_type)

        fun bind(realEstate: RealEstate) {

            binding.realEstateRowCity.text = realEstate.address
            binding.realEstateRowPrice.text = realEstate.price.toString()
            binding.realEstateRowType.text = realEstate.property

        }

//        companion object {
//            fun create(parent: ViewGroup): MainActivityViewHolder {
//                val view: View = LayoutInflater.from(parent.context)
//                    .inflate(R.layout.real_estate_detail_row, parent, false)
//                return MainActivityViewHolder(view)
//            }
//        }
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
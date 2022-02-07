package com.openclassrooms.realestatemanager.ui.add


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.openclassrooms.realestatemanager.databinding.PoiRowBinding


class AddInformationAdapter : ListAdapter<String, AddInformationAdapter.PoiViewHolder>(Companion) {

    inner class PoiViewHolder(val binding: PoiRowBinding) : ViewHolder(binding.root)

    companion object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PoiViewHolder {
        return PoiViewHolder(PoiRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: PoiViewHolder, position: Int) {
        val poi = currentList[position]
        holder.binding.apply {
            poiRowItemTv.text = poi
        }
    }
}


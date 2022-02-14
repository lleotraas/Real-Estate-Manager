package com.openclassrooms.realestatemanager.ui.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.databinding.RealEstateDetailPhotoRowBinding
import com.openclassrooms.realestatemanager.model.RealEstatePhoto
import com.openclassrooms.realestatemanager.utils.UtilsKt


class FragmentAddAdapter() :
    ListAdapter<RealEstatePhoto, FragmentAddAdapter.FragmentAddRealEstateViewHolder>(Companion) {

    inner class FragmentAddRealEstateViewHolder(val binding: RealEstateDetailPhotoRowBinding) : RecyclerView.ViewHolder(binding.root)

    companion object : DiffUtil.ItemCallback<RealEstatePhoto>() {
        override fun areItemsTheSame(oldItem: RealEstatePhoto, newItem: RealEstatePhoto): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: RealEstatePhoto, newItem: RealEstatePhoto): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FragmentAddRealEstateViewHolder {
        return FragmentAddRealEstateViewHolder(RealEstateDetailPhotoRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: FragmentAddRealEstateViewHolder, position: Int) {
        val realEstatePhoto =  currentList[position]
        val uri = UtilsKt.getPicture(holder.binding.root.context, realEstatePhoto)
        val photo = UtilsKt.loadPhotoFromAppDirectory(uri)
        holder.binding.apply {
            Glide.with(holder.binding.root)
                .load(photo)
                .centerCrop()
                .into(realEstateDetailPhotoRowImg)
            if (realEstatePhoto.category.isNotEmpty()) {
                realEstateDetailPhotoRowTv.text = realEstatePhoto.category
            } else {
                realEstateDetailPhotoRowTv.visibility = View.GONE
            }
        }

    }

}
package com.openclassrooms.realestatemanager.features_real_estate.presentation.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.databinding.FragmentDetailPhotoRowBinding
import com.openclassrooms.realestatemanager.features_real_estate.domain.model.RealEstatePhoto


class DetailAdapter(
    var onPhotoClickFullScreen: (RealEstatePhoto) -> Unit
) :
    ListAdapter<RealEstatePhoto, DetailAdapter.FragmentAddRealEstateViewHolder>(Companion) {

    inner class FragmentAddRealEstateViewHolder(val binding: FragmentDetailPhotoRowBinding) : RecyclerView.ViewHolder(binding.root)

    companion object : DiffUtil.ItemCallback<RealEstatePhoto>() {
        override fun areItemsTheSame(oldItem: RealEstatePhoto, newItem: RealEstatePhoto): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: RealEstatePhoto, newItem: RealEstatePhoto): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FragmentAddRealEstateViewHolder {
        return FragmentAddRealEstateViewHolder(FragmentDetailPhotoRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: FragmentAddRealEstateViewHolder, position: Int) {
        val realEstatePhoto =  currentList[position]
        holder.binding.apply {
            Glide.with(holder.binding.root)
                .load(realEstatePhoto.photo)
                .centerCrop()
                .into(realEstateDetailPhotoRowImg)

            if (realEstatePhoto.category.isNotEmpty()) {
                realEstateDetailPhotoRowTv.text = realEstatePhoto.category
            } else {
                realEstateDetailPhotoRowTv.visibility = View.GONE
            }

            realEstateDetailPhotoRowFullscreenBtn.setOnClickListener {
                onPhotoClickFullScreen(realEstatePhoto)
            }
        }

    }
}
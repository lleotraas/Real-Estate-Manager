package com.openclassrooms.realestatemanager.features_real_estate.presentation.add_image

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentAddPhotoRowBinding
import com.openclassrooms.realestatemanager.features_real_estate.domain.model.RealEstatePhoto

class AddImagedAdapter(
    var onPhotoClickDelete: (RealEstatePhoto) -> Unit
) : ListAdapter<RealEstatePhoto, AddImagedAdapter.PhotoViewHolder>(Companion) {

    inner class PhotoViewHolder(val binding: FragmentAddPhotoRowBinding): RecyclerView.ViewHolder(binding.root)

    companion object : DiffUtil.ItemCallback<RealEstatePhoto>() {
        override fun areItemsTheSame(oldItem: RealEstatePhoto, newItem: RealEstatePhoto): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: RealEstatePhoto, newItem: RealEstatePhoto): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        return PhotoViewHolder(FragmentAddPhotoRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    @SuppressLint("CheckResult")
    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val realEstatePhoto = currentList[position]
        holder.binding.apply {
            Glide.with(holder.binding.root)
                .load(realEstatePhoto.photo)
                .centerCrop()
                .into(photoRowImageView)

            photoRowCategoryInput.setText(realEstatePhoto.category)

            photoRowDeleteBtn.setOnClickListener {
                onPhotoClickDelete(realEstatePhoto)
            }
            photoRowCategoryInput.setOnClickListener {
                val alertDialog = MaterialDialog(it.context)
                alertDialog.positiveButton {
                    alertDialog.dismiss()
                }
                alertDialog.show {
                    listItemsSingleChoice(R.array.category_array) {
                            _, _, text ->
                        photoRowCategoryInput.setText(text)
                        realEstatePhoto.category = text.toString()
                    }
                }
            }
        }
    }
}
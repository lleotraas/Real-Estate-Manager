package com.openclassrooms.realestatemanager.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.databinding.PhotoRowBinding
import com.openclassrooms.realestatemanager.model.InternalStoragePhoto
import com.openclassrooms.realestatemanager.model.SharedStoragePhoto

class InternalStoragePhotoAdapter(
    var onPhotoClick: (SharedStoragePhoto) -> Unit
) : ListAdapter<SharedStoragePhoto, InternalStoragePhotoAdapter.PhotoViewHolder>(Companion) {

    inner class PhotoViewHolder(val binding: PhotoRowBinding): RecyclerView.ViewHolder(binding.root)

    companion object : DiffUtil.ItemCallback<SharedStoragePhoto>() {
        override fun areItemsTheSame(oldItem: SharedStoragePhoto, newItem: SharedStoragePhoto): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: SharedStoragePhoto, newItem: SharedStoragePhoto): Boolean {
            return oldItem.name == newItem.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        return PhotoViewHolder(PhotoRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = currentList[position]
        holder.binding.apply {
            Glide.with(holder.binding.root)
                .load(photo.contentUri)
                .centerCrop()
                .into(photoRowImageView)


            photoRowImageView.setOnClickListener {
                onPhotoClick(photo)
            }
        }
    }
}
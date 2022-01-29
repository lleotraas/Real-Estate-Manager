package com.openclassrooms.realestatemanager.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.databinding.PhotoRowBinding
import com.openclassrooms.realestatemanager.model.SharedStoragePhoto

class SharedPhotoAdapter(
    private val onPhotoClick: (SharedStoragePhoto) -> Unit
) : ListAdapter<SharedStoragePhoto, SharedPhotoAdapter.PhotoViewHolder>(Companion) {

    inner class PhotoViewHolder(val binding: PhotoRowBinding): RecyclerView.ViewHolder(binding.root)

    companion object: DiffUtil.ItemCallback<SharedStoragePhoto>() {
        override fun areItemsTheSame(oldItem: SharedStoragePhoto, newItem: SharedStoragePhoto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SharedStoragePhoto, newItem: SharedStoragePhoto): Boolean {
            return oldItem.contentUri == newItem.contentUri
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        return PhotoViewHolder(PhotoRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = currentList[position]
        holder.binding.apply {
            photoRowImageView.setImageURI(photo.contentUri)

            val aspectRatio = photo.width.toFloat() / photo.height.toFloat()
            ConstraintSet().apply {
                clone(root)
                setDimensionRatio(photoRowImageView.id, aspectRatio.toString())
                applyTo(root)
            }
            photoRowImageView.setOnClickListener {
                onPhotoClick(photo)
                true
            }
        }
    }
}
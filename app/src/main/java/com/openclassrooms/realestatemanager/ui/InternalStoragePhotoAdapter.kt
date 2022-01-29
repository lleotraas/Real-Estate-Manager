package com.openclassrooms.realestatemanager.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.databinding.PhotoRowBinding
import com.openclassrooms.realestatemanager.model.InternalStoragePhoto

class InternalStoragePhotoAdapter(
    private val onPhotoClick: (InternalStoragePhoto) -> Unit
) : ListAdapter<InternalStoragePhoto, InternalStoragePhotoAdapter.PhotoViewHolder>(Companion) {

    inner class PhotoViewHolder(val binding: PhotoRowBinding): RecyclerView.ViewHolder(binding.root)

    companion object : DiffUtil.ItemCallback<InternalStoragePhoto>() {
        override fun areItemsTheSame(oldItem: InternalStoragePhoto, newItem: InternalStoragePhoto): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: InternalStoragePhoto, newItem: InternalStoragePhoto): Boolean {
            return oldItem.name == newItem.name && oldItem.bmp.sameAs(newItem.bmp)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        return PhotoViewHolder(PhotoRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = currentList[position]
        holder.binding.apply {
            photoRowImageView.setImageBitmap(photo.bmp)

            val aspectRatio = photo.bmp.width.toFloat() / photo.bmp.height.toFloat()
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
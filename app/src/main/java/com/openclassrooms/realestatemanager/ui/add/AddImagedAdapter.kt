package com.openclassrooms.realestatemanager.ui.add

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.databinding.PhotoRowBinding

class AddImagedAdapter(
    var onPhotoClick: (String) -> Unit
) : ListAdapter<String, AddImagedAdapter.PhotoViewHolder>(Companion) {

    inner class PhotoViewHolder(val binding: PhotoRowBinding): RecyclerView.ViewHolder(binding.root)

    companion object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        return PhotoViewHolder(PhotoRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photoUri = currentList[position]
        holder.binding.apply {
            Glide.with(holder.binding.root)
                .load(photoUri)
                .centerCrop()
                .into(photoRowImageView)


            photoRowDeleteBtn.setOnClickListener {
                onPhotoClick(photoUri)
            }
        }
    }
}
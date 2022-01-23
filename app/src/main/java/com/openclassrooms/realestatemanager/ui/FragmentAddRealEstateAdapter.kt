package com.openclassrooms.realestatemanager.ui

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.databinding.RealEstateDetailRowBinding

class FragmentAddRealEstateAdapter(private val listOfUri: List<Uri>) :
    RecyclerView.Adapter<FragmentAddRealEstateAdapter.FragmentAddRealEstateViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FragmentAddRealEstateViewHolder {
        val binding = RealEstateDetailRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FragmentAddRealEstateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FragmentAddRealEstateViewHolder, position: Int) {
        holder.bind(listOfUri[position])
    }

    override fun getItemCount(): Int {
        return listOfUri.size
    }

    class FragmentAddRealEstateViewHolder(private val binding: RealEstateDetailRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(uri: Uri) {
//            binding.realEstatePhoto.setImageURI(uri)
            Glide.with(binding.root)
                .load(uri)
                .centerCrop()
                .into(binding.realEstatePhoto)
        }
    }
}
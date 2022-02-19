package com.openclassrooms.realestatemanager.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityAddBinding

class AddRealEstateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.activityItemDetailToolbar.toolbar)
    }
}
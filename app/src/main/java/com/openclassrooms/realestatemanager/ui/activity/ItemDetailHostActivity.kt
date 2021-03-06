package com.openclassrooms.realestatemanager.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.navigation.findNavController
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityDetailBinding
import com.openclassrooms.realestatemanager.utils.UtilsKt

class ItemDetailHostActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.activityItemDetailToolbar.toolbar)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_item_detail)
        val map = findViewById<CoordinatorLayout>(R.id.fragment_map_view_container)
        if (map != null) {
            navController.navigate(R.id.navigate_from_maps_to_list)
        } else {
            navController.navigate(R.id.navigate_from_details_to_list)
        }
        return true
    }
}
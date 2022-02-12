package com.openclassrooms.realestatemanager.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityItemDetailBinding
import com.openclassrooms.realestatemanager.ui.detail.ItemDetailFragment
import com.openclassrooms.realestatemanager.utils.UtilsKt

class ItemDetailHostActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityItemDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.activityItemDetailToolbar.toolbar)
        title = if (UtilsKt.isConnectedToInternet(this)) {
            resources.getString(R.string.app_name_offline)
        } else {
            resources.getString(R.string.app_name)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_item_detail)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}
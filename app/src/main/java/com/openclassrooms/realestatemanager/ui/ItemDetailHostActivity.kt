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

class ItemDetailHostActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityItemDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.activityItemDetailToolbar.toolbar)

        val intentFragment = intent.extras?.getInt(ARG_FRAGMENT)
        val realEstateId = intent.extras?.getString(ARG_ID)

        if (intentFragment != null) {
            val detailFragment = ItemDetailFragment()
            val bundle = Bundle()
            bundle.putString(ARG_ID, realEstateId)
            detailFragment.arguments = bundle
            val fragmentManager = this.supportFragmentManager
            fragmentManager.commit {
                setReorderingAllowed(true)
                val isTablet = resources.getBoolean(R.bool.isTablet)
                if (isTablet) {
                    replace(R.id.item_detail_fragment, detailFragment)
                } else {
                    replace(R.id.nav_host_fragment_item_detail, detailFragment)
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_item_detail)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    companion object {
        val ARG_FRAGMENT = "detail_fragment"
        val ARG_ID= "real_estate_id"
    }
}
package com.openclassrooms.realestatemanager.dependency

import android.app.Application
import com.openclassrooms.realestatemanager.database.RealEstateDatabase
import com.openclassrooms.realestatemanager.repository.RealEstateRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class RealEstateApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())
    private val database by lazy { RealEstateDatabase.getDatabase(this, applicationScope) }
    val realEstateRepository by lazy { RealEstateRepository(database.realEstateDao()) }
}
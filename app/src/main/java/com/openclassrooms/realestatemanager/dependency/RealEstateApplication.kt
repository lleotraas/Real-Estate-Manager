package com.openclassrooms.realestatemanager.dependency

import android.app.Application
import com.openclassrooms.realestatemanager.database.RealEstateDatabase
import com.openclassrooms.realestatemanager.repository.FilterRepository
import com.openclassrooms.realestatemanager.repository.RealEstatePhotoRepository
import com.openclassrooms.realestatemanager.repository.RealEstateRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class RealEstateApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())
    private val database by lazy { RealEstateDatabase.getDatabase(this, applicationScope) }
    val realEstateRepository by lazy { RealEstateRepository(database.realEstateDao()) }
    val realEstateImageRepository by lazy {RealEstatePhotoRepository(database.realEstateImageDao())}
    val filterRepository by lazy { FilterRepository() }

    override fun onCreate() {
        super.onCreate()
        isRunningTest = try {
            Class.forName("com.openclassrooms.realestatemanager.RetrofitTest")
            true
        } catch (exception: ClassNotFoundException) {
            false
        }
    }

    companion object {
        var isRunningTest: Boolean = false
    }
}
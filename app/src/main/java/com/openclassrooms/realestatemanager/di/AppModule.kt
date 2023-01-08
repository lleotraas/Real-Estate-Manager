package com.openclassrooms.realestatemanager.di

import android.content.Context
import androidx.room.Room
import com.openclassrooms.realestatemanager.features_real_estate.data.data_source.RealEstateDatabase
import com.openclassrooms.realestatemanager.features_real_estate.data.data_source.dao.RealEstateDao
import com.openclassrooms.realestatemanager.features_real_estate.data.data_source.dao.RealEstatePhotoDao
import com.openclassrooms.realestatemanager.features_add_real_estate.data.remote.AutocompleteApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRealEstateDatabase(@ApplicationContext appContext: Context): RealEstateDatabase {
        return Room.databaseBuilder(
                appContext,
                RealEstateDatabase::class.java,
                "real_estate_database"
            ).build()
    }

    @Provides
    @Singleton
    fun provideAutoCompleteApi(): AutocompleteApi = Retrofit.Builder()
        .baseUrl("https://api-adresse.data.gouv.fr/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(AutocompleteApi::class.java)

    @Provides
    fun provideRealEstateRepository(database: RealEstateDatabase): RealEstateDao = database.realEstateDao()

    @Provides
    fun provideRealEstatePhotoRepository(database: RealEstateDatabase): RealEstatePhotoDao = database.realEstatePhotoDao()

}
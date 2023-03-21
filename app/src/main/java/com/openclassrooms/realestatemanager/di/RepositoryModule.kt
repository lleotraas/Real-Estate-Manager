package com.openclassrooms.realestatemanager.di

import com.openclassrooms.realestatemanager.features_real_estate.data.repository.RealEstateRepositoryImpl
import com.openclassrooms.realestatemanager.features_real_estate.domain.repository.RealEstateRepository
import com.openclassrooms.realestatemanager.features_real_estate.data.repository.RealEstatePhotoRepositoryImpl
import com.openclassrooms.realestatemanager.features_real_estate.domain.repository.RealEstatePhotoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRealEstateRepository(
        realEstateRepositoryImpl: RealEstateRepositoryImpl
    ): RealEstateRepository

    @Binds
    @Singleton
    abstract fun bindRealEstatePhotoRepository(
        realEstatePhotoRepositoryImpl: RealEstatePhotoRepositoryImpl
    ): RealEstatePhotoRepository
}
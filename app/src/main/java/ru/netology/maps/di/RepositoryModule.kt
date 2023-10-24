package ru.netology.maps.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.netology.maps.domain.LocationRepository
import ru.netology.maps.data.LocationRepositoryImpl
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindsMapRepository(impl: LocationRepositoryImpl): LocationRepository
}

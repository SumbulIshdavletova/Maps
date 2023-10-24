package ru.netology.maps.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.netology.maps.data.MapDao
import ru.netology.maps.data.db.AppDb

@InstallIn(SingletonComponent::class)
@Module
object DaoModule {

    @Provides
    fun providePostDao(db: AppDb): MapDao = db.locationDao()


}
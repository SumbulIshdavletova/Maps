package ru.netology.maps.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.netology.maps.dao.MapDao
import ru.netology.maps.entity.LocationEntity

@Database(entities = [LocationEntity::class], version = 1, exportSchema = false)
abstract class AppDb : RoomDatabase() {

    abstract fun locationDao(): MapDao

    companion object {
        @Volatile
        private var instance: AppDb? = null

        fun getInstance(context: Context): AppDb {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, AppDb::class.java, "app.db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build()
    }
}
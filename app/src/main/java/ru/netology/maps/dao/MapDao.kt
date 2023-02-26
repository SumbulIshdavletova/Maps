package ru.netology.maps.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.netology.maps.entity.LocationEntity

@Dao
interface MapDao {

    @Query("SELECT * FROM LocationEntity ORDER BY id DESC")
    fun getAll(): LiveData<List<LocationEntity>>

    @Insert
    fun insert(location: LocationEntity)

    @Query("UPDATE LocationEntity SET latitude = :latitude, longitude = :longitude, title = :title WHERE id = :id")
    fun updateContentById(id: Long, latitude: Double, longitude: Double, title: String)

    fun save(location: LocationEntity) =
        if (location.id == 0L) insert(location) else updateContentById(
            location.id,
            location.latitude,
            location.longitude,
            location.title
        )

    @Query("DELETE FROM LocationEntity WHERE id = :id")
    fun removeById(id: Long)
}
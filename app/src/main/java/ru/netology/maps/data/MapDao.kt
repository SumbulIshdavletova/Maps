package ru.netology.maps.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.maps.data.entity.LocationEntity

@Dao
interface MapDao {

    @Query("SELECT * FROM LocationEntity ORDER BY id DESC")
    fun getAll(): Flow<List<LocationEntity>>

    @Insert
    suspend fun insert(location: LocationEntity)

    @Query("UPDATE LocationEntity SET latitude = :latitude, longitude = :longitude, title = :title WHERE id = :id")
    suspend fun updateContentById(id: Long, latitude: Double, longitude: Double, title: String)

    suspend fun save(location: LocationEntity) =
        if (location.id == 0L) insert(location) else updateContentById(
            location.id,
            location.latitude,
            location.longitude,
            location.title
        )

    @Query("DELETE FROM LocationEntity WHERE id = :id")
    suspend fun removeById(id: Long)
}
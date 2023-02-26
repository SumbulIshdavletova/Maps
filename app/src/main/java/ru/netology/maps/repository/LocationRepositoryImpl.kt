package ru.netology.maps.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import ru.netology.maps.dao.MapDao
import ru.netology.maps.dto.Location
import ru.netology.maps.entity.LocationEntity

class LocationRepositoryImpl(
    private val dao: MapDao,
) : LocationRepository {

    override fun getAll() = Transformations.map(dao.getAll()) { list ->
        list.map {
            it.toDto()
        }
    }

    override fun save(location: Location) {
        dao.save(LocationEntity.fromDto(location))
    }

    override fun removeById(id: Long) {
        dao.removeById(id)
    }
}
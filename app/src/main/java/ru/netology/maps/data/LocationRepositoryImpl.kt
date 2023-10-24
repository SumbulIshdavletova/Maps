package ru.netology.maps.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import ru.netology.maps.domain.LocationRepository
import ru.netology.maps.data.dto.Location
import ru.netology.maps.data.entity.LocationEntity
import ru.netology.maps.data.entity.toDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepositoryImpl @Inject constructor(
    private val dao: MapDao,
    ) : LocationRepository {

    override val data = dao.getAll()
        .map(List<LocationEntity>::toDto)
        .flowOn(Dispatchers.Default)

    override suspend fun getAll() = dao.getAll()
        .map(List<LocationEntity>::toDto)
        .flowOn(Dispatchers.Default)

    override suspend fun save(location: Location) {
        dao.save(LocationEntity.fromDto(location))
    }

    override suspend fun removeById(id: Long) {
        dao.removeById(id)
    }
}
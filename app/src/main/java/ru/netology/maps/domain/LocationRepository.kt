package ru.netology.maps.domain


import kotlinx.coroutines.flow.Flow
import ru.netology.maps.data.dto.Location

interface LocationRepository {

    val data: Flow<List<Location>>
    suspend fun getAll(): Flow<List<Location>>
    suspend fun save(location: Location)
    suspend fun removeById(id: Long)

}
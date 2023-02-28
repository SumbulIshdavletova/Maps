package ru.netology.maps.repository


import kotlinx.coroutines.flow.Flow
import ru.netology.maps.dto.Location

interface LocationRepository {

    val data: Flow<List<Location>>
    suspend fun getAll(): Flow<List<Location>>
    suspend fun save(location: Location)
    suspend fun removeById(id: Long)

}
package ru.netology.maps.repository

import androidx.lifecycle.LiveData
import ru.netology.maps.dto.Location

interface LocationRepository {

    fun getAll(): LiveData<List<Location>>
    fun save(location: Location)
    fun removeById(id: Long)

}
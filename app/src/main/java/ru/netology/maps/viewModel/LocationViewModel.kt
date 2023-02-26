package ru.netology.maps.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.netology.maps.db.AppDb
import ru.netology.maps.dto.Location
import ru.netology.maps.repository.LocationRepository
import ru.netology.maps.repository.LocationRepositoryImpl


private val empty = Location(
    id = 0,
    latitude = 0.0,
    longitude = 0.0,
    title = "",
)

class LocationViewModel(application: Application) : AndroidViewModel(application) {
    // упрощённый вариант
    private val repository: LocationRepository = LocationRepositoryImpl(
        AppDb.getInstance(context = application).locationDao()
    )
    val data = repository.getAll()
    private val edited = MutableLiveData(empty)

    fun changeLocationAndSave(latitude: Double, longitude: Double, title: String) {
        val l = latitude
        val l2 = longitude
        if (edited.value?.latitude == l && edited.value?.longitude == l2) {
            return
        } else {
            edited.value?.let {
                repository.save(it.copy(latitude = l, longitude = l2, title = title))
            }
        }
        edited.value = empty

    }

    fun changeTitle(title: String) {
        if (edited.value?.title == title) {
            return
        }
        edited.value?.let {
            repository.save(it.copy(title = title))
        }

        edited.value = empty

    }

    fun edit(location: Location) {
        edited.value = location
    }

    fun removeById(id: Long) = repository.removeById(id)


}

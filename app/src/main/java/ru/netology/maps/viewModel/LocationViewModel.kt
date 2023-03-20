package ru.netology.maps.viewModel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.maps.dto.Location
import ru.netology.maps.repository.LocationRepository
import javax.inject.Inject


private val empty = Location(
    id = 0,
    latitude = 0.0,
    longitude = 0.0,
    title = "",
)

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val repository: LocationRepository
) : ViewModel() {


    //    private val repository: LocationRepository = LocationRepositoryImpl(
//        AppDb.getInstance(context = application).locationDao()
//    )
    val data = repository.data.asLiveData(Dispatchers.Default)
    private val edited = MutableLiveData(empty)

    init {
        loadPlacemarks()
    }
    fun loadPlacemarks() = viewModelScope.launch { repository.getAll() }

    fun saveLocation(latitude: Double, longitude: Double,
                title: String
    ) {
        val saveLatitude = latitude
        val saveLongitude = longitude
        if (edited.value?.latitude == saveLatitude && edited.value?.longitude == saveLongitude) {
            return
        }
        viewModelScope.launch {
            edited.value?.let {
                repository.save(it.copy(latitude = saveLatitude, longitude = saveLongitude,title = title
                ))
            }
        }
        edited.value = empty
    }

    fun changeTitle(title: String) {
        if (edited.value?.title == title) {
            return
        }
        viewModelScope.launch {
            edited.value?.let {
                repository.save(it.copy(title = title))
            }

            edited.value = empty
        }
    }

    fun edit(location: Location) {
        edited.value = location
    }

    fun removeById(id: Long) = viewModelScope.launch { repository.removeById(id) }



}

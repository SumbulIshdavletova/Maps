package ru.netology.maps.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.maps.dto.Location

@Entity
class LocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val latitude: Double,
    val longitude: Double,
    val title: String,
) {

    fun toDto() =
        Location(
            id,
            latitude,
            longitude,
            title,
        )

    companion object {
        fun fromDto(dto: Location) =
            LocationEntity(
                dto.id,
                dto.latitude,
                dto.longitude,
                dto.title,
            )
    }
}

fun List<LocationEntity>.toDto(): List<Location> = map(LocationEntity::toDto)
fun List<Location>.toEntity(): List<LocationEntity> = map(LocationEntity::fromDto)

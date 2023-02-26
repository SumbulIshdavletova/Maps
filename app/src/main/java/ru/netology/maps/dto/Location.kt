package ru.netology.maps.dto

data class Location(
    val id: Long,
    val latitude: Double,
    val longitude: Double,
    val title: String,
) {
}
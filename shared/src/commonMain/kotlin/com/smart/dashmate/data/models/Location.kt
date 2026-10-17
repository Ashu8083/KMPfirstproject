package com.smart.dashmate.data.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer

@Serializable
data class Location(
    var latitude: Double,
    var longitude: Double

) {
}
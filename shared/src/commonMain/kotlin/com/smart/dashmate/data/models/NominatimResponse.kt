package com.smart.dashmate.data.models


import kotlinx.serialization.Serializable
@Serializable
data class NominatimResponse(
    val place_id: String? = null,
    val display_name: String,
    val lat: String,
    val lon: String
)
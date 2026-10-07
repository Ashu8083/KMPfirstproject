package com.smart.dashmate.data.models

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable


@Serializable
data class RouteResponse(
    val code: String,
    val routes: List<Route>
) {
    @Serializable
    data class Route(
        val distance: Double,
        val duration: Double,
        val geometry: Geometry
    )
    @Serializable
    data class Geometry(
        val type: String,
        val coordinates: List<List<Double>>
    )
}


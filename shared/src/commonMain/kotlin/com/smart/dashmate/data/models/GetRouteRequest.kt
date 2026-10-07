package com.smart.dashmate.data.models

data class GetRouteRequest(
    val startLatitude: Double,
    val startLongitude: Double,
    val destinationLatitude: Double,
    val destinationLongitude: Double
)


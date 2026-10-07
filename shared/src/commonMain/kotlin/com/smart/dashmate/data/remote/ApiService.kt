package com.smart.dashmate.data.remote

import com.smart.dashmate.data.models.GetRouteRequest
import com.smart.dashmate.data.models.RouteResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter



class ApiService(
    private val client: HttpClient
) {

    suspend fun getRoute(
        request: GetRouteRequest
    ): RouteResponse {

        println("Calling Route API...")

        val response = client.get("/api/routes") {
            parameter("fromLat", request.startLatitude)
            parameter("fromLng", request.startLongitude)
            parameter("toLat", request.destinationLatitude)
            parameter("toLng", request.destinationLongitude)
        }

        println("HTTP STATUS: ${response.status}")

        val rawResponse = response.body<String>()

        println("RAW RESPONSE:")
        println(rawResponse)

        return client.get("/api/routes") {
            parameter("fromLat", request.startLatitude)
            parameter("fromLng", request.startLongitude)
            parameter("toLat", request.destinationLatitude)
            parameter("toLng", request.destinationLongitude)
        }.body()
    }
}
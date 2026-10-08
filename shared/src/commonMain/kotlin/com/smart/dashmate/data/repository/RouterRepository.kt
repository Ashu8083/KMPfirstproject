package com.smart.dashmate.data.repository

import com.smart.dashmate.data.models.GetRouteRequest
import com.smart.dashmate.data.models.NominatimResponse
import com.smart.dashmate.data.remote.ApiService

class RouterRepository(
    private val apiService: ApiService
) {

    suspend fun getRoutes(
        getRouteRequest: GetRouteRequest
    ) = apiService.getRoute(
        request = getRouteRequest
    )

    suspend fun searchLocation(
        query: String
    ): List<NominatimResponse> {
        return apiService.searchLocation(query)
    }
}

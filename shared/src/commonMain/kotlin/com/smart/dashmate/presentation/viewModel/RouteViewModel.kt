package com.smart.dashmate.presentation.viewModel

import androidx.lifecycle.ViewModel
import com.smart.dashmate.data.models.GetRouteRequest
import com.smart.dashmate.data.models.RouteResponse
import com.smart.dashmate.data.repository.RouterRepository
class RouteViewModel(
    private val routerRepository: RouterRepository
) : ViewModel() {
    suspend fun getRoutes(
        getRouteRequest: GetRouteRequest
    ): RouteResponse {
        return routerRepository.getRoutes(getRouteRequest)
    }
}
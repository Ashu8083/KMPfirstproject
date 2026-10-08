package com.smart.dashmate.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smart.dashmate.data.models.GetRouteRequest
import com.smart.dashmate.data.models.NominatimResponse
import com.smart.dashmate.data.models.RouteResponse
import com.smart.dashmate.data.repository.RouterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RouteViewModel(
    private val routerRepository: RouterRepository
) : ViewModel() {
    private val _locations =

        MutableStateFlow<List<NominatimResponse>>(emptyList())

    val locations: StateFlow<List<NominatimResponse>> =

        _locations.asStateFlow()

    fun searchLocation(query: String) {

        if (query.length < 3) {

            _locations.value = emptyList()

            return

        }

        viewModelScope.launch {

            try {

                _locations.value =
                    routerRepository.searchLocation(query)
            } catch (e: Exception) {
                e.printStackTrace()
                _locations.value = emptyList()
            }
        }
    }

    fun clearLocations() {
        _locations.value = emptyList()
    }
    suspend fun getRoutes(
        getRouteRequest: GetRouteRequest
    ): RouteResponse {
        return routerRepository.getRoutes(getRouteRequest)
    }
}
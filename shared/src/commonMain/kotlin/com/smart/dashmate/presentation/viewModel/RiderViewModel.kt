package com.smart.dashmate.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smart.dashmate.data.models.Location
import com.smart.dashmate.data.models.RiderWebsocketMessage
import com.smart.dashmate.data.repository.RiderRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.uuid.Uuid

class RiderViewModel(
    private val riderRepository: RiderRepository
) : ViewModel() {

    val riderId: Uuid =
        Uuid.parse("60be7bb4-a174-4342-a8df-1c3d364b0c2b")
    val incomingMessages: StateFlow<RiderWebsocketMessage?> =
        riderRepository.incomingMessages
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null
            )

    fun sendLocationUpdate() {

        println("sendLocationUpdate called")
        viewModelScope.launch {
            val location = Location(
                latitude = 12.9716,
                longitude = 77.5946

            )
            riderRepository.locationUpdate(location)
        }

    }
    fun connectToWebSocket(riderId: String) {
        viewModelScope.launch {
            try {
                riderRepository.connectToWebSocket(
                    Uuid.parse(riderId)
                )
            } catch (e: Exception) {
                println("WebSocket connection failed: ${e.message}")
            }
        }
    }


}
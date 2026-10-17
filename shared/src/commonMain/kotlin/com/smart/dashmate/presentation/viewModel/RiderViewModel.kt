package com.smart.dashmate.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smart.dashmate.data.models.Location
import com.smart.dashmate.data.models.RiderWebsocketMessage
import com.smart.dashmate.data.repository.RiderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlin.uuid.Uuid

class RiderViewModel(
    private val riderRepository: RiderRepository
) : ViewModel() {

    val riderId: Uuid =
        Uuid.parse("60be7bb4-a174-4342-a8df-1c3d364b0c2b")

    val incomingMessages: StateFlow<RiderWebsocketMessage?> =
        riderRepository.incomingMessages.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    private val _tripDetails = MutableStateFlow<TripDetails?>(null)
    val tripDetails = _tripDetails.asStateFlow()

    val isConnected: StateFlow<Boolean> = riderRepository.isConnected

    private var connectJob: Job? = null

    init {
        // Automatically connect to WebSocket on ViewModel creation
        connectToWebSocket(riderId.toString())

        // Automatically listen for incoming WebSocket messages.
        viewModelScope.launch {
            riderRepository.incomingMessages.collect { message ->
                extractTheMessage(message)
            }
        }
    }

    fun extractTheMessage(message: RiderWebsocketMessage) {
        when (message.type) {
            "NEW_TRIP_REQUEST" -> {
                val payload = message.payload

                val tripId = payload["tripId"]
                    ?.jsonPrimitive?.contentOrNull

                val pickup = payload["pickup"]
                    ?.jsonPrimitive?.contentOrNull

                val dropOff = payload["drop_off"]
                    ?.jsonPrimitive?.contentOrNull

                val payment = payload["payment"]
                    ?.jsonPrimitive?.double

                if (tripId != null && pickup != null && dropOff != null && payment != null) {
                    _tripDetails.value = TripDetails(
                        tripId = tripId,
                        pickup = pickup,
                        dropOff = dropOff,
                        payment = payment
                    )

                    println("New trip received: $tripId")
                    println("Pickup: $pickup")
                    println("Drop-off: $dropOff")
                    println("Payment: $payment")
                } else {
                    println("Invalid NEW_TRIP_REQUEST payload")
                }
            }

            else -> {
                println("Received WebSocket message type: ${message.type}")
            }
        }
    }

    fun sendLocationUpdate() {
        viewModelScope.launch {
            val location = Location(
                latitude = 12.9716,
                longitude = 77.5946
            )

            riderRepository.locationUpdate(location)
        }
    }

    fun connectToWebSocket(riderIdInput: String) {
        if (isConnected.value) {
            println("WebSocket is already connected.")
            return
        }
        connectJob?.cancel()
        connectJob = viewModelScope.launch {
            try {
                val targetUuid = try {
                    if (riderIdInput.isNotBlank()) Uuid.parse(riderIdInput.trim()) else riderId
                } catch (_: Exception) {
                    println("Invalid UUID string provided ('$riderIdInput'). Falling back to default riderId: $riderId")
                    riderId
                }
                riderRepository.connectToWebSocket(targetUuid)
            } catch (e: Exception) {
                println("WebSocket connection failed: ${e.message}")
            }
        }
    }
}

data class TripDetails(
    val tripId: String,
    val pickup: String,
    val dropOff: String,
    val payment: Double
)
package com.smart.dashmate.data.repository

import com.smart.dashmate.data.models.Location
import com.smart.dashmate.data.models.RiderWebsocketMessage
import com.smart.dashmate.data.models.TripAccept
import com.smart.dashmate.data.remote.RiderWebSocketService
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import kotlin.uuid.Uuid

class RiderRepository
    (private val riderWebsocketService: RiderWebSocketService){

    val isConnected: StateFlow<Boolean>
        get() = riderWebsocketService.isConnected

    val incomingMessages: SharedFlow<RiderWebsocketMessage>
        get() = riderWebsocketService.incomingMessages

    suspend fun locationUpdate(location: Location) {
        println("STEP 1: Inside repository")

        try {
            println("STEP 2: Creating location payload")

            val payload = Json.encodeToJsonElement(location).jsonObject

            println("STEP 3: Payload created: $payload")

            println("STEP 4: Calling sendRiderMessage()")

            riderWebsocketService.sendRiderMessage(
                type = "LOCATION_UPDATE",
                payload = payload
            )

            println("STEP 5: WebSocket message sent successfully")

        } catch (e: Exception) {
            println("LOCATION UPDATE ERROR: ${e.message}")
            e.printStackTrace()
        }
    }

    suspend fun  acceptsTrip(tripAccept: TripAccept){
        val payload = Json.encodeToJsonElement(tripAccept).jsonObject
        riderWebsocketService.sendRiderMessage(
            type = "ACCEPT_TRIP",
            payload = payload
        )

    }
    suspend fun connectToWebSocket(riderId : Uuid){
        riderWebsocketService.riderWebSocketConnection(riderId)
    }



}
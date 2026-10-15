package com.smart.dashmate.data.repository

import com.smart.dashmate.data.models.Location
import com.smart.dashmate.data.models.RiderLocationUpdate
import com.smart.dashmate.data.models.RiderWebsocketMessage
import com.smart.dashmate.data.remote.RiderWebSocketService
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import kotlin.uuid.Uuid

class RiderRepository
    (private val riderWebsocketService: RiderWebSocketService){

    val incomingMessages: SharedFlow<RiderWebsocketMessage>
        get() = riderWebsocketService.incomingMessages

    suspend fun locationUpdate(location : Location){
        println("inside repository")
        val payload = Json.encodeToJsonElement(location).jsonObject

        riderWebsocketService.sendRiderMessage(
            type = "LOCATION_UPDATE",
            payload = payload
        )


    }
    suspend fun connectToWebSocket(riderId : Uuid){
        riderWebsocketService.riderWebSocketConnection(riderId)
    }



}
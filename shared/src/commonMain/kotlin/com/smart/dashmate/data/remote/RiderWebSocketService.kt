package com.smart.dashmate.data.remote

import com.smart.dashmate.data.models.RiderWebsocketMessage
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlin.uuid.Uuid

class RiderWebSocketService(
    private val client: HttpClient
) {

    private var session: WebSocketSession? = null
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val _isConnected = MutableStateFlow(false)
    val isConnected = _isConnected.asStateFlow()

    private val _incomingMessages = MutableSharedFlow<RiderWebsocketMessage>(
        extraBufferCapacity = 32
    )
    val incomingMessages = _incomingMessages.asSharedFlow()

    suspend fun riderWebSocketConnection(
        riderId: Uuid
    ) {

        println("Attempting WebSocket connection...")
        try {
            client.webSocket(
                urlString = "ws://localhost:8083/ws/rider?userId=$riderId"
            ) {
                session = this
                _isConnected.value = true
                println("WebSocket connected successfully!")
                println("Rider ID: $riderId  and session with $session")
                println("Rider ID: $riderId")
                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> {
                            val text = frame.readText()
                            println("Received from backend: $text")
                            try {
                                val message = json.decodeFromString<RiderWebsocketMessage>(
                                    text
                                )
                                _incomingMessages.emit(message)
                            } catch (e: Exception) {
                                println("Failed to parse WebSocket message: ${e.message}")
                            }
                        }
                        else -> Unit
                    }
                }
            }
        } catch (e: Exception) {
            println(
                "WebSocket connection failed: ${e.message}"
            )
            throw e
        } finally {
            session = null
            _isConnected.value = false
            println("WebSocket session closed and cleared")
        }
    }
    suspend fun sendRiderMessage(
        type: String,
        payload: JsonObject
    ) {
        println("Inside sendRiderMessage function")
        val currentSession = session
            ?: error("WebSocket connection not established")
        val message = RiderWebsocketMessage(
            type = type,
            payload = payload
        )
        val text = json.encodeToString(message)
        println("Sending WebSocket message: $text")
        currentSession.send(Frame.Text(text))
        println("WebSocket send completed")
    }

    suspend fun disconnect() {
        val currentSession = session ?: return
        println("Disconnecting WebSocket...")
        try {
            currentSession.close(
                CloseReason(
                    CloseReason.Codes.NORMAL,
                    "Rider disconnected"
                )
            )
        } finally {
            session = null
            _isConnected.value = false
        }
    }
}
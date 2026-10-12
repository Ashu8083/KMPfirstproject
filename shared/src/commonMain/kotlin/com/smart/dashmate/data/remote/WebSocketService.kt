package com.smart.dashmate.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.http.HttpHeaders
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlin.uuid.Uuid


class WebSocketService(
    private val client: HttpClient
) {
    suspend fun riderWebSocketConnection(
        riderId: Uuid
    ) {
        println("Attempting WebSocket connection...")
        try {
            client.webSocket(
                urlString = "ws://localhost:8083/ws/rider" ,
                request = {
                    headers.append("userId", riderId.toString())
                }
            ) {
                println("WebSocket connected successfully!")
                println("Rider ID: $riderId")

                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> {
                            val message = frame.readText()
                            println("Received from backend: $message")
                        }
                        else -> Unit
                    }
                }
                println("WebSocket session ended.")
            }
        } catch (e: Exception) {
            println(
                "WebSocket connection failed: ${e.message}"
            )
            throw e
        }
    }
}
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

}
package com.smart.dashmate.data.models

import kotlinx.serialization.json.JsonObject

data class WebSocketResponse(
    val type: String,
    val payload: JsonObject
) {
}
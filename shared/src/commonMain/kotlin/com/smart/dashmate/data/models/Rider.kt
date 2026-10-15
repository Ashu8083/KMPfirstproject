package com.smart.dashmate.data.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class RiderWebsocketMessage(
    val type :String,
    val payload : JsonObject
){

}

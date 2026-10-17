package com.smart.dashmate.data.models

import kotlin.uuid.Uuid

data class NewTripInfo(

    val tripId : Uuid,
    val pickup : String,
    val pickup_latitude : Double,
    val pickup_longitude : Double,
    val dropOff : String,
    val drop_latitude : Double,
    val drop_longitude : Double,
    val payment : Double
)

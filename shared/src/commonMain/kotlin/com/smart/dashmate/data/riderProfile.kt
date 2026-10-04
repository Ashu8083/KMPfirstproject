package com.smart.dashmate.data

import kotlin.uuid.Uuid

data class RiderProfile (
    val riderId : Uuid,
    val riderName : String,
    val longitude : Double,
    val latitude : Double,
) {

}
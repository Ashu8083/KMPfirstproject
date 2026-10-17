package com.smart.dashmate.data.models

import kotlin.uuid.Uuid

data class TripAccept(
    val tripId: Uuid,
    val riderId: Uuid,
)

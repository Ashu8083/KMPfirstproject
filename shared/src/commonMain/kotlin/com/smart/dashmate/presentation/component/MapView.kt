package com.smart.dashmate.presentation.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.smart.dashmate.data.models.Location
import com.smart.dashmate.data.models.RoutePoint


@Composable
expect fun RiderMap(
    modifier: Modifier,
    riderLocation : Location,
    destinationLocation : Location?,
    route: List<RoutePoint>
)
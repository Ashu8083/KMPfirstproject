package com.smart.dashmate.presentation.component

@androidx.compose.runtime.Composable
actual fun RiderMap(
    modifier: androidx.compose.ui.Modifier,
    riderLocation: com.smart.dashmate.data.models.Location,
    destinationLocation: com.smart.dashmate.data.models.Location?,
    route: List<com.smart.dashmate.data.models.RoutePoint>,
    is3D: Boolean,
    mapTheme: String
) {
}
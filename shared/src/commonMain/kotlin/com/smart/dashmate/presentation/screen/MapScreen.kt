package com.smart.dashmate.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.smart.dashmate.data.models.GetRouteRequest
import com.smart.dashmate.data.models.Location
import com.smart.dashmate.data.models.RoutePoint
import com.smart.dashmate.presentation.component.AppForm
import com.smart.dashmate.presentation.component.RiderMap
import com.smart.dashmate.presentation.viewModel.RouteViewModel
import org.koin.compose.viewmodel.koinViewModel
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun MapScreen(
) {
    val routeViewModel: RouteViewModel = koinInject()
    val scope = rememberCoroutineScope()


    // -------------------------
    // Form state
    // -------------------------

    var latitudeText by remember {
        mutableStateOf("")
    }

    var longitudeText by remember {
        mutableStateOf("")
    }

    var destinationLatitude by remember {
        mutableStateOf("")
    }

    var destinationLongitude by remember {
        mutableStateOf("")
    }

    // -------------------------
    // Map state
    // -------------------------

    var riderLocation by remember {
        mutableStateOf<Location?>(null)
    }

    var destinationLocation by remember {
        mutableStateOf<Location?>(null)
    }

    var route by remember {
        mutableStateOf<List<RoutePoint>>(emptyList())
    }


    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Map Screen")


        // =========================
        // RIDER
        // =========================

        AppForm(
            field = "Rider latitude",
            value = latitudeText,
            onValueChange = {
                latitudeText = it
            }
        )

        AppForm(
            field = "Rider longitude",
            value = longitudeText,
            onValueChange = {
                longitudeText = it
            }
        )


        Button(
            onClick = {

                val latitude = latitudeText.toDoubleOrNull()
                val longitude = longitudeText.toDoubleOrNull()

                if (latitude != null && longitude != null) {

                    riderLocation = Location(
                        latitude = latitude,
                        longitude = longitude
                    )
                }
            }
        ) {
            Text("Update Location")
        }


        // =========================
        // DESTINATION
        // =========================

        AppForm(
            field = "Destination latitude",
            value = destinationLatitude,
            onValueChange = {
                destinationLatitude = it
            }
        )

        AppForm(
            field = "Destination longitude",
            value = destinationLongitude,
            onValueChange = {
                destinationLongitude = it
            }
        )


        Button(
            onClick = {

                val startLat = latitudeText.toDoubleOrNull()
                val startLng = longitudeText.toDoubleOrNull()

                val destinationLat =
                    destinationLatitude.toDoubleOrNull()

                val destinationLng =
                    destinationLongitude.toDoubleOrNull()


                if (
                    startLat != null &&
                    startLng != null &&
                    destinationLat != null &&
                    destinationLng != null
                ) {

                    // Set rider location
                    riderLocation = Location(
                        latitude = startLat,
                        longitude = startLng
                    )

                    // Set destination
                    destinationLocation = Location(
                        latitude = destinationLat,
                        longitude = destinationLng
                    )


                    // Create API request
                    val request = GetRouteRequest(
                        startLatitude = startLat,
                        startLongitude = startLng,
                        destinationLatitude = destinationLat,
                        destinationLongitude = destinationLng
                    )


                    // Call ViewModel
                    scope.launch {

                        try {

                            val response =
                                routeViewModel.getRoutes(request)

                            // Get first route
                            val firstRoute =
                                response.routes.firstOrNull()

                            if (firstRoute != null) {

                                // OSRM gives:
                                // [longitude, latitude]

                                route =
                                    firstRoute.geometry.coordinates.mapNotNull {
                                        if (it.size >= 2) {
                                            RoutePoint(
                                                latitude = it[1],
                                                longitude = it[0]
                                            )
                                        } else {
                                            null
                                        }
                                    }
                            }

                        } catch (e: Exception) {

                            println(
                                "Route API Error: ${e.message}"
                            )
                        }
                    }
                }
            }
        ) {
            Text("Get Route")
        }


        // =========================
        // MAP
        // =========================

        riderLocation?.let { location ->

            RiderMap(
                riderLocation = location,
                destinationLocation = destinationLocation,
                route = route,

                modifier = Modifier
                    .fillMaxWidth()
                    .height(600.dp)
            )
        }
    }
}
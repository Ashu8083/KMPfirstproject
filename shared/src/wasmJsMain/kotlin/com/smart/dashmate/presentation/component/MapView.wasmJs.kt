package com.smart.dashmate.presentation.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.HtmlElementView
import com.smart.dashmate.MAP_BOX_PK
import com.smart.dashmate.data.models.Location
import com.smart.dashmate.data.models.RoutePoint
import com.smart.dashmate.map.initializeMap
import com.smart.dashmate.map.setMap2D
import com.smart.dashmate.map.setMap3D
import com.smart.dashmate.map.setMapDawnStyle
import com.smart.dashmate.map.setMapDayStyle
import com.smart.dashmate.map.setMapNightStyle
import com.smart.dashmate.map.updateMapData
import kotlinx.browser.document
import org.w3c.dom.HTMLDivElement

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun RiderMap(
    modifier: Modifier,
    riderLocation: Location,
    destinationLocation: Location?,
    route: List<RoutePoint>,
    is3D: Boolean,
    mapTheme: String
) {

    val mapId = remember { "dashmate-map-${kotlin.random.Random.nextInt(100000)}" }

    LaunchedEffect(is3D) {
        if (is3D) setMap3D() else setMap2D()
    }

    LaunchedEffect(mapTheme) {
        when (mapTheme.uppercase()) {
            "NIGHT" -> setMapNightStyle()
            "DAWN" -> setMapDawnStyle()
            else -> setMapDayStyle()
        }
    }

    HtmlElementView(
        modifier = modifier,
        factory = {
            val mapContainer =
                document.createElement("div") as HTMLDivElement
            mapContainer.style.width = "100%"
            mapContainer.style.height = "100%"
            // Give Mapbox a unique ID
            mapContainer.id = mapId
            mapContainer
        },

        update = {
            // Initialize the map with 3D tilt (pitch = 60°), bearing (-17.6°), and zoom 15.5
            initializeMap(
                containerId = mapId,
                accessToken = MAP_BOX_PK,
                longitude = riderLocation.longitude,
                latitude = riderLocation.latitude,
                zoom = 15.5,
                pitch = if (is3D) 60.0 else 0.0,
                bearing = if (is3D) -17.6 else 0.0
            )

            // Draw/update rider marker, destination marker, and route polyline
            val routePairs = route.map { Pair(it.longitude, it.latitude) }
            updateMapData(
                riderLon = riderLocation.longitude,
                riderLat = riderLocation.latitude,
                destLon = destinationLocation?.longitude,
                destLat = destinationLocation?.latitude,
                routePoints = routePairs
            )
        }
    )
}
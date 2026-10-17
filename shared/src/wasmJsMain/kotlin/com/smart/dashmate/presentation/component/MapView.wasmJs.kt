package com.smart.dashmate.presentation.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.HtmlElementView
import com.smart.dashmate.MAP_BOX_PK
import com.smart.dashmate.data.models.Location
import com.smart.dashmate.data.models.RoutePoint
import com.smart.dashmate.map.destroyMap
import com.smart.dashmate.map.initializeMap
import com.smart.dashmate.map.setMap2D
import com.smart.dashmate.map.setMap3D
import com.smart.dashmate.map.setMapDawnStyle
import com.smart.dashmate.map.setMapDayStyle
import com.smart.dashmate.map.setMapNightStyle
import com.smart.dashmate.map.updateMapData
import kotlinx.browser.document
import org.w3c.dom.HTMLDivElement

@OptIn(ExperimentalComposeUiApi::class, kotlin.js.ExperimentalWasmJsInterop::class)
@Composable
actual fun RiderMap(
    modifier: Modifier,
    riderLocation: Location?,
    destinationLocation: Location?,
    route: List<RoutePoint>,
    is3D: Boolean,
    mapTheme: String
) {
    val mapId = remember {
        "dashmate-map-${kotlin.random.Random.nextInt(100000)}"
    }
    var containerElement by remember { mutableStateOf<HTMLDivElement?>(null) }

    DisposableEffect(mapId) {
        onDispose {
            destroyMap(mapId)
            try {
                containerElement?.remove()
            } catch (e: Throwable) {}
        }
    }

    HtmlElementView(
        modifier = modifier,
        factory = {
            (document.createElement("div") as HTMLDivElement).apply {
                id = mapId
                style.width = "100%"
                style.height = "100%"
                style.position = "relative"
                containerElement = this
            }
        },
        update = {
        }
    )

    LaunchedEffect(containerElement) {
        val el = containerElement ?: return@LaunchedEffect
        val location = riderLocation ?: Location(latitude = 12.9716, longitude = 77.5946)
        initializeMap(
            container = el,
            accessToken = MAP_BOX_PK,
            longitude = location.longitude,
            latitude = location.latitude,
            zoom = 15.5,
            pitch = if (is3D) 60.0 else 0.0,
            bearing = if (is3D) -17.6 else 0.0
        )
    }

    LaunchedEffect(riderLocation, destinationLocation, route) {
        val location = riderLocation ?: return@LaunchedEffect
        val routePairs = route.map {
            Pair(it.longitude, it.latitude)
        }
        updateMapData(
            riderLon = location.longitude,
            riderLat = location.latitude,
            destLon = destinationLocation?.longitude,
            destLat = destinationLocation?.latitude,
            routePoints = routePairs
        )
    }

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
}


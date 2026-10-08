package com.smart.dashmate.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smart.dashmate.data.models.GetRouteRequest
import com.smart.dashmate.data.models.Location
import com.smart.dashmate.data.models.RoutePoint
import com.smart.dashmate.presentation.component.RiderMap
import com.smart.dashmate.presentation.viewModel.RouteViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun MapScreen() {
    val routeViewModel: RouteViewModel = koinInject()
    val scope = rememberCoroutineScope()

    val locations by routeViewModel.locations.collectAsState()

    // Origin state
    var originSearch by remember { mutableStateOf("") }
    var originLatitude by remember { mutableStateOf("") }
    var originLongitude by remember { mutableStateOf("") }

    // Destination state
    var destinationSearch by remember { mutableStateOf("") }
    var destinationLatitude by remember { mutableStateOf("") }
    var destinationLongitude by remember { mutableStateOf("") }

    // Active field tracking for search dropdown ("origin" or "destination")
    var activeSearchField by remember { mutableStateOf<String?>(null) }

    // Map state (defaulting to Bengaluru so map is always active)
    var riderLocation by remember {
        mutableStateOf(Location(latitude = 12.9716, longitude = 77.5946))
    }
    var destinationLocation by remember { mutableStateOf<Location?>(null) }
    var route by remember { mutableStateOf<List<RoutePoint>>(emptyList()) }

    // Map Display Controls
    var is3DView by remember { mutableStateOf(true) }
    var mapTheme by remember { mutableStateOf("DAY") }

    // Loading & Status states
    var isLoadingRoute by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }


    // Search Effects
    LaunchedEffect(originSearch) {
        if (activeSearchField == "origin" && originSearch.length >= 3) {
            delay(400)
            routeViewModel.searchLocation(originSearch)
        } else if (activeSearchField == "origin") {
            routeViewModel.clearLocations()
        }
    }

    LaunchedEffect(destinationSearch) {
        if (activeSearchField == "destination" && destinationSearch.length >= 3) {
            delay(400)
            routeViewModel.searchLocation(destinationSearch)
        } else if (activeSearchField == "destination") {
            routeViewModel.clearLocations()
        }
    }

    Row(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {

        // =====================================================
        // 1. LEFT SIDEBAR PANEL (ROUTE CONTROLS)
        // =====================================================
        Surface(
            modifier = Modifier
                .width(420.dp)
                .fillMaxHeight(),
            shadowElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF007AFF))
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "DashMate Navigation",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // Origin Search Input
                OutlinedTextField(
                    value = originSearch,
                    onValueChange = {
                        originSearch = it
                        activeSearchField = "origin"
                    },
                    label = { Text("Origin / Pickup") },
                    placeholder = { Text("Search location...") },
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF34C759))
                        )
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Origin dropdown results
                if (activeSearchField == "origin" && locations.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        LazyColumn(modifier = Modifier.height(180.dp)) {
                            items(locations) { loc ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            originSearch = loc.display_name
                                            originLatitude = loc.lat
                                            originLongitude = loc.lon
                                            val lat = loc.lat.toDoubleOrNull()
                                            val lon = loc.lon.toDoubleOrNull()
                                            if (lat != null && lon != null) {
                                                riderLocation = Location(lat, lon)
                                            }
                                            activeSearchField = null
                                            routeViewModel.clearLocations()
                                        }
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = loc.display_name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${loc.lat}, ${loc.lon}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                            }
                        }
                    }
                }

                // Destination Search Input
                OutlinedTextField(
                    value = destinationSearch,
                    onValueChange = {
                        destinationSearch = it
                        activeSearchField = "destination"
                    },
                    label = { Text("Destination / Drop-off") },
                    placeholder = { Text("Search location...") },
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFF3B30))
                        )
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Destination dropdown results
                if (activeSearchField == "destination" && locations.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        LazyColumn(modifier = Modifier.height(180.dp)) {
                            items(locations) { loc ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            destinationSearch = loc.display_name
                                            destinationLatitude = loc.lat
                                            destinationLongitude = loc.lon
                                            val lat = loc.lat.toDoubleOrNull()
                                            val lon = loc.lon.toDoubleOrNull()
                                            if (lat != null && lon != null) {
                                                destinationLocation = Location(lat, lon)
                                            }
                                            activeSearchField = null
                                            routeViewModel.clearLocations()
                                        }
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = loc.display_name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${loc.lat}, ${loc.lon}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                            }
                        }
                    }
                }

                // CTA Button: Get Route
                Button(
                    onClick = {
                        val startLat = originLatitude.toDoubleOrNull()
                        val startLng = originLongitude.toDoubleOrNull()
                        val destLat = destinationLatitude.toDoubleOrNull()
                        val destLng = destinationLongitude.toDoubleOrNull()

                        if (startLat == null || startLng == null || destLat == null || destLng == null) {
                            statusMessage = "⚠️ Please select both origin and destination from search dropdown!"
                            return@Button
                        }

                        riderLocation = Location(latitude = startLat, longitude = startLng)
                        destinationLocation = Location(latitude = destLat, longitude = destLng)

                        isLoadingRoute = true
                        statusMessage = "🔄 Calculating optimal route..."

                        val request = GetRouteRequest(
                            startLatitude = startLat,
                            startLongitude = startLng,
                            destinationLatitude = destLat,
                            destinationLongitude = destLng
                        )

                        scope.launch {
                            try {
                                val response = routeViewModel.getRoutes(request)
                                val firstRoute = response.routes.firstOrNull()

                                if (firstRoute != null) {
                                    route = firstRoute.geometry.coordinates.mapNotNull { coord ->
                                        if (coord.size >= 2) {
                                            RoutePoint(latitude = coord[1], longitude = coord[0])
                                        } else null
                                    }
                                    statusMessage = "✅ Route calculated successfully! (${route.size} waypoints)"
                                } else {
                                    statusMessage = "❌ No route found between selected points."
                                }
                            } catch (e: Exception) {
                                statusMessage = "❌ Route API Error: ${e.message}"
                            } finally {
                                isLoadingRoute = false
                            }
                        }
                    },
                    enabled = !isLoadingRoute,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF007AFF)
                    )
                ) {
                    if (isLoadingRoute) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Calculating Route...", color = Color.White)
                    } else {
                        Text("Get Route", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                    }
                }

                // Status Message / Error Banner
                statusMessage?.let { msg ->
                    Surface(
                        color = when {
                            msg.startsWith("✅") -> Color(0xFFE8F5E9)
                            msg.startsWith("⚠️") -> Color(0xFFFFF8E1)
                            msg.startsWith("❌") -> Color(0xFFFFEBEE)
                            else -> Color(0xFFE3F2FD)
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = msg,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = Color.DarkGray
                        )
                    }
                }

                // Map Display Controls Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Map View Controls",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        // Perspective selector (2D vs 3D)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { is3DView = false },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (!is3DView) Color(0xFF007AFF) else MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Text("🗺️ 2D Flat", color = if (!is3DView) Color.White else MaterialTheme.colorScheme.onSurface)
                            }
                            Button(
                                onClick = { is3DView = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (is3DView) Color(0xFF007AFF) else MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Text("🏙️ 3D Tilt", color = if (is3DView) Color.White else MaterialTheme.colorScheme.onSurface)
                            }
                        }

                        // Theme selector (Day, Night, Dawn)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Button(
                                onClick = { mapTheme = "DAY" },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (mapTheme == "DAY") Color(0xFF007AFF) else MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Text("☀️ Day", fontSize = 12.sp, color = if (mapTheme == "DAY") Color.White else MaterialTheme.colorScheme.onSurface)
                            }
                            Button(
                                onClick = { mapTheme = "NIGHT" },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (mapTheme == "NIGHT") Color(0xFF1C1C1E) else MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Text("🌙 Night", fontSize = 12.sp, color = if (mapTheme == "NIGHT") Color.White else MaterialTheme.colorScheme.onSurface)
                            }
                            Button(
                                onClick = { mapTheme = "DAWN" },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (mapTheme == "DAWN") Color(0xFFFF9500) else MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Text("🌅 Dawn", fontSize = 12.sp, color = if (mapTheme == "DAWN") Color.White else MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }

                // Route Info Card (When route is active)
                if (route.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Active Route Summary",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF007AFF)
                            )
                            Text(
                                text = "Waypoints: ${route.size} coordinates",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Button(
                                onClick = {
                                    route = emptyList()
                                    destinationLocation = null
                                    statusMessage = null
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFF3B30)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Clear Route", color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // =====================================================
        // 2. RIGHT PANEL (DEDICATED FULL HEIGHT MAP DISPLAY)
        // =====================================================
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            RiderMap(
                riderLocation = riderLocation,
                destinationLocation = destinationLocation,
                route = route,
                is3D = is3DView,
                mapTheme = mapTheme,
                modifier = Modifier.fillMaxSize()
            )

            // Floating Map Controls Overlay
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { is3DView = !is3DView },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (is3DView) Color(0xFF007AFF) else Color(0xFF8E8E93)
                        )
                    ) {
                        Text(if (is3DView) "🏙️ 3D View" else "🗺️ 2D View", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { mapTheme = "DAY" },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (mapTheme == "DAY") Color(0xFF007AFF) else MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text("☀️ Day", color = if (mapTheme == "DAY") Color.White else MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Button(
                        onClick = { mapTheme = "NIGHT" },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (mapTheme == "NIGHT") Color(0xFF1C1C1E) else MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text("🌙 Night", color = Color.White)
                    }

                    Button(
                        onClick = { mapTheme = "DAWN" },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (mapTheme == "DAWN") Color(0xFFFF9500) else MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text("🌅 Dawn", color = if (mapTheme == "DAWN") Color.White else MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }

}
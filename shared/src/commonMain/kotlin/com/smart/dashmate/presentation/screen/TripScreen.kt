 package com.smart.dashmate.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smart.dashmate.data.models.Location
import com.smart.dashmate.data.models.RoutePoint
import com.smart.dashmate.presentation.component.RiderMap

@Composable
fun TripScreen(
    onBack: () -> Unit = {}
) {

    var riderLocation by remember {
        mutableStateOf<Location?>(null)
    }

    var pickUpAddress by remember {
        mutableStateOf<String?>(null)
    }

    var dropOffAddress by remember {
        mutableStateOf<String?>(null)
    }

    var destinationLocation by remember {
        mutableStateOf<Location?>(null)
    }

    var route by remember {
        mutableStateOf<List<RoutePoint>>(emptyList())
    }

    var is3DView by remember { mutableStateOf(true) }
    var mapTheme by remember { mutableStateOf("DAY") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Screen title & navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onBack,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("← Back")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Trip Screen",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                color = Color.Black
            )
        }

        // Pickup and drop-off information
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1E293B))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Pickup
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = pickUpAddress ?: "Pick Up location not set" ,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    color = Color.White
                )

                Text(
                    text = "Pickup Address",
                    fontSize = 16.sp,
                    color = Color.White
                )
            }

            // Drop-off
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Drop Location",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    color = Color.White
                )

                Text(
                    text = "Drop Address",
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        // Map fills the remaining available space
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            RiderMap(
                riderLocation = riderLocation,
                destinationLocation = destinationLocation,
                route = route,
                is3D = is3DView,
                mapTheme = mapTheme,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun TripScreenPreview() {
    MaterialTheme {
        TripScreen()
    }
}
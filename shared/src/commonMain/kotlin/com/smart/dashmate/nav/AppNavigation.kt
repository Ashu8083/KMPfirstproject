package com.smart.dashmate.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smart.dashmate.presentation.screen.MapScreen
import com.smart.dashmate.presentation.screen.RiderScreen
import com.smart.dashmate.presentation.screen.WebSocketTestScreen


@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "riderScreen") {
        composable("riderScreen") {
            RiderScreen(onLoginSuccess = {
                    navController.navigate("map")
                },
                    onWebSocketTest = {
                    navController.navigate("webSocketTest")
                }
            )
        }
        composable("map") {
            MapScreen()
        }

        composable("webSocketTest") {
            WebSocketTestScreen()
        }
    }
}


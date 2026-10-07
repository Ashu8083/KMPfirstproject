package com.smart.dashmate.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smart.dashmate.presentation.screen.MapScreen
import com.smart.dashmate.presentation.screen.RiderScreen


@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "riderScreen") {
        composable("riderScreen") {
            RiderScreen(
                onLoginSuccess = {
                    navController.navigate("map")
                }
            )
        }
        composable("map") {
            MapScreen()
        }
    }
}


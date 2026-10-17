package com.smart.dashmate.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.smart.dashmate.presentation.screen.MapScreen
import com.smart.dashmate.presentation.screen.RiderScreen
import com.smart.dashmate.presentation.screen.TripScreen

@Composable
fun AppNavigation() {
    var screenStack by remember { mutableStateOf(listOf("riderScreen")) }
    val currentScreen = screenStack.lastOrNull() ?: "riderScreen"

    fun navigateTo(route: String) {
        screenStack = screenStack + route
    }

    fun goBack() {
        if (screenStack.size > 1) {
            screenStack = screenStack.dropLast(1)
        } else {
            screenStack = listOf("riderScreen")
        }
    }

    when (currentScreen) {
        "riderScreen" -> {
            RiderScreen(
                onLoginSuccess = {
                    navigateTo("map")
                },
                onSuccess = {
                    navigateTo("trip")
                }
            )
        }
        "map" -> {
            MapScreen(
                onBack = {
                    goBack()
                }
            )
        }
        "trip" -> {
            TripScreen(
                onBack = {
                    goBack()
                }
            )
        }
    }
}


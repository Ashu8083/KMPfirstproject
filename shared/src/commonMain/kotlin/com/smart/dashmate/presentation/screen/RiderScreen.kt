package com.smart.dashmate.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.tooling.ComposeToolingApi
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.smart.dashmate.presentation.component.AppButton
import com.smart.dashmate.presentation.component.AppForm
import com.smart.dashmate.presentation.viewModel.RiderViewModel
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RiderScreen(
    onLoginSuccess: () -> Unit,
) {

    var viewModel: RiderViewModel = koinViewModel()
    var riderId by remember {
        mutableStateOf("")
    }
    val scope = rememberCoroutineScope()
    val message by viewModel.incomingMessages.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        AppForm(
            field = "Rider ID",
            value = riderId,
            onValueChange = {
                riderId = it
            }
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        AppButton(
            text = "Login",
            onClick = {
                onLoginSuccess()
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        AppButton(
            text = "Connect WebSocket",
            onClick = {
                if (riderId.isNotBlank()) {
                    viewModel.connectToWebSocket(riderId.trim())
                } else {
                    println("Please enter a Rider ID")
                }
            }
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Button(onClick = {
            scope.launch {
                viewModel.sendLocationUpdate()
            }
        }

        ) {
            Text("Send Location")
        }

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text("DashMate Rider")
            Text(
                text = message?.type ?: "Waiting for messages..."
            )
            Text(
                text = message?.payload?.toString()
                    ?: "No message received yet"
            )
        }


    }
}
package com.smart.dashmate.presentation.screen

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import com.smart.dashmate.data.remote.WebSocketService
import kotlin.uuid.Uuid

@Composable
fun WebSocketTestScreen() {

    val webSocketService: WebSocketService = koinInject()
    val scope = rememberCoroutineScope()

    Button(
        onClick = {
            scope.launch {
                webSocketService.riderWebSocketConnection(
                    Uuid.parse(
                        "60be7bb4-a174-4342-a8df-1c3d364b0c2b"
                    )
                )
            }
        }
    ) {
        Text("Connect WebSocket")
    }
}
package com.smart.dashmate.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.tooling.ComposeToolingApi
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.smart.dashmate.presentation.component.AppButton
import com.smart.dashmate.presentation.component.AppForm
import com.smart.dashmate.presentation.viewModel.RiderViewModel

@Composable
fun RiderScreen(
    onLoginSuccess: () -> Unit,
    onWebSocketTest: () -> Unit
) {
    var riderId by remember {
        mutableStateOf("")
    }

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

        AppButton(
            text = "WebScoketScreen",
            onClick = {
                onWebSocketTest()
            }
        )
    }
}
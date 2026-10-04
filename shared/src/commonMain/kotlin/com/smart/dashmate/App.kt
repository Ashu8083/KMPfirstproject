package com.smart.dashmate

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smart.dashmate.nav.AppNavigation
import org.jetbrains.compose.resources.painterResource

import dashmate.shared.generated.resources.Res
import dashmate.shared.generated.resources.ashu
import dashmate.shared.generated.resources.compose_multiplatform
import dashmate.shared.generated.resources.priti
import dashmate.shared.generated.resources.sonali

@Composable
@Preview
fun App() {
    MaterialTheme {
        AppNavigation()
    }
}


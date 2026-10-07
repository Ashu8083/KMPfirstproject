package com.smart.dashmate

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.smart.dashmate.di.appModule
import com.smart.dashmate.nav.AppNavigation
import org.koin.compose.KoinApplication

@Composable
@Preview
fun App() {
    KoinApplication(
        application = {
            modules(appModule)
        }
    ) {

        MaterialTheme {
            AppNavigation()
        }
    }
}
package com.smart.dashmate

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    document.querySelector("svg")?.remove()
    ComposeViewport {
        App()
    }
}
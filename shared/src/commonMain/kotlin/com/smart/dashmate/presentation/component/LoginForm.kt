package com.smart.dashmate.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun AppForm(
    field: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = field)

        TextField(
            value = value,
            onValueChange = onValueChange,
            label = {
                Text("Rider ID")
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit
)
{
    Button(
        onClick = onClick,
        modifier = Modifier.width(200.dp)
    ) {
        Text(text = text)
    }
}


@Preview(showBackground = true)
@Composable
fun LoginFormPreview() {

    var riderId by remember {
        mutableStateOf("")
    }

    AppForm(
        field = "Rider ID",
        value = riderId,
        onValueChange = {
            riderId = it
        }
    )
}
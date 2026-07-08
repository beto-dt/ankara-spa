package dev.luisdelatorre.ankaraspa.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun SuccessScreen(code: String, onDone: () -> Unit) {
    Text("Success: $code")
}
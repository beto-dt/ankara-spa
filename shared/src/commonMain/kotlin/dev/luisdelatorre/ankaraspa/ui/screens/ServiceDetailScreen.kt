package dev.luisdelatorre.ankaraspa.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ServiceDetailScreen(serviceId: String, onBook: (String) -> Unit, onBack: () -> Unit) {
    Text("ServiceDetail: $serviceId")
}
package dev.luisdelatorre.ankaraspa.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun BookingScreen(serviceId: String, onSuccess: (String) -> Unit, onBack: () -> Unit) {
    Text("Booking: $serviceId")
}
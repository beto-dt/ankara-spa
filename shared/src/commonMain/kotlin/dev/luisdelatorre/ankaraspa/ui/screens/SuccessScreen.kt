package dev.luisdelatorre.ankaraspa.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.luisdelatorre.ankaraspa.theme.AnkaraColors

@Composable
fun SuccessScreen(code: String, onDone: () -> Unit) {
    Column(
        Modifier.fillMaxSize().background(AnkaraColors.Cream).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Tu cita está confirmada", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(16.dp))
        Text(code, style = MaterialTheme.typography.headlineMedium, color = AnkaraColors.Sage)
        Spacer(Modifier.height(8.dp))
        Text(
            "Guarda este código — es tu comprobante.",
            style = MaterialTheme.typography.bodyMedium,
            color = AnkaraColors.InkSoft,
        )
        Spacer(Modifier.height(32.dp))
        Button(onClick = onDone, colors = ButtonDefaults.buttonColors(containerColor = AnkaraColors.Sage)) {
            Text("Volver al inicio")
        }
    }
}
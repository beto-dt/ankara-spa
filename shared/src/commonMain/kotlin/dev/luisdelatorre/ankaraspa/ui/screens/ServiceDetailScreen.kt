package dev.luisdelatorre.ankaraspa.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.luisdelatorre.ankaraspa.data.Service
import dev.luisdelatorre.ankaraspa.data.SpaApi
import dev.luisdelatorre.ankaraspa.theme.AnkaraColors

@Composable
fun ServiceDetailScreen(serviceId: String, onBook: (String) -> Unit, onBack: () -> Unit) {
    var service by remember { mutableStateOf<Service?>(null) }

    LaunchedEffect(serviceId) {
        service = runCatching { SpaApi().catalog().services.first { it.id == serviceId } }.getOrNull()
    }

    val s = service
    if (s == null) {
        Box(Modifier.fillMaxSize().background(AnkaraColors.Cream), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = AnkaraColors.Sage)
        }
        return
    }

    Column(Modifier.fillMaxSize().background(AnkaraColors.Cream).padding(24.dp)) {
        Text(
            "← Volver",
            color = AnkaraColors.Sage,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.clickableText(onBack),
        )
        Spacer(Modifier.height(20.dp))
        Text(s.name.es, style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            "${s.durationMin} min · $${s.price}",
            style = MaterialTheme.typography.labelMedium,
            color = AnkaraColors.Wood,
        )
        Spacer(Modifier.height(16.dp))
        Text(s.description.es, style = MaterialTheme.typography.bodyMedium, color = AnkaraColors.InkSoft)
        Spacer(Modifier.weight(1f))
        Button(
            onClick = { onBook(s.id) },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AnkaraColors.Sage),
        ) { Text("Reservar", style = MaterialTheme.typography.labelMedium) }
    }
}
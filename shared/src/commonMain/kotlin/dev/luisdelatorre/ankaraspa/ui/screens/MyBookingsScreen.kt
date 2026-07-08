package dev.luisdelatorre.ankaraspa.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.luisdelatorre.ankaraspa.data.Booking
import dev.luisdelatorre.ankaraspa.data.Service
import dev.luisdelatorre.ankaraspa.data.Session
import dev.luisdelatorre.ankaraspa.data.SpaApi
import dev.luisdelatorre.ankaraspa.theme.AnkaraColors
import kotlinx.coroutines.launch

@Composable
fun MyBookingsScreen(onBack: () -> Unit) {
    val api = remember { SpaApi() }
    val scope = rememberCoroutineScope()
    var bookings by remember { mutableStateOf<List<Booking>?>(null) }
    var services by remember { mutableStateOf<Map<String, Service>>(emptyMap()) }
    var confirmingId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        services = runCatching { api.catalog().services.associateBy { it.id } }.getOrDefault(emptyMap())
        bookings = runCatching { api.myBookings(Session.clientUid) }.getOrNull() ?: emptyList()
    }

    Column(Modifier.fillMaxSize().background(AnkaraColors.Cream).padding(24.dp)) {
        Text(
            "← Volver",
            color = AnkaraColors.Sage,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.clickableText(onBack),
        )
        Spacer(Modifier.height(20.dp))
        Text("Mis citas", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(20.dp))

        val current = bookings
        when {
            current == null -> CircularProgressIndicator(color = AnkaraColors.Sage)
            current.isEmpty() -> Text("No tienes citas próximas.", color = AnkaraColors.InkSoft)
            else -> LazyColumn(
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(current, key = { it.id }) { booking ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = AnkaraColors.Surface),
                        border = BorderStroke(1.dp, AnkaraColors.Line),
                    ) {
                        Column(Modifier.padding(20.dp)) {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    services[booking.serviceId]?.name?.es ?: booking.serviceId,
                                    style = MaterialTheme.typography.titleMedium,
                                )
                                Text(booking.code, style = MaterialTheme.typography.labelMedium, color = AnkaraColors.Wood)
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "${booking.date} · ${booking.time}:00 · con ${booking.staffName}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = AnkaraColors.InkSoft,
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                if (confirmingId == booking.id) "¿Confirmas? Toca de nuevo para cancelar" else "Cancelar cita",
                                color = if (confirmingId == booking.id) AnkaraColors.Wood else AnkaraColors.InkSoft,
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.clickableText {
                                    if (confirmingId != booking.id) {
                                        confirmingId = booking.id
                                    } else {
                                        scope.launch {
                                            runCatching { api.cancelBooking(booking.id, Session.clientUid) }
                                            bookings = runCatching { api.myBookings(Session.clientUid) }.getOrNull() ?: emptyList()
                                            confirmingId = null
                                        }
                                    }
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}
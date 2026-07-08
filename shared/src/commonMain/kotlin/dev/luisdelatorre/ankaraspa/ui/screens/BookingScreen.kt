package dev.luisdelatorre.ankaraspa.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.luisdelatorre.ankaraspa.data.CreateBookingRequest
import dev.luisdelatorre.ankaraspa.data.Session
import dev.luisdelatorre.ankaraspa.data.Slot
import dev.luisdelatorre.ankaraspa.data.SlotTakenException
import dev.luisdelatorre.ankaraspa.data.SpaApi
import dev.luisdelatorre.ankaraspa.data.nextDays
import dev.luisdelatorre.ankaraspa.theme.AnkaraColors
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek

private val DAY_ES = mapOf(
    DayOfWeek.MONDAY to "lun", DayOfWeek.TUESDAY to "mar", DayOfWeek.WEDNESDAY to "mié",
    DayOfWeek.THURSDAY to "jue", DayOfWeek.FRIDAY to "vie", DayOfWeek.SATURDAY to "sáb",
    DayOfWeek.SUNDAY to "dom",
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BookingScreen(serviceId: String, onSuccess: (String) -> Unit, onBack: () -> Unit) {
    val api = remember { SpaApi() }
    val scope = rememberCoroutineScope()
    val days = remember { nextDays(7) }
    var selectedDay by remember { mutableStateOf(days.first()) }
    var slots by remember { mutableStateOf<List<Slot>?>(null) }
    var selectedTime by remember { mutableStateOf<Int?>(null) }
    var name by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(selectedDay) {
        slots = null
        selectedTime = null
        slots = runCatching { api.availability(serviceId, selectedDay.toString()).slots }.getOrNull() ?: emptyList()
    }

    Column(Modifier.fillMaxSize().background(AnkaraColors.Cream).padding(24.dp)) {
        Text(
            "← Volver",
            color = AnkaraColors.Sage,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.clickableText(onBack),
        )
        Spacer(Modifier.height(20.dp))
        Text("Elige tu horario", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(20.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(days) { day ->
                FilterChip(
                    selected = day == selectedDay,
                    onClick = { selectedDay = day },
                    label = { Text("${DAY_ES[day.dayOfWeek]} ${day.day}") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AnkaraColors.Sage,
                        selectedLabelColor = AnkaraColors.Cream,
                    ),
                )
            }
        }
        Spacer(Modifier.height(20.dp))

        val currentSlots = slots
        when {
            currentSlots == null -> CircularProgressIndicator(color = AnkaraColors.Sage)
            currentSlots.isEmpty() -> Text("No hay horarios libres este día.", color = AnkaraColors.InkSoft)
            else -> FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                currentSlots.forEach { slot ->
                    FilterChip(
                        selected = slot.time == selectedTime,
                        onClick = { selectedTime = slot.time },
                        label = { Text("${slot.time}:00") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AnkaraColors.Wood,
                            selectedLabelColor = AnkaraColors.Cream,
                        ),
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it.take(60) },
            label = { Text("Tu nombre (opcional)") },
            modifier = Modifier.fillMaxWidth(),
        )
        error?.let {
            Spacer(Modifier.height(10.dp))
            Text(it, color = AnkaraColors.Wood, style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(Modifier.weight(1f))
        Button(
            onClick = {
                val time = selectedTime ?: return@Button
                busy = true
                error = null
                scope.launch {
                    try {
                        val booking = api.createBooking(
                            CreateBookingRequest(
                                serviceId = serviceId,
                                date = selectedDay.toString(),
                                time = time,
                                clientUid = Session.clientUid,
                                clientName = name.ifBlank { null },
                            )
                        )
                        onSuccess(booking.code)
                    } catch (e: SlotTakenException) {
                        error = "Ese horario se acaba de ocupar — elige otro."
                        slots = runCatching { api.availability(serviceId, selectedDay.toString()).slots }.getOrNull() ?: emptyList()
                        selectedTime = null
                    } catch (e: Exception) {
                        error = "No pudimos reservar. Intenta de nuevo."
                    } finally {
                        busy = false
                    }
                }
            },
            enabled = selectedTime != null && !busy,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AnkaraColors.Sage),
        ) { Text(if (busy) "Reservando…" else "Confirmar reserva", style = MaterialTheme.typography.labelMedium) }
    }
}
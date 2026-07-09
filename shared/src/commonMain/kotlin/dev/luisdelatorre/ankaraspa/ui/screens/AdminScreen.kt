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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import dev.luisdelatorre.ankaraspa.data.AdminApi
import dev.luisdelatorre.ankaraspa.data.AgendaBooking
import dev.luisdelatorre.ankaraspa.theme.AnkaraColors
import kotlinx.coroutines.launch

@Composable
fun AdminScreen(onBack: () -> Unit) {
    val api = remember { AdminApi() }
    var token by remember { mutableStateOf<String?>(null) }
    if (token == null) {
        AdminLogin(api, onBack) { token = it }
    } else {
        AdminAgenda(api, token!!, onBack)
    }
}

@Composable
private fun AdminLogin(api: AdminApi, onBack: () -> Unit, onLogged: (String) -> Unit) {
    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize().background(AnkaraColors.Cream).padding(24.dp)) {
        Text("← Volver", color = AnkaraColors.Sage, style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.clickableText(onBack))
        Spacer(Modifier.height(20.dp))
        Text("Admin", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(20.dp))
        OutlinedTextField(email, { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            password, { password = it }, label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
        )
        error?.let {
            Spacer(Modifier.height(10.dp))
            Text(it, color = AnkaraColors.Wood, style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = {
                busy = true; error = null
                scope.launch {
                    try {
                        onLogged(api.login(email.trim(), password))
                    } catch (e: Exception) {
                        error = "Login error: ${e::class.simpleName} — ${e.message?.take(200)}"
                    } finally {
                        busy = false
                    }
                }
            },
            enabled = email.isNotBlank() && password.isNotBlank() && !busy,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AnkaraColors.Sage),
        ) { Text(if (busy) "Entrando…" else "Entrar") }
    }
}

@Composable
private fun AdminAgenda(api: AdminApi, token: String, onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var bookings by remember { mutableStateOf<List<AgendaBooking>?>(null) }
    var date by remember { mutableStateOf("") }

    suspend fun reload() {
        val agenda = api.agenda(token)
        date = agenda.date
        bookings = agenda.bookings
    }
    LaunchedEffect(Unit) { runCatching { reload() } }

    Column(Modifier.fillMaxSize().background(AnkaraColors.Cream).padding(24.dp)) {
        Text("← Volver", color = AnkaraColors.Sage, style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.clickableText(onBack))
        Spacer(Modifier.height(20.dp))
        Text("Agenda de hoy", style = MaterialTheme.typography.headlineMedium)
        Text(date, style = MaterialTheme.typography.bodyMedium, color = AnkaraColors.InkSoft)
        Spacer(Modifier.height(20.dp))

        val current = bookings
        when {
            current == null -> CircularProgressIndicator(color = AnkaraColors.Sage)
            current.isEmpty() -> Text("Sin citas hoy.", color = AnkaraColors.InkSoft)
            else -> LazyColumn(
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(current, key = { it.id }) { b ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = AnkaraColors.Surface),
                        border = BorderStroke(1.dp, AnkaraColors.Line),
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                "${b.time}:00 · ${b.staffName} · ${b.clientName ?: "(sin nombre)"} · ${b.code}",
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Text(b.status, style = MaterialTheme.typography.labelMedium, color = AnkaraColors.Wood)
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                TextButton(onClick = {
                                    scope.launch { runCatching { api.updateStatus(token, b.id, "completed"); reload() } }
                                }) { Text("Completada", color = AnkaraColors.Sage) }
                                TextButton(onClick = {
                                    scope.launch { runCatching { api.updateStatus(token, b.id, "no_show"); reload() } }
                                }) { Text("No-show", color = AnkaraColors.Wood) }
                            }
                        }
                    }
                }
            }
        }
    }
}
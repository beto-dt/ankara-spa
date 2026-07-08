package dev.luisdelatorre.ankaraspa.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.luisdelatorre.ankaraspa.data.Catalog
import dev.luisdelatorre.ankaraspa.data.SpaApi
import dev.luisdelatorre.ankaraspa.theme.AnkaraColors

@Composable
fun HomeScreen(onServiceClick: (String) -> Unit, onMyBookings: () -> Unit) {
    var catalog by remember { mutableStateOf<Catalog?>(null) }
    var failed by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        catalog = runCatching { SpaApi().catalog() }
            .onFailure { failed = true }
            .getOrNull()
    }

    when {
        catalog == null && !failed -> {
            Box(Modifier.fillMaxSize().background(AnkaraColors.Cream), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AnkaraColors.Sage)
            }
        }
        failed -> {
            Box(Modifier.fillMaxSize().background(AnkaraColors.Cream), contentAlignment = Alignment.Center) {
                Text("No pudimos cargar los servicios. Intenta de nuevo.", style = MaterialTheme.typography.bodyMedium)
            }
        }
        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize().background(AnkaraColors.Cream),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom,
                        ) {
                            Column {
                                Text("ANKARA", style = MaterialTheme.typography.headlineMedium)
                                Text(
                                    "sauna & masajes",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = AnkaraColors.InkSoft,
                                )
                            }
                            Text(
                                "Mis citas →",
                                style = MaterialTheme.typography.labelMedium,
                                color = AnkaraColors.Sage,
                                modifier = Modifier.clickable { onMyBookings() },
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                    }
                }
                items(catalog!!.services, key = { it.id }) { service ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { onServiceClick(service.id) },
                        colors = CardDefaults.cardColors(containerColor = AnkaraColors.Surface),
                        border = BorderStroke(1.dp, AnkaraColors.Line),
                    ) {
                        Column(Modifier.padding(20.dp)) {
                            Text(service.name.es, style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "${service.durationMin} min · $${service.price} — ${service.description.es}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = AnkaraColors.InkSoft,
                            )
                        }
                    }
                }
            }
        }
    }
}
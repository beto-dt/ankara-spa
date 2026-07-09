package dev.luisdelatorre.ankaraspa.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.luisdelatorre.ankaraspa.data.AdminApi
import dev.luisdelatorre.ankaraspa.data.Catalog
import dev.luisdelatorre.ankaraspa.data.MetricsResponse
import dev.luisdelatorre.ankaraspa.data.SpaApi
import dev.luisdelatorre.ankaraspa.theme.AnkaraColors

@Composable
fun AdminMetrics(api: AdminApi, token: String) {
    var metrics by remember { mutableStateOf<MetricsResponse?>(null) }
    var catalog by remember { mutableStateOf<Catalog?>(null) }

    LaunchedEffect(Unit) {
        runCatching {
            metrics = api.metrics(token)
            catalog = SpaApi().catalog()
        }
    }

    val m = metrics ?: run {
        CircularProgressIndicator(color = AnkaraColors.Sage)
        return
    }
    val serviceNames = catalog?.services?.associate { it.id to it.name.es } ?: emptyMap()

    val total = m.byDay.sumOf { it.count }
    val completed = m.statuses["completed"] ?: 0
    val noShow = m.statuses["no_show"] ?: 0
    val attended = completed + noShow
    val noShowRate = if (attended == 0) 0 else (noShow * 100) / attended

    Column(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatTile("$total", "citas · 14 días", Modifier.weight(1f))
            StatTile("$completed", "completadas", Modifier.weight(1f))
            StatTile("$noShowRate%", "no-show", Modifier.weight(1f))
        }
        Spacer(Modifier.height(28.dp))

        Text("Reservas por día", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(10.dp))
        DailyBarChart(
            values = m.byDay.map { it.count },
            firstLabel = m.start.drop(5),
            lastLabel = m.end.drop(5),
        )
        Spacer(Modifier.height(28.dp))

        Text("Servicios más pedidos", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(12.dp))
        val maxService = m.byService.maxOfOrNull { it.count } ?: 0
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            m.byService.take(5).forEach { s ->
                HBar(serviceNames[s.serviceId] ?: s.serviceId, s.count, maxService)
            }
        }
        Spacer(Modifier.height(28.dp))

        Text("Por terapeuta", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(12.dp))
        val maxStaff = m.byStaff.maxOfOrNull { it.count } ?: 0
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            m.byStaff.forEach { s ->
                HBar(s.name, s.count, maxStaff, color = AnkaraColors.Wood)
            }
        }
    }
}
package dev.luisdelatorre.ankaraspa
import AnkaraTheme
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.luisdelatorre.ankaraspa.theme.AnkaraColors

@Composable
fun App() {
    AnkaraTheme {
        val services = listOf(
            "Sauna finlandés" to "60 min · calor seco y descanso profundo",
            "Masaje relajante" to "60 min · tensión fuera, calma dentro",
            "Piedras calientes" to "75 min · el clásico que nunca falla",
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(AnkaraColors.Cream),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Column {
                    Text("ANKARA", style = MaterialTheme.typography.headlineMedium)
                    Text(
                        "sauna & masajes",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AnkaraColors.InkSoft,
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }
            items(services) { (name, detail) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = AnkaraColors.Surface),
                    border = BorderStroke(1.dp, AnkaraColors.Line),
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Text(name, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            detail,
                            style = MaterialTheme.typography.bodyMedium,
                            color = AnkaraColors.InkSoft,
                        )
                    }
                }
            }
        }
    }
}
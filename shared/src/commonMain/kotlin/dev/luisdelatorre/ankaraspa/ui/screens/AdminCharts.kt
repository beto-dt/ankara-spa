package dev.luisdelatorre.ankaraspa.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.luisdelatorre.ankaraspa.theme.AnkaraColors

/** Hero number tile: big value + quiet caption. Numbers beat charts for single stats. */
@Composable
fun StatTile(value: String, caption: String, modifier: Modifier = Modifier) {
    Column(
        modifier
            .background(AnkaraColors.Surface)
            .padding(16.dp),
    ) {
        Text(value, style = MaterialTheme.typography.headlineMedium, color = AnkaraColors.Sage)
        Text(caption, style = MaterialTheme.typography.labelMedium, color = AnkaraColors.InkSoft)
    }
}

/**
 * Vertical bar chart drawn by hand: thin bars, rounded tops anchored to the
 * baseline, 2dp gaps, value label only on the peak (selective labeling).
 */
@Composable
fun DailyBarChart(values: List<Int>, firstLabel: String, lastLabel: String) {
    val max = (values.maxOrNull() ?: 0).coerceAtLeast(1)
    Column(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Text("máx $max", style = MaterialTheme.typography.labelMedium, color = AnkaraColors.InkSoft)
        }
        Spacer(Modifier.height(6.dp))
        Canvas(Modifier.fillMaxWidth().height(120.dp)) {
            val gap = 2.dp.toPx()
            val barW = (size.width - gap * (values.size - 1)) / values.size
            val radius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
            // Baseline hairline (recessive axis).
            drawRect(
                color = Color(0xFFE8E0D4),
                topLeft = Offset(0f, size.height - 1f),
                size = Size(size.width, 1f),
            )
            values.forEachIndexed { i, v ->
                if (v == 0) return@forEachIndexed
                val h = (v.toFloat() / max) * (size.height - 8f)
                drawRoundRect(
                    color = if (v == max) Color(0xFF46543D) else Color(0xFF5D7052),
                    topLeft = Offset(i * (barW + gap), size.height - h),
                    size = Size(barW, h),
                    cornerRadius = radius,
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(firstLabel, style = MaterialTheme.typography.labelMedium, color = AnkaraColors.InkSoft)
            Text(lastLabel, style = MaterialTheme.typography.labelMedium, color = AnkaraColors.InkSoft)
        }
    }
}

/** Horizontal bar with direct label: name in ink, count at the end, single hue. */
@Composable
fun HBar(label: String, count: Int, max: Int, color: Color = AnkaraColors.Sage) {
    val fraction = if (max == 0) 0f else count.toFloat() / max
    Column(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodyMedium, color = AnkaraColors.Ink)
            Text("$count", style = MaterialTheme.typography.labelMedium, color = AnkaraColors.InkSoft)
        }
        Spacer(Modifier.height(4.dp))
        Box(Modifier.fillMaxWidth().height(8.dp).background(Color(0xFFEFE9DF))) {
            Box(
                Modifier
                    .fillMaxWidth(fraction)
                    .height(8.dp)
                    .background(color),
            )
        }
    }
}
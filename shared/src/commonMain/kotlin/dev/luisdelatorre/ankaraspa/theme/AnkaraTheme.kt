import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import dev.luisdelatorre.ankaraspa.theme.AnkaraColors
import dev.luisdelatorre.ankaraspa.theme.ankaraTypography

@Composable
fun AnkaraTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = AnkaraColors.Sage,
            onPrimary = Color.White,
            background = AnkaraColors.Cream,
            surface = AnkaraColors.Surface,
            onBackground = AnkaraColors.Ink,
            onSurface = AnkaraColors.Ink,
            outline = AnkaraColors.Line,
        ),
        typography = ankaraTypography(),
        content = content,
    )
}
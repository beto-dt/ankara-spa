package dev.luisdelatorre.ankaraspa.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import ankaraspa.shared.generated.resources.Res
import ankaraspa.shared.generated.resources.fraunces_semibold
import ankaraspa.shared.generated.resources.inter_medium
import ankaraspa.shared.generated.resources.inter_regular
import org.jetbrains.compose.resources.Font

@Composable
fun ankaraTypography(): Typography {
    val fraunces = FontFamily(Font(Res.font.fraunces_semibold))
    val inter = FontFamily(Font(Res.font.inter_regular), Font(Res.font.inter_medium, FontWeight.Medium))
    return Typography(
        headlineMedium = TextStyle(fontFamily = fraunces, fontSize = 28.sp, color = AnkaraColors.Ink),
        titleMedium = TextStyle(fontFamily = fraunces, fontSize = 20.sp, color = AnkaraColors.Ink),
        bodyMedium = TextStyle(fontFamily = inter, fontSize = 15.sp, lineHeight = 22.sp, color = AnkaraColors.Ink),
        labelMedium = TextStyle(fontFamily = inter, fontWeight = FontWeight.Medium, fontSize = 13.sp),
    )
}
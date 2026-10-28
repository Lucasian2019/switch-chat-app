package tivana.chat.switchapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import tivana.chat.switchapp.R

val BarlowFontFamily = FontFamily(
    Font(R.font.barlow_regular, FontWeight.Normal),
    Font(R.font.barlow_medium, FontWeight.Medium),
    Font(R.font.barlow_semibold, FontWeight.SemiBold)
)

object SwitchTypography {
    val logo = TextStyle(
        fontFamily = BarlowFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        letterSpacing = 0.42.em
    )
    val sectionHeading = TextStyle(
        fontFamily = BarlowFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 0.2.em
    )
    val button = TextStyle(
        fontFamily = BarlowFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        letterSpacing = 0.16.em
    )
    val fieldLabel = TextStyle(
        fontFamily = BarlowFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        letterSpacing = 0.14.em
    )
    val body = TextStyle(
        fontFamily = BarlowFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp
    )
    val secondary = TextStyle(
        fontFamily = BarlowFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp
    )
    val bodySmall = TextStyle(
        fontFamily = BarlowFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 16.sp
    )
}

val Typography = Typography(
    bodyLarge = SwitchTypography.body,
    bodyMedium = SwitchTypography.secondary,
    titleLarge = SwitchTypography.logo,
    titleMedium = SwitchTypography.sectionHeading,
    labelLarge = SwitchTypography.button,
    labelSmall = SwitchTypography.fieldLabel
)

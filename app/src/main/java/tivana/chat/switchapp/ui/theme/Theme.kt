package tivana.chat.switchapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

val LocalSwitchColors = staticCompositionLocalOf { SwitchColors }

object SwitchTheme {
    val colors: SwitchColors
        @Composable
        get() = LocalSwitchColors.current
}

private val DarkColorScheme = darkColorScheme(
    background = SwitchColors.Background,
    surface = SwitchColors.Panel,
    primary = SwitchColors.SpaceXBlue,
    onPrimary = SwitchColors.TextPrimary,
    secondary = SwitchColors.BlueLight,
    onSecondary = SwitchColors.TextPrimary,
    error = SwitchColors.DangerRed,
    onError = SwitchColors.TextPrimary,
    outline = SwitchColors.Line
)

@Composable
fun SWITCHTheme(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalSwitchColors provides SwitchColors) {
        MaterialTheme(
            colorScheme = DarkColorScheme,
            typography = Typography,
            content = content
        )
    }
}

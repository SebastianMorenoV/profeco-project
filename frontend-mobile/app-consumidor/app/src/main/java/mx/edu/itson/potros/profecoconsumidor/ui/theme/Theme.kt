package mx.edu.itson.potros.profecoconsumidor.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = ProfecoPurple,
    onPrimary = Color.White,
    primaryContainer = ProfecoPurpleLight,
    onPrimaryContainer = ProfecoPurpleDark,
    secondary = ProfecoAccent,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE0B2),
    onSecondaryContainer = ProfecoAccentDark,
    tertiary = ProfecoSuccess,
    background = ProfecoBg,
    onBackground = ProfecoText,
    surface = ProfecoSurface,
    onSurface = ProfecoText,
    surfaceVariant = Color(0xFFEFEDF4),
    onSurfaceVariant = ProfecoMuted,
    error = ProfecoDanger,
    onError = Color.White
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFCBB3E8),
    onPrimary = ProfecoPurpleDark,
    primaryContainer = Color(0xFF553285),
    onPrimaryContainer = ProfecoPurpleLight,
    secondary = Color(0xFFFFB871),
    onSecondary = Color(0xFF4A2A00),
    background = Color(0xFF14121A),
    onBackground = Color(0xFFE6E1EC),
    surface = Color(0xFF1B1922),
    onSurface = Color(0xFFE6E1EC),
    surfaceVariant = Color(0xFF2A2632),
    onSurfaceVariant = Color(0xFFB7B0BE),
    error = Color(0xFFE0857C),
    onError = Color(0xFF3B0A05)
)

@Composable
fun ProfecoConsumidorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

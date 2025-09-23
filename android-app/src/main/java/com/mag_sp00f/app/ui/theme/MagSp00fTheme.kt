package com.mag_sp00f.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Professional Android 14 Theme Colors
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF00BCD4),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF006064),
    onPrimaryContainer = Color(0xFFE0F2F1),
    secondary = Color(0xFF4CAF50),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF1B5E20),
    onSecondaryContainer = Color(0xFFE8F5E8),
    tertiary = Color(0xFFFF9800),
    onTertiary = Color.Black,
    error = Color(0xFFFF5252),
    onError = Color.White,
    background = Color(0xFF121212),
    onBackground = Color(0xFFE0E0E0),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = Color(0xFFBDBDBD),
    outline = Color(0xFF757575),
    outlineVariant = Color(0xFF424242)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF00838F),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F7FA),
    onPrimaryContainer = Color(0xFF006064),
    secondary = Color(0xFF2E7D32),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFC8E6C9),
    onSecondaryContainer = Color(0xFF1B5E20),
    tertiary = Color(0xFFE65100),
    onTertiary = Color.White,
    error = Color(0xFFD32F2F),
    onError = Color.White,
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF1C1C1C),
    surface = Color.White,
    onSurface = Color(0xFF1C1C1C),
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = Color(0xFF424242),
    outline = Color(0xFF757575),
    outlineVariant = Color(0xFFBDBDBD)
)

@Composable
fun MagSp00fTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

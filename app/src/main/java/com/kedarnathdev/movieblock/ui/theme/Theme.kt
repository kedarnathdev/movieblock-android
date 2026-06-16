package com.kedarnathdev.movieblock.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = SurfaceDarkElevated,
    onPrimaryContainer = OnDark,
    secondary = AccentTeal,
    onSecondary = OnPrimary,
    tertiary = AccentAmber,
    onTertiary = OnPrimary,
    error = Error,
    onError = OnPrimary,
    errorContainer = Error.copy(alpha = 0.15f),
    onErrorContainer = Error,
    background = Canvas,
    onBackground = Body,
    surface = SurfaceCard,
    onSurface = OnDark,
    surfaceVariant = SurfaceDarkElevated,
    onSurfaceVariant = OnDarkSoft,
    outline = Hairline,
    outlineVariant = HairlineSoft,
    inverseSurface = SurfaceCreamStrong,
    inverseOnSurface = Ink,
    inversePrimary = PrimaryActive,
    scrim = Canvas
)

@Composable
fun MovieBlockTheme(
    darkTheme: Boolean = true, // Always dark theme
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Canvas.toArgb()
            window.navigationBarColor = Canvas.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MovieBlockTypography,
        content = content
    )
}

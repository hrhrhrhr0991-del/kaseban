package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.core.view.WindowCompat

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    palette: AppColorPalette = AppColorPalette.PASTEL_TEAL,
    content: @Composable () -> Unit,
) {
    val appColors = getAppColors(palette, darkTheme)
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = appColors.primary,
            onPrimary = Color.Black,
            primaryContainer = appColors.primaryDark,
            onPrimaryContainer = appColors.textPrimary,
            secondary = appColors.secondary,
            onSecondary = Color.Black,
            background = appColors.background,
            onBackground = appColors.textPrimary,
            surface = appColors.surface,
            onSurface = appColors.textPrimary,
            surfaceVariant = appColors.surfaceCard,
            onSurfaceVariant = appColors.textSecondary,
            outline = appColors.border,
            outlineVariant = appColors.borderSubtle
        )
    } else {
        lightColorScheme(
            primary = appColors.primary,
            onPrimary = Color.White,
            primaryContainer = appColors.primaryLight,
            onPrimaryContainer = appColors.primaryDark,
            secondary = appColors.secondary,
            onSecondary = Color.White,
            background = appColors.background,
            onBackground = appColors.textPrimary,
            surface = appColors.surface,
            onSurface = appColors.textPrimary,
            surfaceVariant = appColors.surfaceCard,
            onSurfaceVariant = appColors.textSecondary,
            outline = appColors.border,
            outlineVariant = appColors.borderSubtle
        )
    }

    CompositionLocalProvider(
        LocalAppThemeColors provides appColors,
        LocalTextStyle provides TextStyle(
            fontFamily = PeydaFontFamily,
            color = appColors.textPrimary
        )
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

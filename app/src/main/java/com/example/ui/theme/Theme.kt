package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

enum class AppThemeMode(val displayName: String) {
    OBSIDIAN_DARK("Obsidian Dark"),
    MINT_PASTEL("Mint Pastel"),
    PEACH_PASTEL("Peach Pastel"),
    LAVENDER_PASTEL("Lavender Pastel")
}

val HighQDarkColorScheme = darkColorScheme(
    primary = HighQMint,
    onPrimary = HighQDarkBg,
    primaryContainer = HighQSurfaceElevated,
    onPrimaryContainer = HighQMint,
    secondary = HighQAqua,
    onSecondary = HighQDarkBg,
    tertiary = HighQPeach,
    onTertiary = HighQDarkBg,
    background = HighQDarkBg,
    onBackground = HighQTextPrimary,
    surface = HighQSurface,
    onSurface = HighQTextPrimary,
    surfaceVariant = HighQSurfaceElevated,
    onSurfaceVariant = HighQTextSecondary,
    outline = HighQBorderSubtle,
    outlineVariant = HighQBorderFocused
)

val MintPastelColorScheme = lightColorScheme(
    primary = PastelMintPrimary,
    onPrimary = Color.White,
    primaryContainer = PastelMintCard,
    onPrimaryContainer = PastelMintText,
    secondary = HighQAqua,
    onSecondary = Color.White,
    tertiary = HighQPeach,
    onTertiary = Color.White,
    background = PastelMintBg,
    onBackground = PastelMintText,
    surface = PastelMintSurface,
    onSurface = PastelMintText,
    surfaceVariant = PastelMintCard,
    onSurfaceVariant = PastelMintTextSec,
    outline = PastelMintBorder,
    outlineVariant = PastelMintPrimary
)

val PeachPastelColorScheme = lightColorScheme(
    primary = PastelPeachPrimary,
    onPrimary = Color.White,
    primaryContainer = PastelPeachCard,
    onPrimaryContainer = PastelPeachText,
    secondary = HighQOrange,
    onSecondary = Color.White,
    tertiary = HighQGold,
    onTertiary = Color.White,
    background = PastelPeachBg,
    onBackground = PastelPeachText,
    surface = PastelPeachSurface,
    onSurface = PastelPeachText,
    surfaceVariant = PastelPeachCard,
    onSurfaceVariant = PastelPeachTextSec,
    outline = PastelPeachBorder,
    outlineVariant = PastelPeachPrimary
)

val LavenderPastelColorScheme = lightColorScheme(
    primary = PastelLavenderPrimary,
    onPrimary = Color.White,
    primaryContainer = PastelLavenderCard,
    onPrimaryContainer = PastelLavenderText,
    secondary = HighQAqua,
    onSecondary = Color.White,
    tertiary = HighQMint,
    onTertiary = Color.White,
    background = PastelLavenderBg,
    onBackground = PastelLavenderText,
    surface = PastelLavenderSurface,
    onSurface = PastelLavenderText,
    surfaceVariant = PastelLavenderCard,
    onSurfaceVariant = PastelLavenderTextSec,
    outline = PastelLavenderBorder,
    outlineVariant = PastelLavenderPrimary
)

@Composable
fun HIGHQTheme(
    themeMode: AppThemeMode = AppThemeMode.OBSIDIAN_DARK,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeMode) {
        AppThemeMode.OBSIDIAN_DARK -> HighQDarkColorScheme
        AppThemeMode.MINT_PASTEL -> MintPastelColorScheme
        AppThemeMode.PEACH_PASTEL -> PeachPastelColorScheme
        AppThemeMode.LAVENDER_PASTEL -> LavenderPastelColorScheme
    }

    val isDark = themeMode == AppThemeMode.OBSIDIAN_DARK
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !isDark
            controller.isAppearanceLightNavigationBars = !isDark
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = HighQTypography,
        content = content
    )
}

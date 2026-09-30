package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ==========================================
// Kaseban Multi-Theme System
// 1. Pastel Teal & Mint (Default Light - Gentle & Soft)
// 2. Navy & White (سرمه‌ای و سفید - Deep Royal Navy & Crisp White)
// 3. Emerald Green & White (سبز و سفید - Forest Sage & White)
// Full Dark Mode & Light Mode support for all palettes
// ==========================================

enum class AppThemeMode {
    LIGHT,
    DARK
}

enum class AppColorPalette(val displayName: String, val primaryColor: Color, val secondaryColor: Color) {
    PASTEL_TEAL("فیروزه‌ای و نعنایی", Color(0xFF0F766E), Color(0xFF0284C7)),
    NAVY_WHITE("سرمه‌ای و سفید", Color(0xFF1E3A8A), Color(0xFF2563EB)),
    EMERALD_GREEN("سبز و سفید", Color(0xFF047857), Color(0xFF10B981))
}

data class AppThemeColors(
    val isDark: Boolean,
    val palette: AppColorPalette,
    val primary: Color,
    val primaryDark: Color,
    val primaryLight: Color,
    val primaryGradient: Brush,
    val secondary: Color,
    val background: Color,
    val backgroundGradient: Brush,
    val surface: Color,
    val surfaceCard: Color,
    val surfaceHighlight: Color,
    val border: Color,
    val borderSubtle: Color,
    val borderBrush: Brush,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val chipBg: Color,
    val chipText: Color,
    val orb1: Color,
    val orb2: Color,
    val orb3: Color,
    val orb4: Color
)

fun getAppColors(palette: AppColorPalette, isDark: Boolean): AppThemeColors {
    return when (palette) {
        AppColorPalette.PASTEL_TEAL -> {
            if (!isDark) {
                // Calm, soothing teal & mint (gentle on the eyes, not blinding)
                AppThemeColors(
                    isDark = false,
                    palette = palette,
                    primary = Color(0xFF0F766E),
                    primaryDark = Color(0xFF115E59),
                    primaryLight = Color(0xFFE6F7F4),
                    primaryGradient = Brush.horizontalGradient(listOf(Color(0xFF0F766E), Color(0xFF0284C7))),
                    secondary = Color(0xFF0284C7),
                    background = Color(0xFFF4FAF8),
                    backgroundGradient = Brush.verticalGradient(
                        listOf(Color(0xFFEAF7F3), Color(0xFFE4F2FA), Color(0xFFF4FAF8))
                    ),
                    surface = Color(0xCCFFFFFF),
                    surfaceCard = Color(0xF2FFFFFF),
                    surfaceHighlight = Color(0xFFE6F7F4),
                    border = Color(0x400F766E),
                    borderSubtle = Color(0x260F766E),
                    borderBrush = Brush.linearGradient(
                        listOf(Color(0x99FFFFFF), Color(0x5934D399), Color(0x5938BDF8), Color(0x80FFFFFF)),
                        start = Offset(0f, 0f), end = Offset(400f, 400f)
                    ),
                    textPrimary = Color(0xFF0F172A),
                    textSecondary = Color(0xFF334155),
                    textMuted = Color(0xFF64748B),
                    chipBg = Color(0xFFE0F2FE),
                    chipText = Color(0xFF0369A1),
                    orb1 = Color(0x33A7F3D0),
                    orb2 = Color(0x26BAE6FD),
                    orb3 = Color(0x266EE7B7),
                    orb4 = Color(0x207DD3FC)
                )
            } else {
                // Dark Teal
                AppThemeColors(
                    isDark = true,
                    palette = palette,
                    primary = Color(0xFF2DD4BF),
                    primaryDark = Color(0xFF14B8A6),
                    primaryLight = Color(0xFF134E48),
                    primaryGradient = Brush.horizontalGradient(listOf(Color(0xFF0D9488), Color(0xFF0284C7))),
                    secondary = Color(0xFF38BDF8),
                    background = Color(0xFF0A1118),
                    backgroundGradient = Brush.verticalGradient(
                        listOf(Color(0xFF0B131E), Color(0xFF0F172A), Color(0xFF080D14))
                    ),
                    surface = Color(0xD9172234),
                    surfaceCard = Color(0xF2172234),
                    surfaceHighlight = Color(0xFF1E3048),
                    border = Color(0x4D2DD4BF),
                    borderSubtle = Color(0x262DD4BF),
                    borderBrush = Brush.linearGradient(
                        listOf(Color(0x4D2DD4BF), Color(0x2638BDF8)),
                        start = Offset(0f, 0f), end = Offset(400f, 400f)
                    ),
                    textPrimary = Color(0xFFF8FAFC),
                    textSecondary = Color(0xFFCBD5E1),
                    textMuted = Color(0xFF94A3B8),
                    chipBg = Color(0xFF1E3A4B),
                    chipText = Color(0xFF7DD3FC),
                    orb1 = Color(0x200D9488),
                    orb2 = Color(0x180284C7),
                    orb3 = Color(0x18059669),
                    orb4 = Color(0x140369A1)
                )
            }
        }
        AppColorPalette.NAVY_WHITE -> {
            if (!isDark) {
                // Crisp Royal Navy & White (سرمه‌ای و سفید)
                AppThemeColors(
                    isDark = false,
                    palette = palette,
                    primary = Color(0xFF1E3A8A), // Deep Royal Navy
                    primaryDark = Color(0xFF172554),
                    primaryLight = Color(0xFFEFF6FF),
                    primaryGradient = Brush.horizontalGradient(listOf(Color(0xFF1E3A8A), Color(0xFF1D4ED8))),
                    secondary = Color(0xFF2563EB),
                    background = Color(0xFFF8FAFC),
                    backgroundGradient = Brush.verticalGradient(
                        listOf(Color(0xFFF1F5F9), Color(0xFFEFF6FF), Color(0xFFFFFFFF))
                    ),
                    surface = Color(0xF0FFFFFF),
                    surfaceCard = Color(0xFFFFFFFF),
                    surfaceHighlight = Color(0xFFEFF6FF),
                    border = Color(0x331E3A8A),
                    borderSubtle = Color(0x1A1E3A8A),
                    borderBrush = Brush.linearGradient(
                        listOf(Color(0x99FFFFFF), Color(0x403B82F6), Color(0x80FFFFFF)),
                        start = Offset(0f, 0f), end = Offset(400f, 400f)
                    ),
                    textPrimary = Color(0xFF0F172A),
                    textSecondary = Color(0xFF1E293B),
                    textMuted = Color(0xFF475569),
                    chipBg = Color(0xFFDBEAFE),
                    chipText = Color(0xFF1E40AF),
                    orb1 = Color(0x243B82F6),
                    orb2 = Color(0x1C1E40AF),
                    orb3 = Color(0x1C60A5FA),
                    orb4 = Color(0x1593C5FD)
                )
            } else {
                // Midnight Navy Dark
                AppThemeColors(
                    isDark = true,
                    palette = palette,
                    primary = Color(0xFF60A5FA),
                    primaryDark = Color(0xFF3B82F6),
                    primaryLight = Color(0xFF1E3A8A),
                    primaryGradient = Brush.horizontalGradient(listOf(Color(0xFF2563EB), Color(0xFF3B82F6))),
                    secondary = Color(0xFF93C5FD),
                    background = Color(0xFF080D1A),
                    backgroundGradient = Brush.verticalGradient(
                        listOf(Color(0xFF080D1A), Color(0xFF0E172E), Color(0xFF060913))
                    ),
                    surface = Color(0xD9131F3B),
                    surfaceCard = Color(0xF2131F3B),
                    surfaceHighlight = Color(0xFF1D2E54),
                    border = Color(0x4D60A5FA),
                    borderSubtle = Color(0x263B82F6),
                    borderBrush = Brush.linearGradient(
                        listOf(Color(0x4D60A5FA), Color(0x262563EB)),
                        start = Offset(0f, 0f), end = Offset(400f, 400f)
                    ),
                    textPrimary = Color(0xFFF8FAFC),
                    textSecondary = Color(0xFFBFDBFE),
                    textMuted = Color(0xFF94A3B8),
                    chipBg = Color(0xFF1E3A5F),
                    chipText = Color(0xFF93C5FD),
                    orb1 = Color(0x221E40AF),
                    orb2 = Color(0x1A2563EB),
                    orb3 = Color(0x1A1D4ED8),
                    orb4 = Color(0x153B82F6)
                )
            }
        }
        AppColorPalette.EMERALD_GREEN -> {
            if (!isDark) {
                // Forest Emerald Green & White (سبز و سفید)
                AppThemeColors(
                    isDark = false,
                    palette = palette,
                    primary = Color(0xFF047857), // Forest Emerald
                    primaryDark = Color(0xFF064E3B),
                    primaryLight = Color(0xFFECFDF5),
                    primaryGradient = Brush.horizontalGradient(listOf(Color(0xFF065F46), Color(0xFF059669))),
                    secondary = Color(0xFF10B981),
                    background = Color(0xFFF7FDF9),
                    backgroundGradient = Brush.verticalGradient(
                        listOf(Color(0xFFF0FDF4), Color(0xFFE8F5EE), Color(0xFFFFFFFF))
                    ),
                    surface = Color(0xF0FFFFFF),
                    surfaceCard = Color(0xFFFFFFFF),
                    surfaceHighlight = Color(0xFFECFDF5),
                    border = Color(0x33047857),
                    borderSubtle = Color(0x1A059669),
                    borderBrush = Brush.linearGradient(
                        listOf(Color(0x99FFFFFF), Color(0x4010B981), Color(0x80FFFFFF)),
                        start = Offset(0f, 0f), end = Offset(400f, 400f)
                    ),
                    textPrimary = Color(0xFF064E3B),
                    textSecondary = Color(0xFF1F2937),
                    textMuted = Color(0xFF4B5563),
                    chipBg = Color(0xFFD1FAE5),
                    chipText = Color(0xFF065F46),
                    orb1 = Color(0x2410B981),
                    orb2 = Color(0x1C059669),
                    orb3 = Color(0x1C34D399),
                    orb4 = Color(0x156EE7B7)
                )
            } else {
                // Dark Forest Green
                AppThemeColors(
                    isDark = true,
                    palette = palette,
                    primary = Color(0xFF34D399),
                    primaryDark = Color(0xFF059669),
                    primaryLight = Color(0xFF064E3B),
                    primaryGradient = Brush.horizontalGradient(listOf(Color(0xFF059669), Color(0xFF10B981))),
                    secondary = Color(0xFF6EE7B7),
                    background = Color(0xFF06140E),
                    backgroundGradient = Brush.verticalGradient(
                        listOf(Color(0xFF06140E), Color(0xFF0B2219), Color(0xFF040D09))
                    ),
                    surface = Color(0xD90F2D21),
                    surfaceCard = Color(0xF20F2D21),
                    surfaceHighlight = Color(0xFF173E2E),
                    border = Color(0x4D34D399),
                    borderSubtle = Color(0x2610B981),
                    borderBrush = Brush.linearGradient(
                        listOf(Color(0x4D34D399), Color(0x26059669)),
                        start = Offset(0f, 0f), end = Offset(400f, 400f)
                    ),
                    textPrimary = Color(0xFFF0FDF4),
                    textSecondary = Color(0xFFA7F3D0),
                    textMuted = Color(0xFF6EE7B7),
                    chipBg = Color(0xFF143B2B),
                    chipText = Color(0xFFA7F3D0),
                    orb1 = Color(0x22047857),
                    orb2 = Color(0x1A065F46),
                    orb3 = Color(0x1A10B981),
                    orb4 = Color(0x15059669)
                )
            }
        }
    }
}

val LocalAppThemeColors = staticCompositionLocalOf {
    getAppColors(AppColorPalette.PASTEL_TEAL, false)
}

object AppTheme {
    val colors: AppThemeColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAppThemeColors.current
}

// ==========================================
// Compatibility Fallback Variables
// ==========================================
val BackgroundAtmosphereTop = Color(0xFFE6F7F2)
val BackgroundAtmosphereMiddle = Color(0xFFE0F2FE)
val BackgroundAtmosphereBottom = Color(0xFFEBF6F9)

val BackgroundAmbientGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFFE6F7F2),
        Color(0xFFE0F2FE),
        Color(0xFFF4FAF8)
    )
)

val BackgroundIceLight = Color(0xFFE8F6F4)
val BackgroundSurface = Color(0x73FFFFFF)
val BackgroundDark = Color(0xFF0F172A)

val SurfaceGlassWhite = Color(0x73FFFFFF)
val SurfaceGlassBlue = Color(0x59BAE6FD)
val SurfaceGlassMint = Color(0x59A7F3D0)
val SurfaceGlassCard = Color(0x8CFFFFFF)
val SurfaceGlassHighlight = Color(0x99F0FDFA)
val SurfaceGlassFrostedUltra = Color(0x73FFFFFF)

val GlassBorderBlue = Color(0x4038BDF8)
val GlassBorderMint = Color(0x4034D399)
val GlassBorderSubtle = Color(0x3310B981)
val GlassBorderWhite = Color(0x80FFFFFF)

val GlassBorderRefractionBrush = Brush.linearGradient(
    colors = listOf(
        Color(0x99FFFFFF),
        Color(0x5934D399),
        Color(0x5938BDF8),
        Color(0x80FFFFFF)
    ),
    start = Offset(0f, 0f),
    end = Offset(400f, 400f)
)

val GlassCardFrostedBrush = Brush.verticalGradient(
    colors = listOf(
        Color(0x8CFFFFFF),
        Color(0x66F0FDF4),
        Color(0x66E0F2FE)
    )
)

val PastelMintPrimary = Color(0xFF059669)
val PastelMintLight = Color(0xFFD1FAE5)
val PastelMintDark = Color(0xFF047857)

val PastelBluePrimary = Color(0xFF0284C7)
val PastelBlueLight = Color(0xFFBAE6FD)
val PastelBlueDark = Color(0xFF0369A1)

val BluePrimary = Color(0xFF0D9488)
val BlueSecondary = Color(0xFF0284C7)
val BluePrimaryDark = Color(0xFF0F766E)
val BlueCyanGlow = Color(0xFF06B6D4)
val BlueSkyVibrant = Color(0xFF38BDF8)
val BlueLight = Color(0xFFCCFBF1)
val BlueLightAlt = Color(0xFFE0F2FE)

val TrustGreen = Color(0xFF059669)
val TrustGreenLight = Color(0xFFD1FAE5)
val StatusSuccess = Color(0xFF059669)
val StatusWarning = Color(0xFFD97706)
val StatusPending = Color(0xFFEAB308)
val StatusInfo = Color(0xFF0284C7)
val StatusError = Color(0xFFDC2626)

val TextPrimary = Color(0xFF0F172A)
val TextSecondary = Color(0xFF334155)
val TextMuted = Color(0xFF64748B)

val BlueGlassGradient = Brush.verticalGradient(listOf(Color(0x8CFFFFFF), Color(0x66ECFDF5)))
val PastelGradient = Brush.horizontalGradient(listOf(Color(0xFF059669), Color(0xFF0284C7)))
val BluePrimaryGradient = Brush.horizontalGradient(listOf(Color(0xFF0D9488), Color(0xFF0284C7)))
val BlueGlassIconGradient = Brush.linearGradient(listOf(Color(0x4034D399), Color(0x4038BDF8)))
val TrustGreenGradient = Brush.horizontalGradient(listOf(Color(0xFF059669), Color(0xFF0D9488)))
val WatermarkGradient = Brush.horizontalGradient(listOf(Color(0xCC059669), Color(0xCC0284C7)))

val GlassBorder = GlassBorderMint
val SurfaceDarkGlass = SurfaceGlassCard
val SurfaceGlassLight = SurfaceGlassWhite
val SurfaceGlassMedium = SurfaceGlassBlue
val WheatGold = PastelMintPrimary
val WheatGoldDark = PastelMintDark
val WheatGoldLight = PastelMintLight

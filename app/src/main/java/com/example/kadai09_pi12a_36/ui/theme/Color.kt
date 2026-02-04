package com.example.kadai09_pi12a_36.ui.theme

import androidx.compose.ui.graphics.Color

// Base Dark Theme Colors
val DarkBackground = Color(0xFF0D0D1A)
val DarkSurface = Color(0xFF1A1A2E)
val DarkSurfaceVariant = Color(0xFF252542)

// Glassmorphism Colors
val GlassWhite = Color(0x1AFFFFFF)
val GlassBorder = Color(0x33FFFFFF)

// Category Colors - Primary
object CategoryColors {
    // Space - Navy/Purple (星空・深宇宙)
    val SpacePrimary = Color(0xFF7C4DFF)
    val SpaceSecondary = Color(0xFF1A237E)
    val SpaceAccent = Color(0xFFB388FF)
    val SpaceGlow = Color(0x407C4DFF)

    // Physics - Cyan (電磁気・エネルギー)
    val PhysicsPrimary = Color(0xFF00BCD4)
    val PhysicsSecondary = Color(0xFF006064)
    val PhysicsAccent = Color(0xFF18FFFF)
    val PhysicsGlow = Color(0x4000BCD4)

    // Chemistry - Amber/Orange (化学反応・炎)
    val ChemistryPrimary = Color(0xFFFF9800)
    val ChemistrySecondary = Color(0xFFE65100)
    val ChemistryAccent = Color(0xFFFFD54F)
    val ChemistryGlow = Color(0x40FF9800)

    // Biology - Green (生命・自然)
    val BiologyPrimary = Color(0xFF4CAF50)
    val BiologySecondary = Color(0xFF1B5E20)
    val BiologyAccent = Color(0xFF69F0AE)
    val BiologyGlow = Color(0x404CAF50)

    // Earth - Brown/Terracotta (地層・岩石)
    val EarthPrimary = Color(0xFF8D6E63)
    val EarthSecondary = Color(0xFF3E2723)
    val EarthAccent = Color(0xFFBCAAA4)
    val EarthGlow = Color(0x408D6E63)
}

// Neon Glow Colors
val NeonPurple = Color(0xFFE040FB)
val NeonCyan = Color(0xFF18FFFF)
val NeonPink = Color(0xFFFF4081)
val NeonGreen = Color(0xFF69F0AE)
val NeonOrange = Color(0xFFFFAB40)

// Feedback Colors
val CorrectGreen = Color(0xFF00E676)
val CorrectGreenGlow = Color(0x4000E676)
val IncorrectRed = Color(0xFFFF5252)
val IncorrectRedGlow = Color(0x40FF5252)

// Text Colors
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFB0BEC5)
val TextDisabled = Color(0xFF616161)

// Gradient Pairs
data class GradientColors(
    val start: Color,
    val end: Color
)

val SpaceGradient = GradientColors(
    start = Color(0xFF7C4DFF),
    end = Color(0xFF1A237E)
)

val PhysicsGradient = GradientColors(
    start = Color(0xFF00BCD4),
    end = Color(0xFF006064)
)

val ChemistryGradient = GradientColors(
    start = Color(0xFFFF9800),
    end = Color(0xFFE65100)
)

val BiologyGradient = GradientColors(
    start = Color(0xFF4CAF50),
    end = Color(0xFF1B5E20)
)

val EarthGradient = GradientColors(
    start = Color(0xFF8D6E63),
    end = Color(0xFF3E2723)
)

fun getCategoryGradient(category: String): GradientColors {
    return when (category) {
        "宇宙" -> SpaceGradient
        "物理" -> PhysicsGradient
        "化学" -> ChemistryGradient
        "生物" -> BiologyGradient
        "地学" -> EarthGradient
        else -> SpaceGradient
    }
}

fun getCategoryPrimaryColor(category: String): Color {
    return when (category) {
        "宇宙" -> CategoryColors.SpacePrimary
        "物理" -> CategoryColors.PhysicsPrimary
        "化学" -> CategoryColors.ChemistryPrimary
        "生物" -> CategoryColors.BiologyPrimary
        "地学" -> CategoryColors.EarthPrimary
        else -> CategoryColors.SpacePrimary
    }
}

fun getCategoryGlowColor(category: String): Color {
    return when (category) {
        "宇宙" -> CategoryColors.SpaceGlow
        "物理" -> CategoryColors.PhysicsGlow
        "化学" -> CategoryColors.ChemistryGlow
        "生物" -> CategoryColors.BiologyGlow
        "地学" -> CategoryColors.EarthGlow
        else -> CategoryColors.SpaceGlow
    }
}

package com.example.kadai09_pi12a_36.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Category-specific color scheme
data class CategoryColorScheme(
    val primary: Color,
    val secondary: Color,
    val accent: Color,
    val glow: Color,
    val gradientStart: Color,
    val gradientEnd: Color
)

val SpaceColorScheme = CategoryColorScheme(
    primary = CategoryColors.SpacePrimary,
    secondary = CategoryColors.SpaceSecondary,
    accent = CategoryColors.SpaceAccent,
    glow = CategoryColors.SpaceGlow,
    gradientStart = SpaceGradient.start,
    gradientEnd = SpaceGradient.end
)

val PhysicsColorScheme = CategoryColorScheme(
    primary = CategoryColors.PhysicsPrimary,
    secondary = CategoryColors.PhysicsSecondary,
    accent = CategoryColors.PhysicsAccent,
    glow = CategoryColors.PhysicsGlow,
    gradientStart = PhysicsGradient.start,
    gradientEnd = PhysicsGradient.end
)

val ChemistryColorScheme = CategoryColorScheme(
    primary = CategoryColors.ChemistryPrimary,
    secondary = CategoryColors.ChemistrySecondary,
    accent = CategoryColors.ChemistryAccent,
    glow = CategoryColors.ChemistryGlow,
    gradientStart = ChemistryGradient.start,
    gradientEnd = ChemistryGradient.end
)

val BiologyColorScheme = CategoryColorScheme(
    primary = CategoryColors.BiologyPrimary,
    secondary = CategoryColors.BiologySecondary,
    accent = CategoryColors.BiologyAccent,
    glow = CategoryColors.BiologyGlow,
    gradientStart = BiologyGradient.start,
    gradientEnd = BiologyGradient.end
)

val EarthColorScheme = CategoryColorScheme(
    primary = CategoryColors.EarthPrimary,
    secondary = CategoryColors.EarthSecondary,
    accent = CategoryColors.EarthAccent,
    glow = CategoryColors.EarthGlow,
    gradientStart = EarthGradient.start,
    gradientEnd = EarthGradient.end
)

fun getCategoryColorScheme(category: String): CategoryColorScheme {
    return when (category) {
        "宇宙" -> SpaceColorScheme
        "物理" -> PhysicsColorScheme
        "化学" -> ChemistryColorScheme
        "生物" -> BiologyColorScheme
        "地学" -> EarthColorScheme
        else -> SpaceColorScheme
    }
}

// CompositionLocal for category colors
val LocalCategoryColors = staticCompositionLocalOf { SpaceColorScheme }

// Base dark color scheme
private val DarkColorScheme = darkColorScheme(
    primary = CategoryColors.SpacePrimary,
    onPrimary = Color.White,
    primaryContainer = CategoryColors.SpaceSecondary,
    onPrimaryContainer = Color.White,
    secondary = NeonCyan,
    onSecondary = Color.Black,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = Color.White,
    tertiary = NeonPink,
    onTertiary = Color.Black,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    error = IncorrectRed,
    onError = Color.White
)

@Composable
fun QuizAppTheme(
    category: String = "宇宙",
    content: @Composable () -> Unit
) {
    val categoryColors = getCategoryColorScheme(category)

    val colorScheme = DarkColorScheme.copy(
        primary = categoryColors.primary,
        primaryContainer = categoryColors.secondary,
        secondary = categoryColors.accent
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = DarkBackground.toArgb()
            window.navigationBarColor = DarkBackground.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    CompositionLocalProvider(LocalCategoryColors provides categoryColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

// Extension to access category colors easily
object QuizTheme {
    val categoryColors: CategoryColorScheme
        @Composable
        get() = LocalCategoryColors.current
}

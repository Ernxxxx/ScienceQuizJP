package com.example.kadai09_pi12a_36.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.kadai09_pi12a_36.ui.theme.GlassBorder
import com.example.kadai09_pi12a_36.ui.theme.GlassWhite
import com.example.kadai09_pi12a_36.ui.theme.QuizTheme

@Composable
fun GlassmorphicCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    glowColor: Color? = null,
    enableShimmerBorder: Boolean = false,
    enableFloatingAnimation: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val categoryColors = QuizTheme.categoryColors
    val effectiveGlowColor = glowColor ?: categoryColors.glow

    // Shimmer border animation
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerOffset"
    )

    // Floating animation (gentle bob)
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatOffset"
    )

    val yOffset = if (enableFloatingAnimation) {
        (floatOffset * 6f - 3f).dp
    } else {
        0.dp
    }

    Box(
        modifier = modifier.offset(y = yOffset)
    ) {
        // Drop shadow layer with category color - softer radial glow
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .offset(y = 4.dp)
                .blur(20.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            effectiveGlowColor.copy(alpha = 0.5f),
                            effectiveGlowColor.copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(cornerRadius + 4.dp)
                )
        )

        // Secondary shadow for depth
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(horizontal = 4.dp)
                .offset(y = 8.dp)
                .blur(28.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.35f),
                            Color.Black.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(cornerRadius + 8.dp)
                )
        )

        // Main card with layered blur simulation
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(cornerRadius))
                // Layer 1: Deep background
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0x0DFFFFFF),
                            Color(0x05FFFFFF)
                        )
                    )
                )
        ) {
            // Layer 2: Frost effect simulation
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.08f),
                                Color.Transparent
                            ),
                            center = Offset(0.3f, 0.2f),
                            radius = 800f
                        )
                    )
            )

            // Layer 3: Top highlight (glossy effect)
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.15f),
                                Color.Transparent,
                                Color.Transparent
                            ),
                            startY = 0f,
                            endY = 200f
                        )
                    )
            )

            // Layer 4: Main glass surface
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                GlassWhite,
                                Color(0x0DFFFFFF)
                            )
                        )
                    )
            )
        }

        // Shimmer border effect
        if (enableShimmerBorder) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .drawWithContent {
                        drawContent()
                        val shimmerStart = shimmerOffset * (size.width + size.height) * 2
                        drawRect(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.White.copy(alpha = 0.5f),
                                    effectiveGlowColor.copy(alpha = 0.7f),
                                    Color.White.copy(alpha = 0.5f),
                                    Color.Transparent
                                ),
                                start = Offset(shimmerStart - 200f, shimmerStart - 200f),
                                end = Offset(shimmerStart, shimmerStart)
                            )
                        )
                    }
                    .clip(RoundedCornerShape(cornerRadius))
                    .border(
                        width = 2.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                effectiveGlowColor.copy(alpha = 0.6f),
                                Color.White.copy(alpha = 0.4f),
                                effectiveGlowColor.copy(alpha = 0.8f),
                                Color.White.copy(alpha = 0.3f),
                                effectiveGlowColor.copy(alpha = 0.6f)
                            ),
                            start = Offset(shimmerOffset * 1000f, 0f),
                            end = Offset(shimmerOffset * 1000f + 500f, 500f)
                        ),
                        shape = RoundedCornerShape(cornerRadius)
                    )
            )
        } else {
            // Static premium border
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .border(
                        width = 1.5.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.4f),
                                GlassBorder,
                                Color.White.copy(alpha = 0.1f)
                            )
                        ),
                        shape = RoundedCornerShape(cornerRadius)
                    )
            )
        }

        // Inner glow effect layer
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(cornerRadius))
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            effectiveGlowColor.copy(alpha = 0.2f),
                            effectiveGlowColor.copy(alpha = 0.05f),
                            Color.Transparent
                        ),
                        center = Offset(0.5f, 0.3f),
                        radius = 600f
                    )
                )
        )

        content()
    }
}

@Composable
fun GlassmorphicSurface(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(Color(0x33000000))
            .border(
                width = 1.dp,
                color = GlassBorder,
                shape = RoundedCornerShape(cornerRadius)
            ),
        content = content
    )
}

@Composable
fun GlowingCard(
    modifier: Modifier = Modifier,
    glowColor: Color,
    cornerRadius: Dp = 24.dp,
    glowIntensity: Float = 0.3f,
    enablePulse: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    // Pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val effectiveIntensity = if (enablePulse) {
        glowIntensity * pulseAlpha
    } else {
        glowIntensity
    }

    Box(modifier = modifier) {
        // Outer glow layer 1 (furthest) - soft radial gradient
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(horizontal = 10.dp, vertical = 8.dp)
                .blur(36.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            glowColor.copy(alpha = effectiveIntensity * 0.4f),
                            glowColor.copy(alpha = effectiveIntensity * 0.15f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(cornerRadius + 12.dp)
                )
        )

        // Outer glow layer 2 (middle)
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(horizontal = 6.dp, vertical = 4.dp)
                .blur(22.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            glowColor.copy(alpha = effectiveIntensity * 0.6f),
                            glowColor.copy(alpha = effectiveIntensity * 0.25f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(cornerRadius + 6.dp)
                )
        )

        // Outer glow layer 3 (closest, brightest)
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(horizontal = 4.dp, vertical = 2.dp)
                .blur(14.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            glowColor.copy(alpha = effectiveIntensity * 0.85f),
                            glowColor.copy(alpha = effectiveIntensity * 0.35f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(cornerRadius + 2.dp)
                )
        )

        // Card content
        GlassmorphicCard(
            modifier = Modifier.matchParentSize(),
            cornerRadius = cornerRadius,
            glowColor = glowColor,
            content = content
        )
    }
}

@Composable
fun PremiumFloatingCard(
    modifier: Modifier = Modifier,
    glowColor: Color,
    cornerRadius: Dp = 24.dp,
    enableShimmer: Boolean = true,
    enableFloat: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "premium")

    // Floating animation
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    // Subtle rotation
    val rotation by infiniteTransition.animateFloat(
        initialValue = -0.5f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )

    val yOffset = if (enableFloat) (floatOffset * 8f - 4f).dp else 0.dp

    Box(
        modifier = modifier
            .offset(y = yOffset)
            .graphicsLayer {
                if (enableFloat) {
                    rotationZ = rotation
                }
            }
    ) {
        // Multi-layer glow - soft radial gradients
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .offset(y = 6.dp)
                .blur(32.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            glowColor.copy(alpha = 0.45f),
                            glowColor.copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(cornerRadius + 8.dp)
                )
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(horizontal = 4.dp, vertical = 2.dp)
                .offset(y = 3.dp)
                .blur(18.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            glowColor.copy(alpha = 0.6f),
                            glowColor.copy(alpha = 0.25f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(cornerRadius + 4.dp)
                )
        )

        GlassmorphicCard(
            modifier = Modifier.matchParentSize(),
            cornerRadius = cornerRadius,
            glowColor = glowColor,
            enableShimmerBorder = enableShimmer,
            enableFloatingAnimation = false,
            content = content
        )
    }
}

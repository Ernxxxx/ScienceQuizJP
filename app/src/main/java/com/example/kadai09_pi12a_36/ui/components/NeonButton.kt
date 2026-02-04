package com.example.kadai09_pi12a_36.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.kadai09_pi12a_36.ui.theme.QuizTheme

@Composable
fun NeonButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    glowColor: Color? = null,
    cornerRadius: Dp = 24.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 32.dp, vertical = 18.dp),
    enablePulse: Boolean = false,
    enableShimmer: Boolean = true
) {
    val categoryColors = QuizTheme.categoryColors
    val effectiveGlowColor = glowColor ?: categoryColors.primary

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Scale animation on press
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 500f
        ),
        label = "scale"
    )

    // Glow intensifies on press
    val glowAlpha by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 0.5f,
        animationSpec = spring(stiffness = 400f),
        label = "glow"
    )

    // Y offset for press depth effect
    val yOffset by animateFloatAsState(
        targetValue = if (isPressed) 2f else 0f,
        animationSpec = spring(stiffness = 500f),
        label = "yOffset"
    )

    // Shimmer animation
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerOffset"
    )

    // Pulse animation for CTA
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseGlow"
    )

    val finalScale = if (enablePulse && enabled && !isPressed) {
        scale * pulseScale
    } else {
        scale
    }

    val finalGlowAlpha = if (enablePulse && enabled && !isPressed) {
        pulseGlow
    } else {
        glowAlpha
    }

    Box(
        modifier = modifier
            .scale(finalScale)
            .offset(y = yOffset.dp),
        contentAlignment = Alignment.Center
    ) {
        // Multi-layer glow effect - softer, radial gradients to avoid hard edges
        if (enabled) {
            // Outermost soft glow
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .blur(36.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                effectiveGlowColor.copy(alpha = finalGlowAlpha * 0.5f),
                                effectiveGlowColor.copy(alpha = finalGlowAlpha * 0.2f),
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(cornerRadius + 12.dp)
                    )
            )

            // Middle glow
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                    .blur(24.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                effectiveGlowColor.copy(alpha = finalGlowAlpha * 0.7f),
                                effectiveGlowColor.copy(alpha = finalGlowAlpha * 0.3f),
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(cornerRadius + 8.dp)
                    )
            )

            // Inner glow (brightest)
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(horizontal = 6.dp, vertical = 4.dp)
                    .blur(14.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                effectiveGlowColor.copy(alpha = finalGlowAlpha * 0.9f),
                                effectiveGlowColor.copy(alpha = finalGlowAlpha * 0.4f),
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(cornerRadius + 4.dp)
                    )
            )
        }

        // Button
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(cornerRadius))
                .background(
                    brush = if (enabled) {
                        Brush.verticalGradient(
                            colors = listOf(
                                effectiveGlowColor.copy(alpha = 1f),
                                effectiveGlowColor.copy(alpha = 0.85f),
                                effectiveGlowColor.copy(alpha = 0.7f)
                            )
                        )
                    } else {
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF424242),
                                Color(0xFF303030)
                            )
                        )
                    }
                )
                // Shimmer overlay
                .then(
                    if (enabled && enableShimmer) {
                        Modifier.drawWithContent {
                            drawContent()
                            val shimmerWidth = size.width * 0.4f
                            val shimmerStart = shimmerOffset * size.width
                            drawRect(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.White.copy(alpha = 0.3f),
                                        Color.White.copy(alpha = 0.5f),
                                        Color.White.copy(alpha = 0.3f),
                                        Color.Transparent
                                    ),
                                    start = Offset(shimmerStart, 0f),
                                    end = Offset(shimmerStart + shimmerWidth, size.height)
                                )
                            )
                        }
                    } else {
                        Modifier
                    }
                )
                .border(
                    width = 1.5.dp,
                    brush = Brush.verticalGradient(
                        colors = if (enabled) {
                            listOf(
                                Color.White.copy(alpha = 0.6f),
                                Color.White.copy(alpha = 0.2f),
                                Color.White.copy(alpha = 0.1f)
                            )
                        } else {
                            listOf(
                                Color.White.copy(alpha = 0.1f),
                                Color.White.copy(alpha = 0.05f)
                            )
                        }
                    ),
                    shape = RoundedCornerShape(cornerRadius)
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled,
                    onClick = onClick
                )
                .padding(contentPadding),
            contentAlignment = Alignment.Center
        ) {
            // Inner highlight at top
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.2f),
                                Color.Transparent,
                                Color.Transparent
                            ),
                            startY = 0f,
                            endY = 80f
                        )
                    )
            )

            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (enabled) Color.White else Color(0xFF757575)
            )
        }
    }
}

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    gradientColors: List<Color>,
    enabled: Boolean = true,
    cornerRadius: Dp = 24.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 32.dp, vertical = 18.dp),
    enableShimmer: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 500f
        ),
        label = "scale"
    )

    val yOffset by animateFloatAsState(
        targetValue = if (isPressed) 2f else 0f,
        animationSpec = spring(stiffness = 500f),
        label = "yOffset"
    )

    // Shimmer animation
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerOffset"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .offset(y = yOffset.dp),
        contentAlignment = Alignment.Center
    ) {
        // Glow layers - soft radial gradients
        if (enabled && gradientColors.isNotEmpty()) {
            val buttonGlowColor = gradientColors.first()

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .blur(28.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                buttonGlowColor.copy(alpha = 0.5f),
                                buttonGlowColor.copy(alpha = 0.2f),
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(cornerRadius + 8.dp)
                    )
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(horizontal = 6.dp, vertical = 4.dp)
                    .blur(14.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                buttonGlowColor.copy(alpha = 0.7f),
                                buttonGlowColor.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(cornerRadius + 4.dp)
                    )
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(cornerRadius))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = if (enabled) gradientColors else listOf(Color(0xFF424242), Color(0xFF303030))
                    )
                )
                .then(
                    if (enabled && enableShimmer) {
                        Modifier.drawWithContent {
                            drawContent()
                            val shimmerWidth = size.width * 0.4f
                            val shimmerStart = shimmerOffset * size.width
                            drawRect(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.White.copy(alpha = 0.25f),
                                        Color.White.copy(alpha = 0.4f),
                                        Color.White.copy(alpha = 0.25f),
                                        Color.Transparent
                                    ),
                                    start = Offset(shimmerStart, 0f),
                                    end = Offset(shimmerStart + shimmerWidth, size.height)
                                )
                            )
                        }
                    } else {
                        Modifier
                    }
                )
                .border(
                    width = 1.5.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.5f),
                            Color.White.copy(alpha = 0.15f),
                            Color.White.copy(alpha = 0.05f)
                        )
                    ),
                    shape = RoundedCornerShape(cornerRadius)
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled,
                    onClick = onClick
                )
                .padding(contentPadding),
            contentAlignment = Alignment.Center
        ) {
            // Top shine
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.15f),
                                Color.Transparent
                            ),
                            startY = 0f,
                            endY = 60f
                        )
                    )
            )

            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (enabled) Color.White else Color(0xFF757575)
            )
        }
    }
}

@Composable
fun PremiumCTAButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    glowColor: Color? = null,
    cornerRadius: Dp = 28.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 40.dp, vertical = 20.dp)
) {
    NeonButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        enabled = true,
        glowColor = glowColor,
        cornerRadius = cornerRadius,
        contentPadding = contentPadding,
        enablePulse = true,
        enableShimmer = true
    )
}

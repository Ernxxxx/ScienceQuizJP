package com.example.kadai09_pi12a_36.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kadai09_pi12a_36.ui.theme.CorrectGreen
import com.example.kadai09_pi12a_36.ui.theme.CorrectGreenGlow
import com.example.kadai09_pi12a_36.ui.theme.DarkSurfaceVariant
import com.example.kadai09_pi12a_36.ui.theme.GlassBorder
import com.example.kadai09_pi12a_36.ui.theme.GlassWhite
import com.example.kadai09_pi12a_36.ui.theme.IncorrectRed
import com.example.kadai09_pi12a_36.ui.theme.IncorrectRedGlow
import com.example.kadai09_pi12a_36.ui.theme.QuizTheme
import com.example.kadai09_pi12a_36.ui.theme.TextDisabled
import com.example.kadai09_pi12a_36.ui.theme.TextPrimary

enum class ChoiceState {
    DEFAULT,
    SELECTED,
    CORRECT,
    INCORRECT,
    DISABLED
}

@Composable
fun AnimatedChoiceCard(
    label: String,
    text: String,
    state: ChoiceState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val categoryColors = QuizTheme.categoryColors

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Scale animation
    val scale by animateFloatAsState(
        targetValue = when {
            !enabled || state == ChoiceState.DISABLED -> 1f
            isPressed -> 0.95f
            state == ChoiceState.SELECTED -> 1.02f
            else -> 1f
        },
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "scale"
    )

    // 3D press effect
    val rotationX by animateFloatAsState(
        targetValue = if (isPressed) 3f else 0f,
        animationSpec = spring(stiffness = 400f),
        label = "rotationX"
    )

    val yOffset by animateFloatAsState(
        targetValue = if (isPressed) 2f else 0f,
        animationSpec = spring(stiffness = 400f),
        label = "yOffset"
    )

    // Colors
    val backgroundColor by animateColorAsState(
        targetValue = when (state) {
            ChoiceState.DEFAULT -> GlassWhite
            ChoiceState.SELECTED -> categoryColors.primary.copy(alpha = 0.25f)
            ChoiceState.CORRECT -> CorrectGreen.copy(alpha = 0.2f)
            ChoiceState.INCORRECT -> IncorrectRed.copy(alpha = 0.2f)
            ChoiceState.DISABLED -> Color(0x08FFFFFF)
        },
        animationSpec = tween(300),
        label = "background"
    )

    val borderColor by animateColorAsState(
        targetValue = when (state) {
            ChoiceState.DEFAULT -> GlassBorder
            ChoiceState.SELECTED -> categoryColors.primary
            ChoiceState.CORRECT -> CorrectGreen
            ChoiceState.INCORRECT -> IncorrectRed
            ChoiceState.DISABLED -> Color(0x10FFFFFF)
        },
        animationSpec = tween(300),
        label = "border"
    )

    val glowColor = when (state) {
        ChoiceState.SELECTED -> categoryColors.glow
        ChoiceState.CORRECT -> CorrectGreenGlow
        ChoiceState.INCORRECT -> IncorrectRedGlow
        else -> Color.Transparent
    }

    val badgeColor = when (state) {
        ChoiceState.CORRECT -> CorrectGreen
        ChoiceState.INCORRECT -> IncorrectRed
        ChoiceState.SELECTED -> categoryColors.primary
        ChoiceState.DISABLED -> DarkSurfaceVariant.copy(alpha = 0.5f)
        else -> DarkSurfaceVariant
    }

    // Pulse animation for selected state
    val infiniteTransition = rememberInfiniteTransition(label = "choice")
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )

    val effectiveGlowAlpha = when (state) {
        ChoiceState.SELECTED -> glowPulse
        ChoiceState.CORRECT, ChoiceState.INCORRECT -> 0.6f
        else -> 0f
    }

    Box(
        modifier = modifier
            .scale(scale)
            .offset(y = yOffset.dp)
            .graphicsLayer {
                this.rotationX = rotationX
                cameraDistance = 12f * density
            }
    ) {
        // Glow layers - softer, more natural glow
        if (state == ChoiceState.SELECTED || state == ChoiceState.CORRECT || state == ChoiceState.INCORRECT) {
            // Outer soft glow (larger blur, more padding to avoid hard edges)
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                    .blur(28.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                glowColor.copy(alpha = effectiveGlowAlpha * 0.6f),
                                glowColor.copy(alpha = effectiveGlowAlpha * 0.3f),
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(28.dp)
                    )
            )

            // Inner glow (subtle, blended)
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(horizontal = 4.dp, vertical = 2.dp)
                    .blur(16.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                glowColor.copy(alpha = effectiveGlowAlpha * 0.8f),
                                glowColor.copy(alpha = effectiveGlowAlpha * 0.4f),
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
            )
        }

        // Card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(backgroundColor)
                .border(
                    width = 2.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            borderColor,
                            borderColor.copy(alpha = 0.5f)
                        )
                    ),
                    shape = RoundedCornerShape(22.dp)
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled && state != ChoiceState.DISABLED,
                    onClick = onClick
                )
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Label badge with gradient
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                badgeColor,
                                badgeColor.copy(alpha = 0.7f)
                            )
                        )
                    )
                    .border(
                        width = 1.5.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.4f),
                                Color.White.copy(alpha = 0.1f)
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 18.sp
                )
            }

            // Choice text
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 17.sp,
                    lineHeight = 24.sp
                ),
                color = when (state) {
                    ChoiceState.DISABLED -> TextDisabled
                    else -> TextPrimary
                },
                textDecoration = if (state == ChoiceState.DISABLED) TextDecoration.LineThrough else null,
                modifier = Modifier.weight(1f)
            )

            // Result indicator
            AnimatedVisibility(
                visible = state == ChoiceState.CORRECT || state == ChoiceState.INCORRECT,
                enter = scaleIn(
                    animationSpec = spring(
                        dampingRatio = 0.5f,
                        stiffness = 300f
                    )
                ) + fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    if (state == ChoiceState.CORRECT) CorrectGreen else IncorrectRed,
                                    if (state == ChoiceState.CORRECT) CorrectGreen.copy(alpha = 0.7f) else IncorrectRed.copy(alpha = 0.7f)
                                )
                            )
                        )
                        .border(
                            width = 2.dp,
                            color = Color.White.copy(alpha = 0.3f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (state == ChoiceState.CORRECT) "O" else "X",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

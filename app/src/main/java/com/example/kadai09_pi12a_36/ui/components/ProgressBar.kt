package com.example.kadai09_pi12a_36.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.kadai09_pi12a_36.ui.theme.CorrectGreen
import com.example.kadai09_pi12a_36.ui.theme.DarkSurfaceVariant
import com.example.kadai09_pi12a_36.ui.theme.GlassWhite
import com.example.kadai09_pi12a_36.ui.theme.QuizTheme

@Composable
fun SegmentedProgressBar(
    current: Int,
    total: Int,
    modifier: Modifier = Modifier,
    activeColor: Color? = null,
    inactiveColor: Color = DarkSurfaceVariant,
    segmentSpacing: Dp = 4.dp,
    cornerRadius: Dp = 8.dp,
    height: Dp = 12.dp
) {
    val categoryColors = QuizTheme.categoryColors
    val effectiveActiveColor = activeColor ?: categoryColors.primary

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        horizontalArrangement = Arrangement.spacedBy(segmentSpacing)
    ) {
        repeat(total) { index ->
            val isActive = index < current

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(cornerRadius))
                    .background(
                        if (isActive) {
                            Brush.horizontalGradient(
                                colors = listOf(
                                    effectiveActiveColor,
                                    effectiveActiveColor.copy(alpha = 0.7f)
                                )
                            )
                        } else {
                            Brush.horizontalGradient(
                                colors = listOf(inactiveColor, inactiveColor)
                            )
                        }
                    )
            )
        }
    }
}

@Composable
fun WaveProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    primaryColor: Color? = null,
    backgroundColor: Color = DarkSurfaceVariant,
    height: Dp = 16.dp,
    cornerRadius: Dp = 8.dp
) {
    val categoryColors = QuizTheme.categoryColors
    val effectiveColor = primaryColor ?: categoryColors.primary

    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(500),
        label = "progress"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
    ) {
        // Progress fill with gradient
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedProgress)
                .clip(RoundedCornerShape(cornerRadius))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            effectiveColor.copy(alpha = 0.8f),
                            effectiveColor
                        )
                    )
                )
        )

        // Shine effect
        if (animatedProgress > 0) {
            Box(
                modifier = Modifier
                    .fillMaxHeight(0.5f)
                    .fillMaxWidth(animatedProgress)
                    .clip(RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }
    }
}

@Composable
fun CircularProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    primaryColor: Color? = null,
    backgroundColor: Color = DarkSurfaceVariant,
    strokeWidth: Dp = 8.dp
) {
    val categoryColors = QuizTheme.categoryColors
    val effectiveColor = primaryColor ?: categoryColors.primary

    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(500),
        label = "progress"
    )

    Canvas(modifier = modifier.size(80.dp)) {
        val diameter = size.minDimension
        val radius = diameter / 2
        val stroke = strokeWidth.toPx()

        // Background circle
        drawCircle(
            color = backgroundColor,
            radius = radius - stroke / 2,
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )

        // Progress arc
        drawArc(
            brush = Brush.sweepGradient(
                colors = listOf(
                    effectiveColor.copy(alpha = 0.5f),
                    effectiveColor
                )
            ),
            startAngle = -90f,
            sweepAngle = 360f * animatedProgress,
            useCenter = false,
            topLeft = Offset(stroke / 2, stroke / 2),
            size = Size(diameter - stroke, diameter - stroke),
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun DotProgressIndicator(
    current: Int,
    total: Int,
    modifier: Modifier = Modifier,
    activeColor: Color? = null,
    inactiveColor: Color = DarkSurfaceVariant,
    correctIndices: Set<Int> = emptySet(),
    dotSize: Dp = 10.dp,
    spacing: Dp = 8.dp
) {
    val categoryColors = QuizTheme.categoryColors
    val effectiveActiveColor = activeColor ?: categoryColors.primary

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(total) { index ->
            val color = when {
                index in correctIndices -> CorrectGreen
                index < current -> effectiveActiveColor
                index == current -> effectiveActiveColor.copy(alpha = 0.5f)
                else -> inactiveColor
            }

            Box(
                modifier = Modifier
                    .size(dotSize)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

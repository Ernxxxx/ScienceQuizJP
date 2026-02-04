package com.example.kadai09_pi12a_36.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kadai09_pi12a_36.ui.components.GlassmorphicCard
import com.example.kadai09_pi12a_36.ui.components.NeonButton
import com.example.kadai09_pi12a_36.ui.components.SpaceBackground
import com.example.kadai09_pi12a_36.ui.theme.CorrectGreen
import com.example.kadai09_pi12a_36.ui.theme.CorrectGreenGlow
import com.example.kadai09_pi12a_36.ui.theme.IncorrectRed
import com.example.kadai09_pi12a_36.ui.theme.IncorrectRedGlow
import com.example.kadai09_pi12a_36.ui.theme.NeonCyan
import com.example.kadai09_pi12a_36.ui.theme.NeonGreen
import com.example.kadai09_pi12a_36.ui.theme.NeonOrange
import com.example.kadai09_pi12a_36.ui.theme.NeonPink
import com.example.kadai09_pi12a_36.ui.theme.NeonPurple
import com.example.kadai09_pi12a_36.ui.theme.QuizTheme
import com.example.kadai09_pi12a_36.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class ResultParticle(
    var x: Float,
    var y: Float,
    val velocityX: Float,
    val velocityY: Float,
    val size: Float,
    val color: Color,
    val rotation: Float,
    val rotationSpeed: Float,
    var alpha: Float
)

@Composable
fun ResultScreen(
    category: String,
    isCorrect: Boolean,
    correctAnswer: String,
    explanation: String,
    onNextClick: () -> Unit
) {
    val categoryColors = QuizTheme.categoryColors

    // Animation states
    val iconScale = remember { Animatable(0f) }
    val iconRotation = remember { Animatable(0f) }
    var showContent by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }
    var expandExplanation by remember { mutableStateOf(false) }

    // Shake animation for incorrect
    val shakeOffset = remember { Animatable(0f) }

    // Particles for correct answer
    val particles = remember {
        if (isCorrect) {
            List(40) {
                val angle = Random.nextFloat() * 360f
                val speed = Random.nextFloat() * 8f + 4f
                ResultParticle(
                    x = 0.5f,
                    y = 0.4f,
                    velocityX = cos(Math.toRadians(angle.toDouble())).toFloat() * speed * 0.01f,
                    velocityY = sin(Math.toRadians(angle.toDouble())).toFloat() * speed * 0.01f - 0.02f,
                    size = Random.nextFloat() * 12f + 6f,
                    color = listOf(CorrectGreen, NeonGreen, NeonCyan, NeonPurple, NeonPink, NeonOrange).random(),
                    rotation = Random.nextFloat() * 360f,
                    rotationSpeed = Random.nextFloat() * 10f - 5f,
                    alpha = 1f
                )
            }
        } else emptyList()
    }

    var particleTime by remember { mutableFloatStateOf(0f) }

    // Entrance animations
    LaunchedEffect(Unit) {
        // Icon entrance with overshoot
        iconScale.animateTo(
            targetValue = 1.3f,
            animationSpec = tween(300, easing = FastOutSlowInEasing)
        )
        iconScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )

        if (isCorrect) {
            // Rotation for correct
            iconRotation.animateTo(
                targetValue = 360f,
                animationSpec = tween(500, easing = FastOutSlowInEasing)
            )
        } else {
            // Shake for incorrect
            repeat(3) {
                shakeOffset.animateTo(15f, animationSpec = tween(50))
                shakeOffset.animateTo(-15f, animationSpec = tween(50))
            }
            shakeOffset.animateTo(0f, animationSpec = tween(50))
        }

        delay(200)
        showContent = true
        delay(400)
        showButton = true
    }

    // Particle animation
    LaunchedEffect(isCorrect) {
        if (isCorrect) {
            while (particleTime < 3f) {
                particleTime += 0.016f
                particles.forEach { particle ->
                    particle.x += particle.velocityX
                    particle.y += particle.velocityY + particleTime * 0.005f // gravity
                    particle.alpha = (1f - particleTime / 3f).coerceAtLeast(0f)
                }
                delay(16)
            }
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "result")

    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )

    val resultColor = if (isCorrect) CorrectGreen else IncorrectRed
    val resultGlowColor = if (isCorrect) CorrectGreenGlow else IncorrectRedGlow

    SpaceBackground {
        // Background flash
        if (particleTime < 0.3f && isCorrect) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = CorrectGreen.copy(alpha = (0.3f - particleTime).coerceAtLeast(0f))
                    )
            )
        }

        // Particle layer
        if (isCorrect && particleTime < 3f) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                particles.forEach { particle ->
                    if (particle.alpha > 0) {
                        rotate(
                            degrees = particle.rotation + particleTime * particle.rotationSpeed * 50f,
                            pivot = Offset(particle.x * size.width, particle.y * size.height)
                        ) {
                            // Draw star shape
                            val path = Path()
                            val cx = particle.x * size.width
                            val cy = particle.y * size.height
                            val outerRadius = particle.size
                            val innerRadius = particle.size * 0.4f

                            for (i in 0 until 10) {
                                val radius = if (i % 2 == 0) outerRadius else innerRadius
                                val angle = Math.toRadians((i * 36.0 - 90.0))
                                val x = cx + (radius * cos(angle)).toFloat()
                                val y = cy + (radius * sin(angle)).toFloat()
                                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                            }
                            path.close()

                            drawPath(path, particle.color.copy(alpha = particle.alpha))
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Result icon
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.offset(x = shakeOffset.value.dp)
            ) {
                // Outer glow - soft radial gradient
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .scale(iconScale.value * (1f + glowPulse * 0.1f))
                        .blur(40.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    resultGlowColor.copy(alpha = glowPulse * 0.8f),
                                    resultGlowColor.copy(alpha = glowPulse * 0.4f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )

                // Inner glow - soft radial gradient
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .scale(iconScale.value)
                        .blur(20.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    resultColor.copy(alpha = 0.7f),
                                    resultColor.copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )

                // Icon circle
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .scale(iconScale.value)
                        .rotate(iconRotation.value)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    resultColor,
                                    resultColor.copy(alpha = 0.8f)
                                )
                            )
                        )
                        .border(
                            width = 3.dp,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.6f),
                                    Color.White.copy(alpha = 0.1f)
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isCorrect) Icons.Default.Check else Icons.Default.Close,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(56.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Result text
            Text(
                text = if (isCorrect) "正解!" else "不正解...",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black,
                color = resultColor,
                modifier = Modifier.scale(iconScale.value)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Answer card
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(tween(400)) + slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = tween(400)
                )
            ) {
                GlassmorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 24.dp,
                    glowColor = resultColor
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "正解は",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = correctAnswer,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Explanation card (expandable)
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(tween(400, delayMillis = 200)) + slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = tween(400, delayMillis = 200)
                )
            ) {
                GlassmorphicCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandExplanation = !expandExplanation },
                    cornerRadius = 24.dp,
                    glowColor = categoryColors.primary
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        // Header row
                        Box(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "解説",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = categoryColors.accent,
                                modifier = Modifier.align(Alignment.CenterStart)
                            )

                            val rotationAngle by animateFloatAsState(
                                targetValue = if (expandExplanation) 180f else 0f,
                                animationSpec = tween(300),
                                label = "expandRotation"
                            )

                            Icon(
                                imageVector = Icons.Default.ExpandMore,
                                contentDescription = if (expandExplanation) "閉じる" else "開く",
                                tint = TextSecondary,
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .rotate(rotationAngle)
                            )
                        }

                        // Explanation text
                        AnimatedVisibility(
                            visible = expandExplanation,
                            enter = fadeIn() + slideInVertically()
                        ) {
                            Text(
                                text = explanation,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White,
                                lineHeight = 26.sp,
                                modifier = Modifier.padding(top = 16.dp)
                            )
                        }

                        if (!expandExplanation) {
                            Text(
                                text = "タップして表示",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Next button
            AnimatedVisibility(
                visible = showButton,
                enter = fadeIn(tween(400)) + slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(400)
                )
            ) {
                NeonButton(
                    text = "次の問題へ",
                    onClick = onNextClick,
                    modifier = Modifier.fillMaxWidth(),
                    glowColor = categoryColors.primary,
                    enablePulse = true
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

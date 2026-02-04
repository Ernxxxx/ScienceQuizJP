package com.example.kadai09_pi12a_36.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kadai09_pi12a_36.ui.components.GlassmorphicCard
import com.example.kadai09_pi12a_36.ui.components.NeonButton
import com.example.kadai09_pi12a_36.ui.components.SpaceBackground
import com.example.kadai09_pi12a_36.ui.theme.CorrectGreen
import com.example.kadai09_pi12a_36.ui.theme.IncorrectRed
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

private data class ConfettiStar(
    val x: Float,
    val y: Float,
    val size: Float,
    val rotation: Float,
    val color: Color,
    val speed: Float,
    val rotationSpeed: Float
)

@Composable
fun SetResultScreen(
    setCorrect: Int,
    totalCorrect: Int,
    totalAnswered: Int,
    hasMore: Boolean,
    onContinueClick: () -> Unit,
    onHomeClick: () -> Unit
) {
    val categoryColors = QuizTheme.categoryColors
    val questionsPerSet = 10 // Assuming 10 questions per set
    val isPerfect = setCorrect == questionsPerSet
    val percentage = setCorrect.toFloat() / questionsPerSet

    // Animation states
    val progressAnimation = remember { Animatable(0f) }
    var showTitle by remember { mutableStateOf(false) }
    var showScore by remember { mutableStateOf(false) }
    var showSummary by remember { mutableStateOf(false) }
    var showButtons by remember { mutableStateOf(false) }

    // Title celebration animation
    val infiniteTransition = rememberInfiniteTransition(label = "celebration")
    val titleScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "titleScale"
    )

    val titleGlow by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "titleGlow"
    )

    // Confetti stars for perfect score
    val confettiStars = remember {
        if (isPerfect) {
            List(30) {
                ConfettiStar(
                    x = Random.nextFloat(),
                    y = Random.nextFloat() * 0.3f - 0.1f,
                    size = Random.nextFloat() * 20f + 10f,
                    rotation = Random.nextFloat() * 360f,
                    color = listOf(
                        NeonPurple, NeonCyan, NeonPink, NeonGreen, NeonOrange, CorrectGreen
                    ).random(),
                    speed = Random.nextFloat() * 0.003f + 0.001f,
                    rotationSpeed = Random.nextFloat() * 2f - 1f
                )
            }
        } else emptyList()
    }

    var confettiProgress by remember { mutableStateOf(0f) }

    // Sequential animations
    LaunchedEffect(Unit) {
        delay(100)
        showTitle = true
        delay(300)
        showScore = true
        progressAnimation.animateTo(
            targetValue = percentage,
            animationSpec = tween(1000, easing = FastOutSlowInEasing)
        )
        delay(200)
        showSummary = true
        delay(200)
        showButtons = true
    }

    // Confetti animation
    LaunchedEffect(isPerfect) {
        if (isPerfect) {
            while (true) {
                confettiProgress += 0.01f
                delay(16)
            }
        }
    }

    val scoreColor = when {
        percentage >= 1f -> CorrectGreen
        percentage >= 0.7f -> categoryColors.accent
        percentage >= 0.4f -> categoryColors.primary
        else -> IncorrectRed
    }

    val message = when {
        percentage >= 1f -> "パーフェクト!"
        percentage >= 0.7f -> "よくできました!"
        percentage >= 0.4f -> "まずまずです!"
        else -> "もう少し頑張ろう!"
    }

    SpaceBackground {
        // Confetti layer for perfect scores
        if (isPerfect) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                confettiStars.forEach { star ->
                    val currentY = star.y + confettiProgress * star.speed * 300f
                    val normalizedY = (currentY % 1.2f)
                    val currentRotation = star.rotation + confettiProgress * star.rotationSpeed * 100f

                    if (normalizedY < 1.1f) {
                        rotate(
                            degrees = currentRotation,
                            pivot = Offset(
                                star.x * size.width,
                                normalizedY * size.height
                            )
                        ) {
                            drawStar(
                                center = Offset(
                                    star.x * size.width,
                                    normalizedY * size.height
                                ),
                                size = star.size,
                                color = star.color.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Title with celebration animation
            val titleAlpha by animateFloatAsState(
                targetValue = if (showTitle) 1f else 0f,
                animationSpec = tween(400),
                label = "titleAlpha"
            )

            Box(
                modifier = Modifier
                    .alpha(titleAlpha)
                    .scale(if (showTitle) titleScale else 0.8f)
            ) {
                Text(
                    text = "セット完了!",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = titleGlow),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Score display with CircularProgressIndicator
            val scoreAlpha by animateFloatAsState(
                targetValue = if (showScore) 1f else 0f,
                animationSpec = tween(400),
                label = "scoreAlpha"
            )

            val scoreScale by animateFloatAsState(
                targetValue = if (showScore) 1f else 0.5f,
                animationSpec = tween(600, easing = FastOutSlowInEasing),
                label = "scoreScale"
            )

            GlassmorphicCard(
                modifier = Modifier
                    .alpha(scoreAlpha)
                    .scale(scoreScale)
                    .padding(16.dp),
                glowColor = scoreColor
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(180.dp)
                    ) {
                        // Background track
                        CircularProgressIndicator(
                            progress = { 1f },
                            modifier = Modifier.fillMaxSize(),
                            color = Color.White.copy(alpha = 0.1f),
                            strokeWidth = 12.dp,
                            strokeCap = StrokeCap.Round
                        )

                        // Animated progress
                        CircularProgressIndicator(
                            progress = { progressAnimation.value },
                            modifier = Modifier.fillMaxSize(),
                            color = scoreColor,
                            strokeWidth = 12.dp,
                            strokeCap = StrokeCap.Round
                        )

                        // Score text
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "$setCorrect/$questionsPerSet",
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold,
                                color = scoreColor
                            )
                            Text(
                                text = "正解",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Message
                    Text(
                        text = message,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = scoreColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Total score summary card
            val summaryAlpha by animateFloatAsState(
                targetValue = if (showSummary) 1f else 0f,
                animationSpec = tween(400),
                label = "summaryAlpha"
            )

            val summaryTranslation by animateFloatAsState(
                targetValue = if (showSummary) 0f else 30f,
                animationSpec = tween(400, easing = FastOutSlowInEasing),
                label = "summaryTranslation"
            )

            GlassmorphicCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(summaryAlpha)
                    .padding(top = summaryTranslation.dp),
                glowColor = categoryColors.primary
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "累計スコア",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "$totalCorrect",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = categoryColors.accent
                        )
                        Text(
                            text = " / ",
                            style = MaterialTheme.typography.headlineMedium,
                            color = TextSecondary
                        )
                        Text(
                            text = "$totalAnswered",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = " 正解",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextSecondary
                        )
                    }

                    if (totalAnswered > 0) {
                        val totalPercentage = (totalCorrect.toFloat() / totalAnswered * 100).toInt()
                        Text(
                            text = "正答率: $totalPercentage%",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Buttons
            val buttonsAlpha by animateFloatAsState(
                targetValue = if (showButtons) 1f else 0f,
                animationSpec = tween(400),
                label = "buttonsAlpha"
            )

            val buttonsTranslation by animateFloatAsState(
                targetValue = if (showButtons) 0f else 30f,
                animationSpec = tween(400, easing = FastOutSlowInEasing),
                label = "buttonsTranslation"
            )

            Column(
                modifier = Modifier
                    .alpha(buttonsAlpha)
                    .padding(top = buttonsTranslation.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (hasMore) {
                    NeonButton(
                        text = "次のセットへ",
                        onClick = onContinueClick,
                        modifier = Modifier.fillMaxWidth(),
                        glowColor = categoryColors.primary
                    )
                }

                NeonButton(
                    text = "ホームに戻る",
                    onClick = onHomeClick,
                    modifier = Modifier.fillMaxWidth(),
                    glowColor = if (hasMore) Color(0xFF616161) else categoryColors.primary
                )
            }
        }
    }
}

// Extension function to draw a star shape
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawStar(
    center: Offset,
    size: Float,
    color: Color
) {
    val path = Path()
    val outerRadius = size / 2
    val innerRadius = outerRadius * 0.4f
    val points = 5

    for (i in 0 until points * 2) {
        val radius = if (i % 2 == 0) outerRadius else innerRadius
        val angle = Math.toRadians((i * 36.0 - 90.0))
        val x = center.x + (radius * cos(angle)).toFloat()
        val y = center.y + (radius * sin(angle)).toFloat()

        if (i == 0) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
    }
    path.close()

    drawPath(path, color)
}

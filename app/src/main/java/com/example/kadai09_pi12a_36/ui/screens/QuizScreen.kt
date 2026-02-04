package com.example.kadai09_pi12a_36.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kadai09_pi12a_36.ui.components.CategoryBackground
import com.example.kadai09_pi12a_36.ui.theme.DarkSurfaceVariant
import com.example.kadai09_pi12a_36.ui.theme.GlassBorder
import com.example.kadai09_pi12a_36.ui.theme.GlassWhite
import com.example.kadai09_pi12a_36.ui.theme.QuizTheme
import com.example.kadai09_pi12a_36.ui.theme.TextPrimary
import com.example.kadai09_pi12a_36.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun QuizScreen(
    category: String,
    level: Int,
    questionNumber: Int,
    totalInSet: Int,
    question: String,
    choices: List<String>,
    selectedIndex: Int?,
    correctCount: Int,
    totalAnswered: Int,
    hintUsed: Boolean,
    hintDisabledIndices: Set<Int>,
    onChoiceSelected: (Int) -> Unit,
    onAnswerClick: () -> Unit,
    onHintClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val categoryColors = QuizTheme.categoryColors
    val choiceLabels = listOf("A", "B", "C", "D")

    // Track visibility for staggered animations
    var showContent by remember { mutableStateOf(false) }
    LaunchedEffect(question) {
        showContent = false
        delay(50)
        showContent = true
    }

    CategoryBackground(category = category) {
        // Floating particles background
        ParticleBackground(
            particleColor = categoryColors.primary.copy(alpha = 0.3f)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Premium Header with circular progress
            PremiumHeader(
                category = category,
                level = level,
                questionNumber = questionNumber,
                totalInSet = totalInSet,
                correctCount = correctCount,
                totalAnswered = totalAnswered,
                onBackClick = onBackClick
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Scrollable content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                // Floating Question Card
                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn(tween(400)) + scaleIn(
                        initialScale = 0.9f,
                        animationSpec = tween(400, easing = FastOutSlowInEasing)
                    )
                ) {
                    FloatingQuestionCard(
                        question = question,
                        accentColor = categoryColors.primary
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Choice cards with staggered animation
                choices.forEachIndexed { index, choiceText ->
                    val isDisabled = index in hintDisabledIndices
                    val isSelected = selectedIndex == index

                    AnimatedVisibility(
                        visible = showContent,
                        enter = slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(
                                durationMillis = 400,
                                delayMillis = 100 + index * 80,
                                easing = FastOutSlowInEasing
                            )
                        ) + fadeIn(
                            animationSpec = tween(
                                durationMillis = 300,
                                delayMillis = 100 + index * 80
                            )
                        )
                    ) {
                        PremiumChoiceCard(
                            label = choiceLabels.getOrElse(index) { "${index + 1}" },
                            text = choiceText,
                            isSelected = isSelected,
                            isDisabled = isDisabled,
                            accentColor = categoryColors.primary,
                            glowColor = categoryColors.glow,
                            onClick = { onChoiceSelected(index) }
                        )
                    }

                    if (index < choices.lastIndex) {
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Bottom action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Floating Hint Button
                PulsingHintButton(
                    enabled = !hintUsed,
                    onClick = onHintClick,
                    modifier = Modifier.weight(1f)
                )

                // Premium Answer Button
                PremiumAnswerButton(
                    text = "回答する",
                    enabled = selectedIndex != null,
                    glowColor = categoryColors.primary,
                    onClick = onAnswerClick,
                    modifier = Modifier.weight(2f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ============== Particle Background ==============

@Composable
private fun ParticleBackground(
    particleColor: Color
) {
    val infiniteTransition = rememberInfiniteTransition(label = "particles")

    val particles = remember {
        List(30) {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 4f + 2f,
                speed = Random.nextFloat() * 0.5f + 0.2f,
                alpha = Random.nextFloat() * 0.4f + 0.1f
            )
        }
    }

    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particleOffset"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val yOffset = (particle.y + animatedOffset * particle.speed) % 1f
            drawCircle(
                color = particleColor.copy(alpha = particle.alpha),
                radius = particle.size,
                center = Offset(
                    x = particle.x * size.width,
                    y = yOffset * size.height
                )
            )
        }
    }
}

private data class Particle(
    val x: Float,
    val y: Float,
    val size: Float,
    val speed: Float,
    val alpha: Float
)

// ============== Premium Header ==============

@Composable
private fun PremiumHeader(
    category: String,
    level: Int,
    questionNumber: Int,
    totalInSet: Int,
    correctCount: Int,
    totalAnswered: Int,
    onBackClick: () -> Unit
) {
    val categoryColors = QuizTheme.categoryColors

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Glowing Back Button
        GlowingBackButton(
            onClick = onBackClick,
            glowColor = categoryColors.primary
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Circular Progress Ring with Question Number
        CircularProgressRing(
            current = questionNumber,
            total = totalInSet,
            primaryColor = categoryColors.primary,
            glowColor = categoryColors.glow
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Category & Level badges
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            GlowingBadge(
                text = category,
                backgroundColor = categoryColors.primary.copy(alpha = 0.2f),
                borderColor = categoryColors.primary,
                textColor = categoryColors.primary,
                glowColor = categoryColors.glow
            )
            Badge(
                text = "Lv.$level",
                backgroundColor = GlassWhite,
                borderColor = GlassBorder,
                textColor = TextPrimary
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Animated Score Display
        AnimatedScoreDisplay(
            correctCount = correctCount,
            totalAnswered = totalAnswered,
            primaryColor = categoryColors.primary
        )
    }
}

@Composable
private fun GlowingBackButton(
    onClick: () -> Unit,
    glowColor: Color
) {
    val infiniteTransition = rememberInfiniteTransition(label = "backGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "backGlowAlpha"
    )

    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(GlassWhite)
            .border(1.dp, GlassBorder, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        // Subtle glow
        Box(
            modifier = Modifier
                .size(44.dp)
                .blur(8.dp)
                .background(
                    color = glowColor.copy(alpha = glowAlpha),
                    shape = CircleShape
                )
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = TextPrimary,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
private fun CircularProgressRing(
    current: Int,
    total: Int,
    primaryColor: Color,
    glowColor: Color,
    size: Dp = 56.dp,
    strokeWidth: Dp = 5.dp
) {
    val progress = current.toFloat() / total.toFloat()
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "ringProgress"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "ringGlow")
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ringGlowPulse"
    )

    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Glow behind
        Canvas(modifier = Modifier.size(size)) {
            drawCircle(
                color = glowColor.copy(alpha = glowPulse * 0.3f),
                radius = this.size.minDimension / 2
            )
        }

        // Progress ring
        Canvas(modifier = Modifier.size(size)) {
            val stroke = strokeWidth.toPx()
            val diameter = this.size.minDimension
            val radius = diameter / 2 - stroke / 2

            // Background ring
            drawCircle(
                color = DarkSurfaceVariant,
                radius = radius,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )

            // Progress arc
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.6f),
                        primaryColor,
                        primaryColor.copy(alpha = 0.8f)
                    )
                ),
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }

        // Question number with glow
        Text(
            text = "$current",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = primaryColor
        )
    }
}

@Composable
private fun GlowingBadge(
    text: String,
    backgroundColor: Color,
    borderColor: Color,
    textColor: Color,
    glowColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
private fun Badge(
    text: String,
    backgroundColor: Color,
    borderColor: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
private fun AnimatedScoreDisplay(
    correctCount: Int,
    totalAnswered: Int,
    primaryColor: Color
) {
    var displayedScore by remember { mutableIntStateOf(correctCount) }

    LaunchedEffect(correctCount) {
        if (displayedScore < correctCount) {
            for (i in displayedScore until correctCount) {
                delay(80)
                displayedScore = i + 1
            }
        } else {
            displayedScore = correctCount
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "scoreGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scoreGlowAlpha"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.15f),
                        primaryColor.copy(alpha = 0.25f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = glowAlpha),
                        primaryColor.copy(alpha = glowAlpha + 0.2f)
                    )
                ),
                shape = RoundedCornerShape(18.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "$displayedScore",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = primaryColor
            )
            Text(
                text = "/",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )
            Text(
                text = "$totalAnswered",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )
        }
    }
}

// ============== Floating Question Card ==============

@Composable
private fun FloatingQuestionCard(
    question: String,
    accentColor: Color
) {
    val infiniteTransition = rememberInfiniteTransition(label = "float")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatOffset"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = floatOffset.dp)
    ) {
        // Glow behind card
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(8.dp)
                .blur(24.dp)
                .background(
                    color = accentColor.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(28.dp)
                )
        )

        // Main glassmorphic card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            GlassWhite,
                            Color(0x0DFFFFFF)
                        )
                    )
                )
                .border(
                    width = 2.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.5f),
                            accentColor.copy(alpha = 0.2f),
                            GlassBorder
                        )
                    ),
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(28.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = question,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 22.sp,
                    lineHeight = 32.sp
                ),
                fontWeight = FontWeight.Medium,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ============== Premium Choice Card ==============

@Composable
private fun PremiumChoiceCard(
    label: String,
    text: String,
    isSelected: Boolean,
    isDisabled: Boolean,
    accentColor: Color,
    glowColor: Color,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = when {
            isDisabled -> 1f
            isPressed -> 0.96f
            isSelected -> 1.02f
            else -> 1f
        },
        animationSpec = spring(stiffness = 400f),
        label = "choiceScale"
    )

    val borderWidth by animateFloatAsState(
        targetValue = if (isSelected) 2.5f else 1.5f,
        animationSpec = tween(200),
        label = "borderWidth"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "neonPulse")
    val neonGlow by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "neonGlowAlpha"
    )

    val backgroundColor = when {
        isDisabled -> Color(0x08FFFFFF)
        isSelected -> accentColor.copy(alpha = 0.2f)
        else -> GlassWhite
    }

    val borderColor = when {
        isDisabled -> Color(0x1AFFFFFF)
        isSelected -> accentColor.copy(alpha = neonGlow)
        else -> GlassBorder
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
    ) {
        // Neon glow effect when selected
        if (isSelected) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(4.dp)
                    .blur(16.dp)
                    .background(
                        color = glowColor.copy(alpha = neonGlow * 0.5f),
                        shape = RoundedCornerShape(22.dp)
                    )
            )
        }

        // Card content
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(backgroundColor)
                .border(
                    width = borderWidth.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(22.dp)
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = !isDisabled,
                    onClick = onClick
                )
                .padding(horizontal = 18.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Letter badge with gradient
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        brush = if (isDisabled) {
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF424242),
                                    Color(0xFF303030)
                                )
                            )
                        } else if (isSelected) {
                            Brush.verticalGradient(
                                colors = listOf(
                                    accentColor,
                                    accentColor.copy(alpha = 0.7f)
                                )
                            )
                        } else {
                            Brush.verticalGradient(
                                colors = listOf(
                                    DarkSurfaceVariant,
                                    DarkSurfaceVariant.copy(alpha = 0.8f)
                                )
                            )
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isDisabled) Color(0xFF757575) else Color.White
                )
            }

            // Choice text
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 17.sp
                ),
                color = if (isDisabled) Color(0xFF616161) else TextPrimary,
                textDecoration = if (isDisabled) TextDecoration.LineThrough else TextDecoration.None,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// ============== Pulsing Hint Button ==============

@Composable
private fun PulsingHintButton(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "hintPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (enabled) 1.05f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hintPulseScale"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = if (enabled) 0.6f else 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hintGlowAlpha"
    )

    val backgroundColor = if (enabled) {
        Color(0xFF8D6E63).copy(alpha = 0.25f)
    } else {
        Color(0xFF424242).copy(alpha = 0.2f)
    }

    val borderColor = if (enabled) {
        Color(0xFFFFD54F).copy(alpha = glowAlpha)
    } else {
        Color(0xFF616161)
    }

    val iconColor = if (enabled) Color(0xFFFFD54F) else Color(0xFF757575)

    Box(
        modifier = modifier
            .height(58.dp)
            .scale(pulseScale)
    ) {
        // Glow effect when enabled
        if (enabled) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(4.dp)
                    .blur(12.dp)
                    .background(
                        color = Color(0xFFFFD54F).copy(alpha = glowAlpha * 0.4f),
                        shape = RoundedCornerShape(26.dp)
                    )
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(26.dp))
                .background(backgroundColor)
                .border(1.5f.dp, borderColor, RoundedCornerShape(26.dp))
                .clickable(enabled = enabled, onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = "Hint",
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ヒント",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = iconColor
                )
            }
        }
    }
}

// ============== Premium Answer Button ==============

@Composable
private fun PremiumAnswerButton(
    text: String,
    enabled: Boolean,
    glowColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(stiffness = 400f),
        label = "answerScale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "answerGlow")
    val glowIntensity by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = if (enabled) 0.7f else 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "answerGlowIntensity"
    )

    Box(
        modifier = modifier
            .height(58.dp)
            .scale(scale),
        contentAlignment = Alignment.Center
    ) {
        // Glow layer
        if (enabled) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(6.dp)
                    .blur(20.dp)
                    .background(
                        color = glowColor.copy(alpha = glowIntensity),
                        shape = RoundedCornerShape(26.dp)
                    )
            )
        }

        // Button
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(26.dp))
                .background(
                    brush = if (enabled) {
                        Brush.horizontalGradient(
                            colors = listOf(
                                glowColor,
                                glowColor.copy(alpha = 0.85f)
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
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.35f),
                            Color.White.copy(alpha = 0.1f)
                        )
                    ),
                    shape = RoundedCornerShape(26.dp)
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled,
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 18.sp
                ),
                fontWeight = FontWeight.Bold,
                color = if (enabled) Color.White else Color(0xFF757575)
            )
        }
    }
}

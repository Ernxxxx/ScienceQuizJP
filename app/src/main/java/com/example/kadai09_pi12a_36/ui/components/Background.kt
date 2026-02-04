package com.example.kadai09_pi12a_36.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import com.example.kadai09_pi12a_36.ui.theme.CategoryColors
import com.example.kadai09_pi12a_36.ui.theme.DarkBackground
import com.example.kadai09_pi12a_36.ui.theme.QuizTheme
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// ============================================
// Data Classes for Particles and Effects
// ============================================

data class Star(
    val x: Float,
    val y: Float,
    val radius: Float,
    val baseAlpha: Float,
    val twinkleSpeed: Float,
    val layer: Int // 0 = far, 1 = mid, 2 = close
)

data class ShootingStar(
    var x: Float,
    var y: Float,
    val angle: Float,
    val speed: Float,
    val length: Float,
    var alpha: Float,
    var active: Boolean
)

data class NebulaBlob(
    var x: Float,
    var y: Float,
    val radius: Float,
    val color: Color,
    val driftSpeedX: Float,
    val driftSpeedY: Float,
    val pulseSpeed: Float
)

data class Particle(
    var x: Float,
    var y: Float,
    val size: Float,
    var alpha: Float,
    val speedY: Float,
    val speedX: Float,
    val color: Color
)

data class ElectricArc(
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float,
    val segments: List<Offset>,
    var alpha: Float,
    var active: Boolean
)

data class Molecule(
    var x: Float,
    var y: Float,
    val rotation: Float,
    val scale: Float,
    var alpha: Float,
    val driftSpeed: Float
)

data class Cell(
    var x: Float,
    var y: Float,
    val radius: Float,
    val nucleusRadius: Float,
    var alpha: Float,
    val pulsePhase: Float
)

data class Crystal(
    var x: Float,
    var y: Float,
    val size: Float,
    val rotation: Float,
    var alpha: Float,
    val shinePhase: Float
)

// ============================================
// SpaceBackground - Animated Starfield
// ============================================

@Composable
fun SpaceBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val categoryColors = QuizTheme.categoryColors

    // Infinite transition for animations
    val infiniteTransition = rememberInfiniteTransition(label = "space_transition")

    // Time-based animation for smooth updates
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(100000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    // Twinkle animation
    val twinkle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "twinkle"
    )

    // Nebula pulse animation
    val nebulaPulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "nebula_pulse"
    )

    // Stars - 3 layers for parallax effect
    val stars = remember {
        List(80) {
            Star(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                radius = when (Random.nextInt(3)) {
                    0 -> Random.nextFloat() * 0.8f + 0.3f  // Small/far
                    1 -> Random.nextFloat() * 1.2f + 0.8f  // Medium
                    else -> Random.nextFloat() * 2f + 1.5f // Large/close
                },
                baseAlpha = Random.nextFloat() * 0.4f + 0.4f,
                twinkleSpeed = Random.nextFloat() * 2f + 0.5f,
                layer = Random.nextInt(3)
            )
        }
    }

    // Shooting stars state
    var shootingStars by remember { mutableStateOf(listOf<ShootingStar>()) }

    // Spawn shooting stars occasionally
    LaunchedEffect(Unit) {
        while (true) {
            delay(Random.nextLong(3000, 8000))
            val newStar = ShootingStar(
                x = Random.nextFloat() * 0.3f,
                y = Random.nextFloat() * 0.5f,
                angle = Random.nextFloat() * 30f + 20f, // 20-50 degrees
                speed = Random.nextFloat() * 0.015f + 0.01f,
                length = Random.nextFloat() * 0.1f + 0.05f,
                alpha = 1f,
                active = true
            )
            shootingStars = shootingStars.filter { it.active } + newStar
        }
    }

    // Update shooting stars
    LaunchedEffect(time) {
        shootingStars = shootingStars.mapNotNull { star ->
            if (!star.active) return@mapNotNull null
            val radAngle = star.angle * PI.toFloat() / 180f
            star.x += cos(radAngle) * star.speed
            star.y += sin(radAngle) * star.speed
            star.alpha -= 0.02f
            if (star.alpha <= 0f || star.x > 1.5f || star.y > 1.5f) {
                star.active = false
            }
            star
        }.filter { it.active }
    }

    // Nebula blobs
    val nebulaBlobs = remember {
        listOf(
            NebulaBlob(0.2f, 0.3f, 300f, categoryColors.primary.copy(alpha = 0.08f), 0.0001f, 0.00005f, 1f),
            NebulaBlob(0.8f, 0.6f, 250f, categoryColors.secondary.copy(alpha = 0.06f), -0.00008f, 0.0001f, 1.3f),
            NebulaBlob(0.5f, 0.8f, 200f, categoryColors.accent.copy(alpha = 0.05f), 0.00006f, -0.00008f, 0.8f)
        )
    }

    // Floating particles
    val particles = remember {
        List(50) {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 3f + 1f,
                alpha = Random.nextFloat() * 0.5f + 0.2f,
                speedY = -(Random.nextFloat() * 0.0003f + 0.0001f),
                speedX = (Random.nextFloat() - 0.5f) * 0.0002f,
                color = listOf(
                    categoryColors.primary,
                    categoryColors.accent,
                    Color.White
                ).random()
            )
        }
    }

    // Update particles
    var particleTime by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(time) {
        particleTime = time
        particles.forEach { particle ->
            particle.y += particle.speedY * 10f
            particle.x += particle.speedX * 10f
            if (particle.y < -0.1f) {
                particle.y = 1.1f
                particle.x = Random.nextFloat()
            }
            if (particle.x < -0.1f) particle.x = 1.1f
            if (particle.x > 1.1f) particle.x = -0.1f
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        DarkBackground,
                        Color(0xFF0A0A15),
                        categoryColors.secondary.copy(alpha = 0.15f),
                        Color(0xFF0A0A15),
                        DarkBackground
                    )
                )
            )
    ) {
        // Nebula layer (background)
        Canvas(modifier = Modifier.fillMaxSize()) {
            nebulaBlobs.forEachIndexed { index, blob ->
                val pulseFactor = nebulaPulse * (1f + index * 0.1f)
                val adjustedX = blob.x + sin(time * blob.driftSpeedX) * 0.05f
                val adjustedY = blob.y + cos(time * blob.driftSpeedY) * 0.03f

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            blob.color.copy(alpha = blob.color.alpha * pulseFactor),
                            blob.color.copy(alpha = blob.color.alpha * 0.5f * pulseFactor),
                            Color.Transparent
                        ),
                        center = Offset(adjustedX * size.width, adjustedY * size.height),
                        radius = blob.radius * pulseFactor
                    ),
                    center = Offset(adjustedX * size.width, adjustedY * size.height),
                    radius = blob.radius * pulseFactor,
                    blendMode = BlendMode.Screen
                )
            }
        }

        // Stars layer with parallax
        Canvas(modifier = Modifier.fillMaxSize()) {
            stars.forEach { star ->
                // Parallax effect based on layer
                val parallaxOffset = when (star.layer) {
                    0 -> time * 0.00001f // Slow
                    1 -> time * 0.00003f // Medium
                    else -> time * 0.00006f // Fast
                }

                val adjustedY = (star.y + parallaxOffset) % 1f

                // Twinkle effect
                val twinkleAlpha = star.baseAlpha * (0.7f + 0.3f * sin(twinkle * star.twinkleSpeed))

                // Draw star glow
                if (star.radius > 1f) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = twinkleAlpha * 0.3f),
                                Color.Transparent
                            ),
                            center = Offset(star.x * size.width, adjustedY * size.height),
                            radius = star.radius * 4f
                        ),
                        center = Offset(star.x * size.width, adjustedY * size.height),
                        radius = star.radius * 4f
                    )
                }

                // Draw star core
                drawCircle(
                    color = Color.White.copy(alpha = twinkleAlpha),
                    radius = star.radius,
                    center = Offset(star.x * size.width, adjustedY * size.height)
                )
            }
        }

        // Shooting stars layer
        Canvas(modifier = Modifier.fillMaxSize()) {
            shootingStars.forEach { star ->
                if (star.active) {
                    val radAngle = star.angle * PI.toFloat() / 180f
                    val endX = star.x - cos(radAngle) * star.length
                    val endY = star.y - sin(radAngle) * star.length

                    // Draw shooting star trail
                    drawLine(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = star.alpha),
                                Color.White.copy(alpha = star.alpha * 0.5f),
                                Color.Transparent
                            ),
                            start = Offset(star.x * size.width, star.y * size.height),
                            end = Offset(endX * size.width, endY * size.height)
                        ),
                        start = Offset(star.x * size.width, star.y * size.height),
                        end = Offset(endX * size.width, endY * size.height),
                        strokeWidth = 2f
                    )

                    // Bright head
                    drawCircle(
                        color = Color.White.copy(alpha = star.alpha),
                        radius = 3f,
                        center = Offset(star.x * size.width, star.y * size.height)
                    )
                }
            }
        }

        // Floating particles layer
        Canvas(modifier = Modifier.fillMaxSize()) {
            particles.forEach { particle ->
                // Glow effect
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            particle.color.copy(alpha = particle.alpha * 0.5f),
                            Color.Transparent
                        ),
                        center = Offset(particle.x * size.width, particle.y * size.height),
                        radius = particle.size * 5f
                    ),
                    center = Offset(particle.x * size.width, particle.y * size.height),
                    radius = particle.size * 5f
                )

                // Core
                drawCircle(
                    color = particle.color.copy(alpha = particle.alpha),
                    radius = particle.size,
                    center = Offset(particle.x * size.width, particle.y * size.height)
                )
            }
        }

        // Gradient overlay for depth
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            categoryColors.glow.copy(alpha = 0.15f),
                            Color.Transparent
                        ),
                        center = Offset(0.5f, 0.3f),
                        radius = 800f
                    )
                )
        )

        // Vignette effect
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.Transparent,
                            DarkBackground.copy(alpha = 0.5f)
                        ),
                        center = Offset(0.5f, 0.5f),
                        radius = 1200f
                    )
                )
        )

        content()
    }
}

// ============================================
// CategoryBackground - Category-Specific Overlays
// ============================================

@Composable
fun CategoryBackground(
    category: String,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val categoryColors = QuizTheme.categoryColors

    // Animations
    val infiniteTransition = rememberInfiniteTransition(label = "category_transition")

    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(50000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "category_time"
    )

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "category_pulse"
    )

    val wave by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "category_wave"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        DarkBackground,
                        categoryColors.secondary.copy(alpha = 0.15f),
                        categoryColors.secondary.copy(alpha = 0.08f),
                        DarkBackground
                    )
                )
            )
    ) {
        // Category-specific elements
        Canvas(modifier = Modifier.fillMaxSize()) {
            when (category) {
                "宇宙" -> drawSpaceElements(time, pulse, categoryColors.primary, categoryColors.accent)
                "物理" -> drawPhysicsElements(time, wave, categoryColors.primary, categoryColors.accent)
                "化学" -> drawChemistryElements(time, pulse, categoryColors.primary, categoryColors.accent)
                "生物" -> drawBiologyElements(time, pulse, categoryColors.primary, categoryColors.accent)
                "地学" -> drawEarthElements(time, pulse, categoryColors.primary, categoryColors.accent)
            }
        }

        // Glow overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            categoryColors.glow.copy(alpha = 0.2f),
                            Color.Transparent
                        ),
                        center = Offset(0.5f, 0.2f),
                        radius = 1000f
                    )
                )
        )

        content()
    }
}

// ============================================
// Category-Specific Drawing Functions
// ============================================

private fun DrawScope.drawSpaceElements(
    time: Float,
    pulse: Float,
    primary: Color,
    accent: Color
) {
    // Animated stars
    repeat(100) { i ->
        val seed = i * 12345L
        val random = Random(seed)
        val x = random.nextFloat() * size.width
        val y = (random.nextFloat() + time * 0.00002f * (1 + random.nextFloat())) % 1.1f * size.height
        val twinkleAlpha = 0.3f + 0.4f * sin(pulse * (1 + random.nextFloat() * 2))

        drawCircle(
            color = Color.White.copy(alpha = twinkleAlpha),
            radius = random.nextFloat() * 2f + 0.5f,
            center = Offset(x, y)
        )
    }

    // Planet silhouettes
    val planetX = size.width * 0.85f
    val planetY = size.height * 0.15f
    val planetRadius = 60f

    // Planet glow
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                primary.copy(alpha = 0.3f),
                Color.Transparent
            ),
            center = Offset(planetX, planetY),
            radius = planetRadius * 2f
        ),
        center = Offset(planetX, planetY),
        radius = planetRadius * 2f
    )

    // Planet body
    drawCircle(
        brush = Brush.linearGradient(
            colors = listOf(
                primary.copy(alpha = 0.4f),
                accent.copy(alpha = 0.2f)
            ),
            start = Offset(planetX - planetRadius, planetY - planetRadius),
            end = Offset(planetX + planetRadius, planetY + planetRadius)
        ),
        center = Offset(planetX, planetY),
        radius = planetRadius
    )

    // Planet ring
    drawArc(
        color = accent.copy(alpha = 0.2f),
        startAngle = -30f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(planetX - planetRadius * 1.5f, planetY - 10f),
        size = Size(planetRadius * 3f, 20f),
        style = Stroke(width = 3f)
    )

    // Nebula clouds
    repeat(3) { i ->
        val nebulaX = size.width * (0.2f + i * 0.3f)
        val nebulaY = size.height * (0.3f + sin(time * 0.0001f + i) * 0.1f)
        val nebulaRadius = 150f + i * 50f

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    primary.copy(alpha = 0.05f * (1 + 0.3f * sin(pulse + i))),
                    Color.Transparent
                ),
                center = Offset(nebulaX, nebulaY),
                radius = nebulaRadius
            ),
            center = Offset(nebulaX, nebulaY),
            radius = nebulaRadius
        )
    }
}

private fun DrawScope.drawPhysicsElements(
    time: Float,
    wave: Float,
    primary: Color,
    accent: Color
) {
    // Electric arcs
    repeat(5) { arcIndex ->
        val startX = size.width * (0.1f + arcIndex * 0.2f)
        val startY = size.height * 0.3f
        val endX = startX + size.width * 0.15f
        val endY = size.height * 0.7f

        val alpha = 0.1f + 0.15f * sin(wave + arcIndex * 0.5f)

        // Draw zigzag lightning
        val path = Path()
        path.moveTo(startX, startY)

        val segments = 8
        for (i in 1..segments) {
            val progress = i.toFloat() / segments
            val baseX = startX + (endX - startX) * progress
            val baseY = startY + (endY - startY) * progress
            val offsetX = if (i < segments) sin(time * 0.01f + i) * 20f else 0f
            path.lineTo(baseX + offsetX, baseY)
        }

        drawPath(
            path = path,
            color = accent.copy(alpha = alpha),
            style = Stroke(width = 2f)
        )

        // Glow effect
        drawPath(
            path = path,
            brush = Brush.linearGradient(
                colors = listOf(
                    primary.copy(alpha = alpha * 0.5f),
                    accent.copy(alpha = alpha * 0.3f)
                )
            ),
            style = Stroke(width = 8f)
        )
    }

    // Wave patterns
    repeat(4) { waveIndex ->
        val baseY = size.height * (0.2f + waveIndex * 0.2f)
        val amplitude = 30f + waveIndex * 10f
        val frequency = 0.02f - waveIndex * 0.003f
        val phase = wave + waveIndex * PI.toFloat() / 4

        val path = Path()
        var firstPoint = true

        for (x in 0..size.width.toInt() step 5) {
            val y = baseY + amplitude * sin(x * frequency + phase)
            if (firstPoint) {
                path.moveTo(x.toFloat(), y)
                firstPoint = false
            } else {
                path.lineTo(x.toFloat(), y)
            }
        }

        drawPath(
            path = path,
            color = primary.copy(alpha = 0.1f - waveIndex * 0.02f),
            style = Stroke(width = 2f)
        )
    }

    // Energy particles
    repeat(30) { i ->
        val seed = i * 54321L
        val random = Random(seed)
        val x = (random.nextFloat() + time * 0.0001f * (random.nextFloat() - 0.5f)) % 1f * size.width
        val y = random.nextFloat() * size.height
        val particleAlpha = 0.3f + 0.3f * sin(wave * 2 + i)

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    accent.copy(alpha = particleAlpha),
                    Color.Transparent
                ),
                center = Offset(x, y),
                radius = 15f
            ),
            center = Offset(x, y),
            radius = 15f
        )

        drawCircle(
            color = accent.copy(alpha = particleAlpha),
            radius = 2f,
            center = Offset(x, y)
        )
    }
}

private fun DrawScope.drawChemistryElements(
    time: Float,
    pulse: Float,
    primary: Color,
    accent: Color
) {
    // Floating hexagons (benzene rings)
    repeat(15) { i ->
        val seed = i * 98765L
        val random = Random(seed)
        val baseX = random.nextFloat() * size.width
        val baseY = (random.nextFloat() - time * 0.00005f * (1 + random.nextFloat())) % 1.2f * size.height
        val hexSize = 30f + random.nextFloat() * 30f
        val rotation = time * 0.001f * (if (random.nextBoolean()) 1 else -1) + random.nextFloat() * 360f
        val alpha = 0.05f + 0.05f * sin(pulse + i)

        rotate(rotation, Offset(baseX, baseY)) {
            drawHexagon(
                center = Offset(baseX, baseY),
                size = hexSize,
                color = primary.copy(alpha = alpha),
                strokeWidth = 2f
            )

            // Inner circle (benzene)
            drawCircle(
                color = primary.copy(alpha = alpha * 0.5f),
                radius = hexSize * 0.5f,
                center = Offset(baseX, baseY),
                style = Stroke(width = 1f)
            )
        }
    }

    // Molecule bonds
    repeat(20) { i ->
        val seed = i * 13579L
        val random = Random(seed)
        val x1 = random.nextFloat() * size.width
        val y1 = random.nextFloat() * size.height
        val angle = random.nextFloat() * 360f
        val bondLength = 40f + random.nextFloat() * 40f
        val x2 = x1 + cos(angle * PI.toFloat() / 180f) * bondLength
        val y2 = y1 + sin(angle * PI.toFloat() / 180f) * bondLength

        val alpha = 0.08f + 0.05f * sin(pulse * 1.5f + i)

        // Bond line
        drawLine(
            color = accent.copy(alpha = alpha),
            start = Offset(x1, y1),
            end = Offset(x2, y2),
            strokeWidth = 3f
        )

        // Atoms at ends
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    accent.copy(alpha = alpha * 2),
                    Color.Transparent
                ),
                center = Offset(x1, y1),
                radius = 15f
            ),
            center = Offset(x1, y1),
            radius = 15f
        )

        drawCircle(
            color = primary.copy(alpha = alpha * 2),
            radius = 5f,
            center = Offset(x1, y1)
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    primary.copy(alpha = alpha * 2),
                    Color.Transparent
                ),
                center = Offset(x2, y2),
                radius = 12f
            ),
            center = Offset(x2, y2),
            radius = 12f
        )

        drawCircle(
            color = accent.copy(alpha = alpha * 2),
            radius = 4f,
            center = Offset(x2, y2)
        )
    }

    // Bubbling particles rising
    repeat(25) { i ->
        val seed = i * 24680L
        val random = Random(seed)
        val x = random.nextFloat() * size.width + sin(time * 0.001f + i) * 20f
        val y = (1.1f - (time * 0.0001f * (0.5f + random.nextFloat() * 0.5f) + random.nextFloat())) % 1.2f * size.height
        val bubbleSize = 5f + random.nextFloat() * 15f
        val alpha = 0.1f + 0.1f * sin(pulse * 2 + i)

        drawCircle(
            color = accent.copy(alpha = alpha),
            radius = bubbleSize,
            center = Offset(x, y),
            style = Stroke(width = 1.5f)
        )

        // Bubble highlight
        drawArc(
            color = Color.White.copy(alpha = alpha * 0.5f),
            startAngle = 200f,
            sweepAngle = 60f,
            useCenter = false,
            topLeft = Offset(x - bubbleSize * 0.7f, y - bubbleSize * 0.7f),
            size = Size(bubbleSize * 1.4f, bubbleSize * 1.4f),
            style = Stroke(width = 1f)
        )
    }
}

private fun DrawScope.drawBiologyElements(
    time: Float,
    pulse: Float,
    primary: Color,
    accent: Color
) {
    // Cell-like circles
    repeat(12) { i ->
        val seed = i * 11111L
        val random = Random(seed)
        val x = random.nextFloat() * size.width
        val y = random.nextFloat() * size.height
        val cellRadius = 40f + random.nextFloat() * 60f
        val pulseFactor = 1f + 0.1f * sin(pulse + i * 0.5f)
        val alpha = 0.06f + 0.04f * sin(pulse + i)

        // Cell membrane
        drawCircle(
            color = primary.copy(alpha = alpha),
            radius = cellRadius * pulseFactor,
            center = Offset(x, y),
            style = Stroke(width = 2f)
        )

        // Cell cytoplasm
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    primary.copy(alpha = alpha * 0.5f),
                    Color.Transparent
                ),
                center = Offset(x, y),
                radius = cellRadius * pulseFactor
            ),
            center = Offset(x, y),
            radius = cellRadius * pulseFactor
        )

        // Nucleus
        val nucleusRadius = cellRadius * 0.3f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    accent.copy(alpha = alpha * 2),
                    primary.copy(alpha = alpha)
                ),
                center = Offset(x, y),
                radius = nucleusRadius
            ),
            center = Offset(x, y),
            radius = nucleusRadius
        )
    }

    // DNA helix hints
    val helixCenterX = size.width * 0.85f
    val helixStartY = size.height * 0.1f
    val helixEndY = size.height * 0.9f
    val helixWidth = 40f
    val helixStep = 30f

    var y = helixStartY
    while (y < helixEndY) {
        val phase = y * 0.05f + time * 0.002f
        val x1 = helixCenterX + sin(phase) * helixWidth
        val x2 = helixCenterX - sin(phase) * helixWidth
        val alpha = 0.1f + 0.05f * sin(pulse + y * 0.01f)

        // Helix strands
        drawCircle(
            color = primary.copy(alpha = alpha),
            radius = 4f,
            center = Offset(x1, y)
        )
        drawCircle(
            color = accent.copy(alpha = alpha),
            radius = 4f,
            center = Offset(x2, y)
        )

        // Base pair connection (every few steps)
        if ((y.toInt() / helixStep.toInt()) % 2 == 0) {
            drawLine(
                color = primary.copy(alpha = alpha * 0.5f),
                start = Offset(x1, y),
                end = Offset(x2, y),
                strokeWidth = 1f
            )
        }

        y += helixStep / 3
    }

    // Floating organelles
    repeat(20) { i ->
        val seed = i * 33333L
        val random = Random(seed)
        val x = random.nextFloat() * size.width * 0.7f
        val y = (random.nextFloat() + time * 0.00003f * (random.nextFloat() - 0.5f)) % 1f * size.height
        val orgSize = 8f + random.nextFloat() * 12f
        val alpha = 0.15f + 0.1f * sin(pulse * 1.5f + i)

        // Mitochondria-like shapes
        drawOval(
            color = accent.copy(alpha = alpha),
            topLeft = Offset(x - orgSize, y - orgSize * 0.5f),
            size = Size(orgSize * 2, orgSize),
            style = Stroke(width = 1.5f)
        )

        // Inner membrane folds
        repeat(3) { fold ->
            val foldX = x - orgSize * 0.6f + fold * orgSize * 0.4f
            drawLine(
                color = accent.copy(alpha = alpha * 0.5f),
                start = Offset(foldX, y - orgSize * 0.3f),
                end = Offset(foldX, y + orgSize * 0.3f),
                strokeWidth = 1f
            )
        }
    }
}

private fun DrawScope.drawEarthElements(
    time: Float,
    pulse: Float,
    primary: Color,
    accent: Color
) {
    // Geological layers
    val layerColors = listOf(
        primary.copy(alpha = 0.15f),
        accent.copy(alpha = 0.12f),
        primary.copy(alpha = 0.1f),
        accent.copy(alpha = 0.08f),
        primary.copy(alpha = 0.06f)
    )

    layerColors.forEachIndexed { index, color ->
        val baseY = size.height * (0.5f + index * 0.1f)
        val path = Path()
        var firstPoint = true

        for (x in 0..size.width.toInt() step 10) {
            val waveOffset = sin(x * 0.01f + time * 0.0005f + index) * 15f
            val y = baseY + waveOffset

            if (firstPoint) {
                path.moveTo(0f, y)
                firstPoint = false
            } else {
                path.lineTo(x.toFloat(), y)
            }
        }

        path.lineTo(size.width, size.height)
        path.lineTo(0f, size.height)
        path.close()

        drawPath(
            path = path,
            color = color
        )
    }

    // Crystal formations
    repeat(10) { i ->
        val seed = i * 44444L
        val random = Random(seed)
        val x = random.nextFloat() * size.width
        val y = size.height * (0.3f + random.nextFloat() * 0.4f)
        val crystalSize = 20f + random.nextFloat() * 40f
        val rotation = random.nextFloat() * 60f - 30f
        val alpha = 0.08f + 0.06f * sin(pulse + i * 0.3f)
        val shineAlpha = 0.15f + 0.1f * sin(pulse * 2 + i)

        rotate(rotation, Offset(x, y)) {
            // Crystal body
            val crystalPath = Path().apply {
                moveTo(x, y - crystalSize)
                lineTo(x + crystalSize * 0.3f, y)
                lineTo(x + crystalSize * 0.2f, y + crystalSize * 0.5f)
                lineTo(x - crystalSize * 0.2f, y + crystalSize * 0.5f)
                lineTo(x - crystalSize * 0.3f, y)
                close()
            }

            drawPath(
                path = crystalPath,
                brush = Brush.linearGradient(
                    colors = listOf(
                        accent.copy(alpha = alpha),
                        primary.copy(alpha = alpha * 0.5f)
                    ),
                    start = Offset(x - crystalSize * 0.3f, y - crystalSize),
                    end = Offset(x + crystalSize * 0.3f, y + crystalSize * 0.5f)
                )
            )

            drawPath(
                path = crystalPath,
                color = accent.copy(alpha = alpha),
                style = Stroke(width = 1.5f)
            )

            // Crystal shine
            drawLine(
                color = Color.White.copy(alpha = shineAlpha),
                start = Offset(x - crystalSize * 0.1f, y - crystalSize * 0.8f),
                end = Offset(x - crystalSize * 0.15f, y - crystalSize * 0.3f),
                strokeWidth = 2f
            )
        }
    }

    // Floating rock particles
    repeat(30) { i ->
        val seed = i * 55555L
        val random = Random(seed)
        val x = random.nextFloat() * size.width
        val y = (random.nextFloat() + time * 0.00002f) % 1.1f * size.height
        val rockSize = 3f + random.nextFloat() * 8f
        val alpha = 0.2f + 0.15f * sin(pulse + i * 0.2f)

        // Irregular rock shape
        val rockPath = Path().apply {
            moveTo(x, y - rockSize)
            lineTo(x + rockSize * 0.8f, y - rockSize * 0.3f)
            lineTo(x + rockSize, y + rockSize * 0.2f)
            lineTo(x + rockSize * 0.5f, y + rockSize)
            lineTo(x - rockSize * 0.3f, y + rockSize * 0.8f)
            lineTo(x - rockSize * 0.8f, y + rockSize * 0.1f)
            lineTo(x - rockSize * 0.5f, y - rockSize * 0.5f)
            close()
        }

        drawPath(
            path = rockPath,
            color = primary.copy(alpha = alpha)
        )
    }

    // Seismic wave lines
    repeat(3) { i ->
        val waveY = size.height * (0.2f + i * 0.15f)
        val wavePhase = time * 0.003f + i * PI.toFloat() / 3
        val alpha = 0.08f + 0.05f * sin(pulse + i)

        val path = Path()
        var firstPoint = true

        for (x in 0..size.width.toInt() step 3) {
            val amplitude = 10f * sin(x * 0.02f - wavePhase)
            val y = waveY + amplitude * (1f - (x / size.width) * 0.5f)

            if (firstPoint) {
                path.moveTo(x.toFloat(), y)
                firstPoint = false
            } else {
                path.lineTo(x.toFloat(), y)
            }
        }

        drawPath(
            path = path,
            color = accent.copy(alpha = alpha),
            style = Stroke(width = 1.5f)
        )
    }
}

// ============================================
// Helper Drawing Functions
// ============================================

private fun DrawScope.drawHexagon(
    center: Offset,
    size: Float,
    color: Color,
    strokeWidth: Float
) {
    val path = Path()
    for (i in 0..5) {
        val angle = (60 * i - 30) * PI.toFloat() / 180f
        val x = center.x + size * cos(angle)
        val y = center.y + size * sin(angle)
        if (i == 0) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
    }
    path.close()

    drawPath(
        path = path,
        color = color,
        style = Stroke(width = strokeWidth)
    )
}

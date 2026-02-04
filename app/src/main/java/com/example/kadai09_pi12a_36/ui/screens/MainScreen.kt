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
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kadai09_pi12a_36.ui.components.SpaceBackground
import com.example.kadai09_pi12a_36.ui.theme.CategoryColors
import com.example.kadai09_pi12a_36.ui.theme.DarkSurfaceVariant
import com.example.kadai09_pi12a_36.ui.theme.NeonCyan
import com.example.kadai09_pi12a_36.ui.theme.NeonPurple
import com.example.kadai09_pi12a_36.ui.theme.TextSecondary
import kotlinx.coroutines.delay

data class CategoryInfo(
    val name: String,
    val initial: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val accentColor: Color
)

private val categories = listOf(
    CategoryInfo("宇宙", "U", CategoryColors.SpacePrimary, CategoryColors.SpaceSecondary, CategoryColors.SpaceAccent),
    CategoryInfo("物理", "P", CategoryColors.PhysicsPrimary, CategoryColors.PhysicsSecondary, CategoryColors.PhysicsAccent),
    CategoryInfo("化学", "C", CategoryColors.ChemistryPrimary, CategoryColors.ChemistrySecondary, CategoryColors.ChemistryAccent),
    CategoryInfo("生物", "B", CategoryColors.BiologyPrimary, CategoryColors.BiologySecondary, CategoryColors.BiologyAccent),
    CategoryInfo("地学", "E", CategoryColors.EarthPrimary, CategoryColors.EarthSecondary, CategoryColors.EarthAccent)
)

@Composable
fun MainScreen(
    totalQuestions: Int,
    onCategorySelected: (String) -> Unit,
    onAchievementClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "main")

    // Title glow animation
    val titleGlow by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "titleGlow"
    )

    // Holographic shimmer
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    // Achievement button pulse
    val achievementPulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "achievementPulse"
    )

    // Staggered card entrance
    var showCards by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(300)
        showCards = true
    }

    SpaceBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Achievement button (top right)
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopEnd
            ) {
                // Glow behind button
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .scale(achievementPulse)
                        .blur(16.dp)
                        .background(
                            color = NeonPurple.copy(alpha = 0.5f),
                            shape = CircleShape
                        )
                )

                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    NeonPurple,
                                    NeonPurple.copy(alpha = 0.7f)
                                )
                            )
                        )
                        .border(
                            width = 2.dp,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.5f),
                                    Color.White.copy(alpha = 0.1f)
                                )
                            ),
                            shape = CircleShape
                        )
                        .clickable(onClick = onAchievementClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "実績",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Hero title with holographic effect
            Box(contentAlignment = Alignment.Center) {
                // Glow layer
                Text(
                    text = "Science Quiz",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 42.sp,
                        letterSpacing = 4.sp
                    ),
                    fontWeight = FontWeight.Black,
                    color = NeonCyan.copy(alpha = titleGlow * 0.3f),
                    modifier = Modifier
                        .blur(20.dp)
                        .graphicsLayer { alpha = titleGlow }
                )

                // Main text with shimmer
                Text(
                    text = "Science Quiz",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 42.sp,
                        letterSpacing = 4.sp
                    ),
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.drawWithContent {
                        drawContent()
                        val shimmerWidth = size.width * 0.3f
                        val shimmerStart = shimmerOffset * (size.width + shimmerWidth) - shimmerWidth
                        drawRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.White.copy(alpha = 0.4f),
                                    NeonCyan.copy(alpha = 0.6f),
                                    Color.White.copy(alpha = 0.4f),
                                    Color.Transparent
                                ),
                                startX = shimmerStart,
                                endX = shimmerStart + shimmerWidth
                            )
                        )
                    },
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = "理科の知識を試そう",
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Question count badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                NeonPurple.copy(alpha = 0.3f),
                                NeonCyan.copy(alpha = 0.3f)
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                NeonPurple.copy(alpha = 0.5f),
                                NeonCyan.copy(alpha = 0.5f)
                            )
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "全${totalQuestions}問収録",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Category selection title
            Text(
                text = "カテゴリを選択",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Category cards grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                itemsIndexed(categories) { index, category ->
                    AnimatedVisibility(
                        visible = showCards,
                        enter = fadeIn(
                            animationSpec = tween(
                                durationMillis = 500,
                                delayMillis = index * 100
                            )
                        ) + scaleIn(
                            initialScale = 0.8f,
                            animationSpec = tween(
                                durationMillis = 500,
                                delayMillis = index * 100
                            )
                        )
                    ) {
                        CategoryCard(
                            category = category,
                            onClick = { onCategorySelected(category.name) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryCard(
    category: CategoryInfo,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "scale"
    )

    val rotation by animateFloatAsState(
        targetValue = if (isPressed) 2f else 0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "rotation"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "card")
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )

    Box(
        modifier = Modifier
            .aspectRatio(0.9f)
            .scale(scale)
            .graphicsLayer { rotationZ = rotation }
    ) {
        // Outer glow
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(4.dp)
                .blur(20.dp)
                .background(
                    color = category.primaryColor.copy(alpha = glowPulse),
                    shape = RoundedCornerShape(28.dp)
                )
        )

        // Card
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(28.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            category.primaryColor.copy(alpha = 0.2f),
                            category.secondaryColor.copy(alpha = 0.3f),
                            DarkSurfaceVariant.copy(alpha = 0.8f)
                        )
                    )
                )
                .border(
                    width = 2.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            category.primaryColor.copy(alpha = 0.8f),
                            category.accentColor.copy(alpha = 0.4f),
                            category.primaryColor.copy(alpha = 0.2f)
                        )
                    ),
                    shape = RoundedCornerShape(28.dp)
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            // Inner gradient overlay
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                category.primaryColor.copy(alpha = 0.15f),
                                Color.Transparent
                            ),
                            center = Offset(0.5f, 0.3f),
                            radius = 400f
                        )
                    )
            )

            // Top shine
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
                            endY = 150f
                        )
                    )
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                // Initial badge
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    category.primaryColor,
                                    category.primaryColor.copy(alpha = 0.6f)
                                )
                            )
                        )
                        .border(
                            width = 2.dp,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.5f),
                                    Color.White.copy(alpha = 0.1f)
                                )
                            ),
                            shape = CircleShape
                        )
                        .padding(bottom = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = category.initial,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Category name
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Decorative line
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    category.primaryColor,
                                    Color.Transparent
                                )
                            )
                        )
                )
            }
        }
    }
}

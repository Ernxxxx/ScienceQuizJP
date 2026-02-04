package com.example.kadai09_pi12a_36.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kadai09_pi12a_36.ui.components.CategoryBackground
import com.example.kadai09_pi12a_36.ui.components.GlassmorphicCard
import com.example.kadai09_pi12a_36.ui.theme.QuizTheme
import com.example.kadai09_pi12a_36.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun LevelSelectScreen(
    category: String,
    levelQuestionCounts: List<Int>,
    onLevelSelected: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    val categoryColors = QuizTheme.categoryColors

    // Track visibility state for each level card for staggered animation
    val visibleItems = remember { mutableStateListOf<Boolean>() }

    LaunchedEffect(Unit) {
        visibleItems.clear()
        repeat(5) { visibleItems.add(false) }
        // Staggered animation - show each card with delay
        for (i in 0 until 5) {
            delay(100L)
            visibleItems[i] = true
        }
    }

    CategoryBackground(category = category) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Top bar with back button and category title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Text(
                    text = category,
                    style = MaterialTheme.typography.headlineLarge,
                    color = categoryColors.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Level selection title
            Text(
                text = "Level Select",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Level cards
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(
                    items = (1..5).toList(),
                    key = { _, level -> level }
                ) { index, level ->
                    val questionCount = levelQuestionCounts.getOrElse(index) { 0 }
                    val isVisible = visibleItems.getOrElse(index) { false }

                    AnimatedVisibility(
                        visible = isVisible,
                        enter = slideInHorizontally(
                            initialOffsetX = { fullWidth -> fullWidth },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        ) + fadeIn(
                            animationSpec = tween(durationMillis = 300)
                        )
                    ) {
                        LevelCard(
                            level = level,
                            questionCount = questionCount,
                            categoryColors = categoryColors,
                            onClick = { onLevelSelected(level) }
                        )
                    }
                }

                // Bottom spacing
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun LevelCard(
    level: Int,
    questionCount: Int,
    categoryColors: com.example.kadai09_pi12a_36.ui.theme.CategoryColorScheme,
    onClick: () -> Unit
) {
    GlassmorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        cornerRadius = 20.dp,
        glowColor = categoryColors.glow
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Lv.$level",
                    style = MaterialTheme.typography.headlineMedium,
                    color = categoryColors.primary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$questionCount questions",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }

            // Level indicator circle
            Box(
                modifier = Modifier
                    .size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getLevelEmoji(level),
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
    }
}

private fun getLevelEmoji(level: Int): String {
    return when (level) {
        1 -> "☆"
        2 -> "★"
        3 -> "★☆"
        4 -> "★★"
        5 -> "★★★"
        else -> "☆"
    }
}

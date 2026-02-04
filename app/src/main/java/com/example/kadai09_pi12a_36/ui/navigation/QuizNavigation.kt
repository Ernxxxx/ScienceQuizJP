package com.example.kadai09_pi12a_36.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.kadai09_pi12a_36.Achievement
import com.example.kadai09_pi12a_36.AchievementManager
import com.example.kadai09_pi12a_36.QuizQuestion
import com.example.kadai09_pi12a_36.QuizRepository
import com.example.kadai09_pi12a_36.QuizSession
import com.example.kadai09_pi12a_36.ui.screens.AchievementScreen
import com.example.kadai09_pi12a_36.ui.screens.LevelSelectScreen
import com.example.kadai09_pi12a_36.ui.screens.MainScreen
import com.example.kadai09_pi12a_36.ui.screens.QuizScreen
import com.example.kadai09_pi12a_36.ui.screens.ResultScreen
import com.example.kadai09_pi12a_36.ui.screens.SetResultScreen
import com.example.kadai09_pi12a_36.ui.theme.QuizAppTheme
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object LevelSelect : Screen("level_select/{category}") {
        fun createRoute(category: String): String {
            val encoded = URLEncoder.encode(category, StandardCharsets.UTF_8.toString())
            return "level_select/$encoded"
        }
    }
    object Quiz : Screen("quiz/{category}/{level}") {
        fun createRoute(category: String, level: Int): String {
            val encoded = URLEncoder.encode(category, StandardCharsets.UTF_8.toString())
            return "quiz/$encoded/$level"
        }
    }
    object Result : Screen("result/{category}/{isCorrect}/{correctAnswer}/{explanation}") {
        fun createRoute(category: String, isCorrect: Boolean, correctAnswer: String, explanation: String): String {
            val encodedCategory = URLEncoder.encode(category, StandardCharsets.UTF_8.toString())
            val encodedAnswer = URLEncoder.encode(correctAnswer, StandardCharsets.UTF_8.toString())
            val encodedExplanation = URLEncoder.encode(explanation, StandardCharsets.UTF_8.toString())
            return "result/$encodedCategory/$isCorrect/$encodedAnswer/$encodedExplanation"
        }
    }
    object SetResult : Screen("set_result/{setCorrect}/{totalCorrect}/{totalAnswered}/{hasMore}") {
        fun createRoute(setCorrect: Int, totalCorrect: Int, totalAnswered: Int, hasMore: Boolean): String {
            return "set_result/$setCorrect/$totalCorrect/$totalAnswered/$hasMore"
        }
    }
    object Achievement : Screen("achievement")
}

@Composable
fun QuizNavHost(
    repository: QuizRepository,
    achievementManager: AchievementManager,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    // Session state
    var currentSession by remember { mutableStateOf<QuizSession?>(null) }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var hintUsed by remember { mutableStateOf(false) }
    var hintDisabledIndices by remember { mutableStateOf<Set<Int>>(emptySet()) }

    NavHost(
        navController = navController,
        startDestination = Screen.Main.route,
        modifier = modifier
    ) {
        // Main Screen
        composable(Screen.Main.route) {
            QuizAppTheme(category = "宇宙") {
                MainScreen(
                    totalQuestions = repository.getTotalQuestionCount(),
                    onCategorySelected = { category ->
                        navController.navigate(Screen.LevelSelect.createRoute(category))
                    },
                    onAchievementClick = {
                        navController.navigate(Screen.Achievement.route)
                    }
                )
            }
        }

        // Level Select Screen
        composable(
            route = Screen.LevelSelect.route,
            arguments = listOf(navArgument("category") { type = NavType.StringType })
        ) { backStackEntry ->
            val category = URLDecoder.decode(
                backStackEntry.arguments?.getString("category") ?: "",
                StandardCharsets.UTF_8.toString()
            )

            QuizAppTheme(category = category) {
                LevelSelectScreen(
                    category = category,
                    levelQuestionCounts = (1..5).map { level ->
                        repository.getQuestionCountByCategoryAndLevel(category, level)
                    },
                    onLevelSelected = { level ->
                        // Initialize session
                        val questions = repository.getQuestionsByCategoryAndLevel(category, level)
                        currentSession = QuizSession(
                            selectedLevel = level,
                            selectedCategory = category,
                            questions = questions
                        )
                        selectedIndex = null
                        hintUsed = false
                        hintDisabledIndices = emptySet()
                        navController.navigate(Screen.Quiz.createRoute(category, level))
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }
        }

        // Quiz Screen
        composable(
            route = Screen.Quiz.route,
            arguments = listOf(
                navArgument("category") { type = NavType.StringType },
                navArgument("level") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val category = URLDecoder.decode(
                backStackEntry.arguments?.getString("category") ?: "",
                StandardCharsets.UTF_8.toString()
            )
            val level = backStackEntry.arguments?.getInt("level") ?: 1

            val session = currentSession
            val question = session?.getCurrentQuestion()

            if (session != null && question != null) {
                QuizAppTheme(category = category) {
                    QuizScreen(
                        category = category,
                        level = level,
                        questionNumber = session.setAnswered + 1,
                        totalInSet = QuizSession.QUESTIONS_PER_SET,
                        question = question.question,
                        choices = question.choices,
                        selectedIndex = selectedIndex,
                        correctCount = session.totalCorrect,
                        totalAnswered = session.totalAnswered,
                        hintUsed = hintUsed,
                        hintDisabledIndices = hintDisabledIndices,
                        onChoiceSelected = { index -> selectedIndex = index },
                        onAnswerClick = {
                            selectedIndex?.let { selected ->
                                val isCorrect = selected == question.answerIndex
                                session.recordAnswer(isCorrect)

                                // Track achievements
                                achievementManager.onQuestionAnswered(
                                    isCorrect = isCorrect,
                                    category = question.category,
                                    usedHint = hintUsed
                                )

                                session.moveToNextQuestion()

                                // Navigate to result
                                navController.navigate(
                                    Screen.Result.createRoute(
                                        category = category,
                                        isCorrect = isCorrect,
                                        correctAnswer = question.choices[question.answerIndex],
                                        explanation = question.explanation
                                    )
                                )
                            }
                        },
                        onHintClick = {
                            if (!hintUsed) {
                                val correctIndex = question.answerIndex
                                val wrongIndices = (0..3).filter { it != correctIndex }.shuffled().take(2)
                                hintDisabledIndices = wrongIndices.toSet()
                                hintUsed = true
                            }
                        },
                        onBackClick = {
                            currentSession = null
                            navController.popBackStack(Screen.Main.route, inclusive = false)
                        }
                    )
                }
            }
        }

        // Result Screen
        composable(
            route = Screen.Result.route,
            arguments = listOf(
                navArgument("category") { type = NavType.StringType },
                navArgument("isCorrect") { type = NavType.BoolType },
                navArgument("correctAnswer") { type = NavType.StringType },
                navArgument("explanation") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val category = URLDecoder.decode(
                backStackEntry.arguments?.getString("category") ?: "",
                StandardCharsets.UTF_8.toString()
            )
            val isCorrect = backStackEntry.arguments?.getBoolean("isCorrect") ?: false
            val correctAnswer = URLDecoder.decode(
                backStackEntry.arguments?.getString("correctAnswer") ?: "",
                StandardCharsets.UTF_8.toString()
            )
            val explanation = URLDecoder.decode(
                backStackEntry.arguments?.getString("explanation") ?: "",
                StandardCharsets.UTF_8.toString()
            )

            val session = currentSession

            QuizAppTheme(category = category) {
                ResultScreen(
                    category = category,
                    isCorrect = isCorrect,
                    correctAnswer = correctAnswer,
                    explanation = explanation,
                    onNextClick = {
                        // Reset for next question
                        selectedIndex = null
                        hintUsed = false
                        hintDisabledIndices = emptySet()

                        if (session != null) {
                            when {
                                session.isSetComplete() -> {
                                    // Check for perfect set achievement
                                    if (session.setCorrect == QuizSession.QUESTIONS_PER_SET) {
                                        achievementManager.onPerfectSet()
                                    }
                                    navController.navigate(
                                        Screen.SetResult.createRoute(
                                            setCorrect = session.setCorrect,
                                            totalCorrect = session.totalCorrect,
                                            totalAnswered = session.totalAnswered,
                                            hasMore = session.hasMoreQuestions()
                                        )
                                    ) {
                                        popUpTo(Screen.Quiz.route) { inclusive = true }
                                    }
                                }
                                session.hasMoreQuestions() -> {
                                    navController.popBackStack()
                                }
                                else -> {
                                    navController.navigate(
                                        Screen.SetResult.createRoute(
                                            setCorrect = session.setCorrect,
                                            totalCorrect = session.totalCorrect,
                                            totalAnswered = session.totalAnswered,
                                            hasMore = false
                                        )
                                    ) {
                                        popUpTo(Screen.Quiz.route) { inclusive = true }
                                    }
                                }
                            }
                        }
                    }
                )
            }
        }

        // Set Result Screen
        composable(
            route = Screen.SetResult.route,
            arguments = listOf(
                navArgument("setCorrect") { type = NavType.IntType },
                navArgument("totalCorrect") { type = NavType.IntType },
                navArgument("totalAnswered") { type = NavType.IntType },
                navArgument("hasMore") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            val setCorrect = backStackEntry.arguments?.getInt("setCorrect") ?: 0
            val totalCorrect = backStackEntry.arguments?.getInt("totalCorrect") ?: 0
            val totalAnswered = backStackEntry.arguments?.getInt("totalAnswered") ?: 0
            val hasMore = backStackEntry.arguments?.getBoolean("hasMore") ?: false

            QuizAppTheme(category = "宇宙") {
                SetResultScreen(
                    setCorrect = setCorrect,
                    totalCorrect = totalCorrect,
                    totalAnswered = totalAnswered,
                    hasMore = hasMore,
                    onContinueClick = {
                        currentSession?.resetSetProgress()
                        selectedIndex = null
                        hintUsed = false
                        hintDisabledIndices = emptySet()

                        val session = currentSession
                        if (session != null) {
                            navController.navigate(
                                Screen.Quiz.createRoute(session.selectedCategory, session.selectedLevel)
                            ) {
                                popUpTo(Screen.SetResult.route) { inclusive = true }
                            }
                        }
                    },
                    onHomeClick = {
                        currentSession = null
                        navController.popBackStack(Screen.Main.route, inclusive = false)
                    }
                )
            }
        }

        // Achievement Screen
        composable(Screen.Achievement.route) {
            QuizAppTheme(category = "宇宙") {
                AchievementScreen(
                    achievements = achievementManager.getAllAchievements(),
                    unlockedCount = achievementManager.getUnlockedCount(),
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}

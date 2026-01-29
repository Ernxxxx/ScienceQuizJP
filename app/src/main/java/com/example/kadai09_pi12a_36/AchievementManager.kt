package com.example.kadai09_pi12a_36

import android.content.Context
import android.content.SharedPreferences

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    var isUnlocked: Boolean = false
)

class AchievementManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("achievements", Context.MODE_PRIVATE)

    companion object {
        val ALL_ACHIEVEMENTS = listOf(
            Achievement("first_correct", "初めての正解", "初めて問題に正解した", "🌟"),
            Achievement("streak_3", "3連続正解", "3問連続で正解した", "🔥"),
            Achievement("streak_5", "5連続正解", "5問連続で正解した", "💫"),
            Achievement("streak_10", "10連続正解", "10問連続で正解した", "🚀"),
            Achievement("perfect_set", "パーフェクト", "1セット全問正解した", "👑"),
            Achievement("no_hint", "自力で解決", "ヒントを使わずに10問正解", "🧠"),
            Achievement("space_master", "宇宙マスター", "宇宙カテゴリで50問正解", "🪐"),
            Achievement("physics_master", "物理マスター", "物理カテゴリで50問正解", "⚛️"),
            Achievement("chemistry_master", "化学マスター", "化学カテゴリで50問正解", "🧪"),
            Achievement("biology_master", "生物マスター", "生物カテゴリで50問正解", "🧬"),
            Achievement("earth_master", "地学マスター", "地学カテゴリで50問正解", "🌍"),
            Achievement("quiz_100", "クイズ100問", "累計100問に回答した", "📚"),
            Achievement("quiz_500", "クイズ500問", "累計500問に回答した", "🏆")
        )
    }

    // Stats tracking
    private var streakCount: Int
        get() = prefs.getInt("current_streak", 0)
        set(value) = prefs.edit().putInt("current_streak", value).apply()

    private var answeredCount: Int
        get() = prefs.getInt("total_answered", 0)
        set(value) = prefs.edit().putInt("total_answered", value).apply()

    private var noHintCorrectCount: Int
        get() = prefs.getInt("no_hint_correct", 0)
        set(value) = prefs.edit().putInt("no_hint_correct", value).apply()

    fun getCategoryCorrect(category: String): Int {
        return prefs.getInt("correct_$category", 0)
    }

    private fun setCategoryCorrect(category: String, value: Int) {
        prefs.edit().putInt("correct_$category", value).apply()
    }

    fun isAchievementUnlocked(id: String): Boolean {
        return prefs.getBoolean("achievement_$id", false)
    }

    private fun unlockAchievement(id: String) {
        prefs.edit().putBoolean("achievement_$id", true).apply()
    }

    fun getUnlockedAchievements(): List<Achievement> {
        return ALL_ACHIEVEMENTS.map { achievement ->
            achievement.copy(isUnlocked = isAchievementUnlocked(achievement.id))
        }.filter { it.isUnlocked }
    }

    fun getAllAchievements(): List<Achievement> {
        return ALL_ACHIEVEMENTS.map { achievement ->
            achievement.copy(isUnlocked = isAchievementUnlocked(achievement.id))
        }
    }

    fun getUnlockedCount(): Int {
        return ALL_ACHIEVEMENTS.count { isAchievementUnlocked(it.id) }
    }

    /**
     * Call this when user answers a question
     * Returns list of newly unlocked achievements
     */
    fun onQuestionAnswered(
        isCorrect: Boolean,
        category: String,
        usedHint: Boolean
    ): List<Achievement> {
        val newlyUnlocked = mutableListOf<Achievement>()

        answeredCount++

        if (isCorrect) {
            streakCount++

            // Category tracking
            val categoryCount = getCategoryCorrect(category) + 1
            setCategoryCorrect(category, categoryCount)

            // No hint tracking
            if (!usedHint) {
                noHintCorrectCount++
            } else {
                noHintCorrectCount = 0
            }

            // Check achievements
            if (!isAchievementUnlocked("first_correct")) {
                unlockAchievement("first_correct")
                newlyUnlocked.add(ALL_ACHIEVEMENTS.first { it.id == "first_correct" })
            }

            if (streakCount >= 3 && !isAchievementUnlocked("streak_3")) {
                unlockAchievement("streak_3")
                newlyUnlocked.add(ALL_ACHIEVEMENTS.first { it.id == "streak_3" })
            }

            if (streakCount >= 5 && !isAchievementUnlocked("streak_5")) {
                unlockAchievement("streak_5")
                newlyUnlocked.add(ALL_ACHIEVEMENTS.first { it.id == "streak_5" })
            }

            if (streakCount >= 10 && !isAchievementUnlocked("streak_10")) {
                unlockAchievement("streak_10")
                newlyUnlocked.add(ALL_ACHIEVEMENTS.first { it.id == "streak_10" })
            }

            if (noHintCorrectCount >= 10 && !isAchievementUnlocked("no_hint")) {
                unlockAchievement("no_hint")
                newlyUnlocked.add(ALL_ACHIEVEMENTS.first { it.id == "no_hint" })
            }

            // Category masters
            val categoryAchievementMap = mapOf(
                "宇宙" to "space_master",
                "物理" to "physics_master",
                "化学" to "chemistry_master",
                "生物" to "biology_master",
                "地学" to "earth_master"
            )
            categoryAchievementMap[category]?.let { achievementId ->
                if (categoryCount >= 50 && !isAchievementUnlocked(achievementId)) {
                    unlockAchievement(achievementId)
                    newlyUnlocked.add(ALL_ACHIEVEMENTS.first { it.id == achievementId })
                }
            }

        } else {
            streakCount = 0
            noHintCorrectCount = 0
        }

        // Total questions achievements
        if (answeredCount >= 100 && !isAchievementUnlocked("quiz_100")) {
            unlockAchievement("quiz_100")
            newlyUnlocked.add(ALL_ACHIEVEMENTS.first { it.id == "quiz_100" })
        }

        if (answeredCount >= 500 && !isAchievementUnlocked("quiz_500")) {
            unlockAchievement("quiz_500")
            newlyUnlocked.add(ALL_ACHIEVEMENTS.first { it.id == "quiz_500" })
        }

        return newlyUnlocked
    }

    /**
     * Call this when user completes a perfect set
     */
    fun onPerfectSet(): Achievement? {
        if (!isAchievementUnlocked("perfect_set")) {
            unlockAchievement("perfect_set")
            return ALL_ACHIEVEMENTS.first { it.id == "perfect_set" }
        }
        return null
    }

    fun getCurrentStreak(): Int = streakCount
    fun getTotalAnswered(): Int = answeredCount
}

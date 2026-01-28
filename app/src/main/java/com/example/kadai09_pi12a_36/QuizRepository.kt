package com.example.kadai09_pi12a_36

import android.content.Context
import com.google.gson.Gson
import java.io.IOException

/**
 * クイズデータの読み込みと管理を行うリポジトリ
 */
class QuizRepository(private val context: Context) {

    private var allQuestions: List<QuizQuestion> = emptyList()

    init {
        loadQuestions()
    }

    private fun loadQuestions() {
        try {
            val jsonString = context.assets.open("questions.json")
                .bufferedReader()
                .use { it.readText() }

            val gson = Gson()
            val wrapper = gson.fromJson(jsonString, QuizDataWrapper::class.java)
            allQuestions = wrapper.questions
        } catch (e: IOException) {
            e.printStackTrace()
            allQuestions = emptyList()
        }
    }

    fun getTotalQuestionCount(): Int {
        return allQuestions.size
    }

    fun getQuestionCountByLevel(level: Int): Int {
        return allQuestions.count { it.level == level }
    }

    fun getQuestionCountByCategory(category: String): Int {
        return allQuestions.count { it.category == category }
    }

    fun getQuestionCountByCategoryAndLevel(category: String, level: Int): Int {
        return allQuestions.count { it.category == category && it.level == level }
    }

    fun getQuestionsByLevel(level: Int): List<QuizQuestion> {
        return allQuestions
            .filter { it.level == level }
            .shuffled()
    }

    fun getQuestionsByCategory(category: String): List<QuizQuestion> {
        return allQuestions
            .filter { it.category == category }
            .shuffled()
    }

    fun getQuestionsByCategoryAndLevel(category: String, level: Int): List<QuizQuestion> {
        return allQuestions
            .filter { it.category == category && it.level == level }
            .shuffled()
    }

    fun getAllCategories(): List<String> {
        return allQuestions.map { it.category }.distinct()
    }
}

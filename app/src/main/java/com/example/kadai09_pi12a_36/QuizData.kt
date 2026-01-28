package com.example.kadai09_pi12a_36

import com.google.gson.annotations.SerializedName

data class QuizQuestion(
    @SerializedName("id")
    val id: Int,

    @SerializedName("question")
    val question: String,

    @SerializedName("choices")
    val choices: List<String>,

    @SerializedName("answerIndex")
    val answerIndex: Int,

    @SerializedName("level")
    val level: Int,

    @SerializedName("category")
    val category: String,

    @SerializedName("explanation")
    val explanation: String
)

data class QuizDataWrapper(
    @SerializedName("questions")
    val questions: List<QuizQuestion>
)

data class QuizSession(
    var currentQuestionIndex: Int = 0,
    var totalAnswered: Int = 0,
    var totalCorrect: Int = 0,
    var setCorrect: Int = 0,
    var setAnswered: Int = 0,
    var selectedLevel: Int = 1,
    var selectedCategory: String = "",
    var questions: List<QuizQuestion> = emptyList()
) {
    companion object {
        const val QUESTIONS_PER_SET = 10
    }

    fun getCurrentQuestion(): QuizQuestion? {
        return if (currentQuestionIndex < questions.size) {
            questions[currentQuestionIndex]
        } else null
    }

    fun hasMoreQuestions(): Boolean {
        return currentQuestionIndex < questions.size
    }

    fun isSetComplete(): Boolean {
        return setAnswered >= QUESTIONS_PER_SET
    }

    fun moveToNextQuestion() {
        currentQuestionIndex++
    }

    fun recordAnswer(isCorrect: Boolean) {
        totalAnswered++
        setAnswered++
        if (isCorrect) {
            totalCorrect++
            setCorrect++
        }
    }

    fun resetSetProgress() {
        setCorrect = 0
        setAnswered = 0
    }

    fun resetAll() {
        currentQuestionIndex = 0
        totalAnswered = 0
        totalCorrect = 0
        setCorrect = 0
        setAnswered = 0
    }
}

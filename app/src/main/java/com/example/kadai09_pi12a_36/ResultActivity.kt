package com.example.kadai09_pi12a_36

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton

class ResultActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_IS_CORRECT = "extra_is_correct"
        const val EXTRA_CORRECT_ANSWER = "extra_correct_answer"
        const val EXTRA_EXPLANATION = "extra_explanation"
        const val EXTRA_IS_SET_COMPLETE = "extra_is_set_complete"
    }

    private lateinit var cardResult: CardView
    private lateinit var tvResultIcon: TextView
    private lateinit var tvResult: TextView
    private lateinit var tvCorrectAnswer: TextView
    private lateinit var tvExplanation: TextView
    private lateinit var btnNext: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        initViews()
        displayResult()
        setupListeners()
    }

    private fun initViews() {
        cardResult = findViewById(R.id.cardResult)
        tvResultIcon = findViewById(R.id.tvResultIcon)
        tvResult = findViewById(R.id.tvResult)
        tvCorrectAnswer = findViewById(R.id.tvCorrectAnswer)
        tvExplanation = findViewById(R.id.tvExplanation)
        btnNext = findViewById(R.id.btnNext)
    }

    private fun displayResult() {
        val isCorrect = intent.getBooleanExtra(EXTRA_IS_CORRECT, false)
        val correctAnswer = intent.getStringExtra(EXTRA_CORRECT_ANSWER) ?: ""
        val explanation = intent.getStringExtra(EXTRA_EXPLANATION) ?: ""
        val isSetComplete = intent.getBooleanExtra(EXTRA_IS_SET_COMPLETE, false)

        if (isCorrect) {
            // 正解の表示
            cardResult.setCardBackgroundColor(
                ContextCompat.getColor(this, R.color.correct_green_light)
            )
            tvResultIcon.text = "○"
            tvResultIcon.setTextColor(ContextCompat.getColor(this, R.color.correct_green))
            tvResult.text = getString(R.string.result_correct)
            tvResult.setTextColor(ContextCompat.getColor(this, R.color.correct_green))
        } else {
            // 不正解の表示
            cardResult.setCardBackgroundColor(
                ContextCompat.getColor(this, R.color.incorrect_red_light)
            )
            tvResultIcon.text = "×"
            tvResultIcon.setTextColor(ContextCompat.getColor(this, R.color.incorrect_red))
            tvResult.text = getString(R.string.result_incorrect)
            tvResult.setTextColor(ContextCompat.getColor(this, R.color.incorrect_red))
        }

        tvCorrectAnswer.text = correctAnswer
        tvExplanation.text = explanation

        // ボタンテキストの設定
        btnNext.text = if (isSetComplete) {
            "セット結果を見る"
        } else {
            getString(R.string.next_question)
        }
    }

    private fun setupListeners() {
        btnNext.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // 戻るボタンでも正常に次へ進む
        setResult(RESULT_OK)
        super.onBackPressed()
    }
}

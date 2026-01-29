package com.example.kadai09_pi12a_36

import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton

class ResultActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_IS_CORRECT = "extra_is_correct"
        const val EXTRA_CORRECT_ANSWER = "extra_correct_answer"
        const val EXTRA_EXPLANATION = "extra_explanation"
        const val EXTRA_IS_SET_COMPLETE = "extra_is_set_complete"
    }

    private lateinit var layoutResultIcon: LinearLayout
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
        startAnimations()
    }

    private fun initViews() {
        layoutResultIcon = findViewById(R.id.layoutResultIcon)
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
            tvResultIcon.text = "○"
            tvResultIcon.setTextColor(ContextCompat.getColor(this, R.color.correct_green))
            tvResult.text = "正解!"
            tvResult.setTextColor(ContextCompat.getColor(this, R.color.correct_green))
        } else {
            tvResultIcon.text = "×"
            tvResultIcon.setTextColor(ContextCompat.getColor(this, R.color.incorrect_red))
            tvResult.text = "不正解..."
            tvResult.setTextColor(ContextCompat.getColor(this, R.color.incorrect_red))
        }

        tvCorrectAnswer.text = correctAnswer
        tvExplanation.text = explanation

        btnNext.text = if (isSetComplete) {
            "セット結果を見る"
        } else {
            "次の問題へ"
        }
    }

    private fun setupListeners() {
        btnNext.setOnClickListener {
            it.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction {
                    it.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .withEndAction {
                            setResult(RESULT_OK)
                            finish()
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        }
                        .start()
                }
                .start()
        }
    }

    private fun startAnimations() {
        val scaleIn = AnimationUtils.loadAnimation(this, R.anim.scale_in)
        layoutResultIcon.startAnimation(scaleIn)

        tvResult.alpha = 0f
        tvResult.postDelayed({
            tvResult.animate()
                .alpha(1f)
                .setDuration(400)
                .start()
        }, 300)

        btnNext.alpha = 0f
        btnNext.translationY = 50f
        btnNext.postDelayed({
            btnNext.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(400)
                .setInterpolator(android.view.animation.DecelerateInterpolator())
                .start()
        }, 500)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        setResult(RESULT_OK)
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}

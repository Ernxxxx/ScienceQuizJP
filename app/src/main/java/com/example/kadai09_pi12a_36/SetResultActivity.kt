package com.example.kadai09_pi12a_36

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton

class SetResultActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_SET_CORRECT = "extra_set_correct"
        const val EXTRA_TOTAL_CORRECT = "extra_total_correct"
        const val EXTRA_TOTAL_ANSWERED = "extra_total_answered"
        const val EXTRA_HAS_MORE = "extra_has_more"
    }

    private lateinit var layoutScoreCircle: LinearLayout
    private lateinit var tvSetScore: TextView
    private lateinit var tvMessage: TextView
    private lateinit var tvTotalScore: TextView
    private lateinit var btnNextSet: MaterialButton
    private lateinit var btnBackHome: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_result)

        initViews()
        displayResult()
        setupListeners()
        startAnimations()
    }

    private fun initViews() {
        layoutScoreCircle = findViewById(R.id.layoutScoreCircle)
        tvSetScore = findViewById(R.id.tvSetScore)
        tvMessage = findViewById(R.id.tvMessage)
        tvTotalScore = findViewById(R.id.tvTotalScore)
        btnNextSet = findViewById(R.id.btnNextSet)
        btnBackHome = findViewById(R.id.btnBackHome)
    }

    private fun displayResult() {
        val setCorrect = intent.getIntExtra(EXTRA_SET_CORRECT, 0)
        val totalCorrect = intent.getIntExtra(EXTRA_TOTAL_CORRECT, 0)
        val totalAnswered = intent.getIntExtra(EXTRA_TOTAL_ANSWERED, 0)
        val hasMore = intent.getBooleanExtra(EXTRA_HAS_MORE, false)

        val questionsPerSet = QuizSession.QUESTIONS_PER_SET
        tvSetScore.text = "$setCorrect/$questionsPerSet"
        tvTotalScore.text = "累計: $totalCorrect / $totalAnswered 正解"

        val percentage = (setCorrect.toFloat() / questionsPerSet * 100).toInt()
        val (color, message) = when {
            percentage >= 100 -> Pair(R.color.correct_green, "パーフェクト!")
            percentage >= 70 -> Pair(R.color.accent, "よくできました!")
            percentage >= 40 -> Pair(R.color.nebula_purple, "まずまずです!")
            else -> Pair(R.color.incorrect_red, "もう少し頑張ろう!")
        }

        tvSetScore.setTextColor(ContextCompat.getColor(this, color))
        tvMessage.text = message
        tvMessage.setTextColor(ContextCompat.getColor(this, color))

        if (hasMore) {
            btnNextSet.visibility = View.VISIBLE
        } else {
            btnNextSet.visibility = View.GONE
        }
    }

    private fun setupListeners() {
        btnNextSet.setOnClickListener {
            animateButton(it) {
                setResult(RESULT_OK)
                finish()
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        }

        btnBackHome.setOnClickListener {
            animateButton(it) {
                setResult(RESULT_CANCELED)
                finish()
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        }
    }

    private fun animateButton(view: View, onComplete: () -> Unit) {
        view.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(100)
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .withEndAction(onComplete)
                    .start()
            }
            .start()
    }

    private fun startAnimations() {
        val scaleIn = AnimationUtils.loadAnimation(this, R.anim.scale_in)
        layoutScoreCircle.startAnimation(scaleIn)

        tvMessage.alpha = 0f
        tvMessage.postDelayed({
            tvMessage.animate()
                .alpha(1f)
                .setDuration(400)
                .start()
        }, 400)

        tvTotalScore.alpha = 0f
        tvTotalScore.postDelayed({
            tvTotalScore.animate()
                .alpha(1f)
                .setDuration(400)
                .start()
        }, 500)

        val buttons = listOf(btnNextSet, btnBackHome)
        buttons.forEachIndexed { index, button ->
            if (button.visibility == View.VISIBLE) {
                button.alpha = 0f
                button.translationY = 30f
                button.postDelayed({
                    button.animate()
                        .alpha(1f)
                        .translationY(0f)
                        .setDuration(400)
                        .setInterpolator(android.view.animation.DecelerateInterpolator())
                        .start()
                }, (600 + index * 100).toLong())
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        setResult(RESULT_CANCELED)
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}

package com.example.kadai09_pi12a_36

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
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

        // スコア表示
        tvSetScore.text = "$setCorrect/3"
        tvTotalScore.text = "累計: $totalCorrect / $totalAnswered 正解"

        // スコアに応じた色とメッセージ
        val (color, message) = when (setCorrect) {
            3 -> Pair(R.color.correct_green, getString(R.string.message_perfect))
            2 -> Pair(R.color.level2, getString(R.string.message_great))
            1 -> Pair(R.color.level3, getString(R.string.message_good))
            else -> Pair(R.color.incorrect_red, getString(R.string.message_try_again))
        }

        // スコアサークルの背景色を設定
        val circleDrawable = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(ContextCompat.getColor(this@SetResultActivity, color))
            alpha = 30
        }
        layoutScoreCircle.background = circleDrawable

        tvSetScore.setTextColor(ContextCompat.getColor(this, color))
        tvMessage.text = message
        tvMessage.setTextColor(ContextCompat.getColor(this, color))

        // 次のセットボタンの表示制御
        if (hasMore) {
            btnNextSet.visibility = View.VISIBLE
            btnNextSet.text = getString(R.string.next_set)
        } else {
            btnNextSet.visibility = View.GONE
        }
    }

    private fun setupListeners() {
        btnNextSet.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }

        btnBackHome.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        setResult(RESULT_CANCELED)
        super.onBackPressed()
    }
}

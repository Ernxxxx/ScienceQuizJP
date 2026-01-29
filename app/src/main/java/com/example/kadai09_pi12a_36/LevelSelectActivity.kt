package com.example.kadai09_pi12a_36

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class LevelSelectActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CATEGORY = "extra_category"

        private val categoryEnglishNames = mapOf(
            "宇宙" to "SPACE",
            "物理" to "PHYSICS",
            "化学" to "CHEMISTRY",
            "生物" to "BIOLOGY",
            "地学" to "EARTH"
        )
    }

    private lateinit var btnBack: ImageButton
    private lateinit var tvCategoryTitle: TextView
    private lateinit var tvCategoryCount: TextView
    private lateinit var tvLevel1Count: TextView
    private lateinit var tvLevel2Count: TextView
    private lateinit var tvLevel3Count: TextView
    private lateinit var tvLevel4Count: TextView
    private lateinit var tvLevel5Count: TextView
    private lateinit var cardLevel1: CardView
    private lateinit var cardLevel2: CardView
    private lateinit var cardLevel3: CardView
    private lateinit var cardLevel4: CardView
    private lateinit var cardLevel5: CardView

    private lateinit var repository: QuizRepository
    private var category: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_level_select)

        category = intent.getStringExtra(EXTRA_CATEGORY) ?: ""
        if (category.isEmpty()) {
            finish()
            return
        }

        initViews()
        initRepository()
        setupListeners()
        startAnimations()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        tvCategoryTitle = findViewById(R.id.tvCategoryTitle)
        tvCategoryCount = findViewById(R.id.tvCategoryCount)
        tvLevel1Count = findViewById(R.id.tvLevel1Count)
        tvLevel2Count = findViewById(R.id.tvLevel2Count)
        tvLevel3Count = findViewById(R.id.tvLevel3Count)
        tvLevel4Count = findViewById(R.id.tvLevel4Count)
        tvLevel5Count = findViewById(R.id.tvLevel5Count)
        cardLevel1 = findViewById(R.id.cardLevel1)
        cardLevel2 = findViewById(R.id.cardLevel2)
        cardLevel3 = findViewById(R.id.cardLevel3)
        cardLevel4 = findViewById(R.id.cardLevel4)
        cardLevel5 = findViewById(R.id.cardLevel5)
    }

    private fun initRepository() {
        repository = QuizRepository(this)

        tvCategoryTitle.text = category
        val totalInCategory = repository.getQuestionCountByCategory(category)
        tvCategoryCount.text = "このカテゴリ: ${totalInCategory}問"

        tvLevel1Count.text = "${repository.getQuestionCountByCategoryAndLevel(category, 1)}問"
        tvLevel2Count.text = "${repository.getQuestionCountByCategoryAndLevel(category, 2)}問"
        tvLevel3Count.text = "${repository.getQuestionCountByCategoryAndLevel(category, 3)}問"
        tvLevel4Count.text = "${repository.getQuestionCountByCategoryAndLevel(category, 4)}問"
        tvLevel5Count.text = "${repository.getQuestionCountByCategoryAndLevel(category, 5)}問"
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        cardLevel1.setOnClickListener { animateAndStartQuiz(1, it) }
        cardLevel2.setOnClickListener { animateAndStartQuiz(2, it) }
        cardLevel3.setOnClickListener { animateAndStartQuiz(3, it) }
        cardLevel4.setOnClickListener { animateAndStartQuiz(4, it) }
        cardLevel5.setOnClickListener { animateAndStartQuiz(5, it) }
    }

    private fun startAnimations() {
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        tvCategoryTitle.startAnimation(fadeIn)

        val cards = listOf(cardLevel1, cardLevel2, cardLevel3, cardLevel4, cardLevel5)
        cards.forEachIndexed { index, card ->
            card.alpha = 0f
            card.translationX = 100f
            card.postDelayed({
                card.animate()
                    .alpha(1f)
                    .translationX(0f)
                    .setDuration(400)
                    .setInterpolator(android.view.animation.DecelerateInterpolator())
                    .start()
            }, (200 + index * 80).toLong())
        }
    }

    private fun animateAndStartQuiz(level: Int, view: View) {
        view.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(100)
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .withEndAction {
                        startQuiz(level)
                    }
                    .start()
            }
            .start()
    }

    private fun startQuiz(level: Int) {
        val count = repository.getQuestionCountByCategoryAndLevel(category, level)
        if (count == 0) {
            Toast.makeText(this, "この難易度の問題がありません", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(this, QuizActivity::class.java).apply {
            putExtra(QuizActivity.EXTRA_CATEGORY, category)
            putExtra(QuizActivity.EXTRA_LEVEL, level)
        }
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}

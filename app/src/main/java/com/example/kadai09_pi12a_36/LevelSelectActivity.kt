package com.example.kadai09_pi12a_36

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class LevelSelectActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CATEGORY = "extra_category"
    }

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
    }

    private fun initViews() {
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

        // 各レベルの問題数を表示
        tvLevel1Count.text = "${repository.getQuestionCountByCategoryAndLevel(category, 1)}問"
        tvLevel2Count.text = "${repository.getQuestionCountByCategoryAndLevel(category, 2)}問"
        tvLevel3Count.text = "${repository.getQuestionCountByCategoryAndLevel(category, 3)}問"
        tvLevel4Count.text = "${repository.getQuestionCountByCategoryAndLevel(category, 4)}問"
        tvLevel5Count.text = "${repository.getQuestionCountByCategoryAndLevel(category, 5)}問"
    }

    private fun setupListeners() {
        cardLevel1.setOnClickListener { startQuiz(1) }
        cardLevel2.setOnClickListener { startQuiz(2) }
        cardLevel3.setOnClickListener { startQuiz(3) }
        cardLevel4.setOnClickListener { startQuiz(4) }
        cardLevel5.setOnClickListener { startQuiz(5) }
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
    }
}

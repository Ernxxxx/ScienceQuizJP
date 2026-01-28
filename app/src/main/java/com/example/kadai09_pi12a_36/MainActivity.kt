package com.example.kadai09_pi12a_36

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    private lateinit var tvTotalQuestions: TextView
    private lateinit var btnCategorySpace: MaterialButton
    private lateinit var btnCategoryPhysics: MaterialButton
    private lateinit var btnCategoryChemistry: MaterialButton
    private lateinit var btnCategoryBiology: MaterialButton
    private lateinit var btnCategoryEarth: MaterialButton

    private lateinit var repository: QuizRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        initRepository()
        setupListeners()
    }

    private fun initViews() {
        tvTotalQuestions = findViewById(R.id.tvTotalQuestions)
        btnCategorySpace = findViewById(R.id.btnCategorySpace)
        btnCategoryPhysics = findViewById(R.id.btnCategoryPhysics)
        btnCategoryChemistry = findViewById(R.id.btnCategoryChemistry)
        btnCategoryBiology = findViewById(R.id.btnCategoryBiology)
        btnCategoryEarth = findViewById(R.id.btnCategoryEarth)
    }

    private fun initRepository() {
        repository = QuizRepository(this)
        val totalCount = repository.getTotalQuestionCount()
        tvTotalQuestions.text = getString(R.string.total_questions, totalCount)
    }

    private fun setupListeners() {
        btnCategorySpace.setOnClickListener { openLevelSelect("宇宙") }
        btnCategoryPhysics.setOnClickListener { openLevelSelect("物理") }
        btnCategoryChemistry.setOnClickListener { openLevelSelect("化学") }
        btnCategoryBiology.setOnClickListener { openLevelSelect("生物") }
        btnCategoryEarth.setOnClickListener { openLevelSelect("地学") }
    }

    private fun openLevelSelect(category: String) {
        val intent = Intent(this, LevelSelectActivity::class.java).apply {
            putExtra(LevelSelectActivity.EXTRA_CATEGORY, category)
        }
        startActivity(intent)
    }
}

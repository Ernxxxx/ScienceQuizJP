package com.example.kadai09_pi12a_36

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    private lateinit var tvTitle: TextView
    private lateinit var tvSubtitle: TextView
    private lateinit var tvTotalQuestions: TextView
    private lateinit var layoutCategories: LinearLayout
    private lateinit var btnCategorySpace: MaterialButton
    private lateinit var btnCategoryPhysics: MaterialButton
    private lateinit var btnCategoryChemistry: MaterialButton
    private lateinit var btnCategoryBiology: MaterialButton
    private lateinit var btnCategoryEarth: MaterialButton
    private lateinit var btnAchievement: ImageButton

    private lateinit var repository: QuizRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        initRepository()
        setupListeners()
        startAnimations()
    }

    private fun initViews() {
        tvTitle = findViewById(R.id.tvTitle)
        tvSubtitle = findViewById(R.id.tvSubtitle)
        tvTotalQuestions = findViewById(R.id.tvTotalQuestions)
        layoutCategories = findViewById(R.id.layoutCategories)
        btnCategorySpace = findViewById(R.id.btnCategorySpace)
        btnCategoryPhysics = findViewById(R.id.btnCategoryPhysics)
        btnCategoryChemistry = findViewById(R.id.btnCategoryChemistry)
        btnCategoryBiology = findViewById(R.id.btnCategoryBiology)
        btnCategoryEarth = findViewById(R.id.btnCategoryEarth)
        btnAchievement = findViewById(R.id.btnAchievement)
    }

    private fun initRepository() {
        repository = QuizRepository(this)
        val totalCount = repository.getTotalQuestionCount()
        tvTotalQuestions.text = getString(R.string.total_questions, totalCount)
    }

    private fun setupListeners() {
        btnCategorySpace.setOnClickListener { animateAndOpen("宇宙", it) }
        btnCategoryPhysics.setOnClickListener { animateAndOpen("物理", it) }
        btnCategoryChemistry.setOnClickListener { animateAndOpen("化学", it) }
        btnCategoryBiology.setOnClickListener { animateAndOpen("生物", it) }
        btnCategoryEarth.setOnClickListener { animateAndOpen("地学", it) }

        btnAchievement.setOnClickListener {
            it.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(100)
                .withEndAction {
                    it.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .withEndAction {
                            openAchievements()
                        }
                        .start()
                }
                .start()
        }
    }

    private fun openAchievements() {
        val intent = Intent(this, AchievementActivity::class.java)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun startAnimations() {
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val scaleIn = AnimationUtils.loadAnimation(this, R.anim.scale_in)

        tvTitle.startAnimation(fadeIn)
        tvSubtitle.postDelayed({
            tvSubtitle.startAnimation(fadeIn)
        }, 150)
        tvTotalQuestions.postDelayed({
            tvTotalQuestions.startAnimation(scaleIn)
        }, 300)

        // Staggered animation for category buttons
        val buttons = listOf(btnCategorySpace, btnCategoryPhysics, btnCategoryChemistry, btnCategoryBiology, btnCategoryEarth)
        buttons.forEachIndexed { index, button ->
            button.alpha = 0f
            button.translationY = 50f
            button.postDelayed({
                button.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(400)
                    .setInterpolator(android.view.animation.DecelerateInterpolator())
                    .start()
            }, (400 + index * 100).toLong())
        }
    }

    private fun animateAndOpen(category: String, view: View) {
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
                        openLevelSelect(category)
                    }
                    .start()
            }
            .start()
    }

    private fun openLevelSelect(category: String) {
        val intent = Intent(this, LevelSelectActivity::class.java).apply {
            putExtra(LevelSelectActivity.EXTRA_CATEGORY, category)
        }
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}

package com.example.kadai09_pi12a_36

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AchievementActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var tvProgress: TextView
    private lateinit var rvAchievements: RecyclerView

    private lateinit var achievementManager: AchievementManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achievement)

        achievementManager = AchievementManager(this)

        initViews()
        setupListeners()
        loadAchievements()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        tvProgress = findViewById(R.id.tvProgress)
        rvAchievements = findViewById(R.id.rvAchievements)
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    private fun loadAchievements() {
        val allAchievements = achievementManager.getAllAchievements()
        val unlockedCount = achievementManager.getUnlockedCount()
        val totalCount = allAchievements.size

        tvProgress.text = "$unlockedCount / $totalCount"

        rvAchievements.layoutManager = LinearLayoutManager(this)
        rvAchievements.adapter = AchievementAdapter(allAchievements)
    }
}

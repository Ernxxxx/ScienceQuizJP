package com.example.kadai09_pi12a_36

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.button.MaterialButton

class QuizActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CATEGORY = "extra_category"
        const val EXTRA_LEVEL = "extra_level"

        private val categoryEnglishNames = mapOf(
            "宇宙" to "SPACE",
            "物理" to "PHYSICS",
            "化学" to "CHEMISTRY",
            "生物" to "BIOLOGY",
            "地学" to "EARTH"
        )
    }

    private lateinit var tvSetProgress: TextView
    private lateinit var tvScore: TextView
    private lateinit var tvQuestionNumber: TextView
    private lateinit var tvCategory: TextView
    private lateinit var tvLevel: TextView
    private lateinit var tvQuestion: TextView
    private lateinit var cardQuestion: CardView
    private lateinit var radioGroupChoices: RadioGroup
    private lateinit var radioChoice1: RadioButton
    private lateinit var radioChoice2: RadioButton
    private lateinit var radioChoice3: RadioButton
    private lateinit var radioChoice4: RadioButton
    private lateinit var btnAnswer: MaterialButton
    private lateinit var btnReset: MaterialButton
    private lateinit var btnBack: ImageButton
    private lateinit var btnHint: MaterialButton

    private lateinit var repository: QuizRepository
    private lateinit var session: QuizSession
    private lateinit var achievementManager: AchievementManager

    private var usedHintForCurrentQuestion = false
    private var hintUsedChoices = mutableListOf<Int>()

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            if (session.isSetComplete()) {
                showSetResult()
            } else if (session.hasMoreQuestions()) {
                displayCurrentQuestion()
            } else {
                showNoMoreQuestions()
            }
        }
    }

    private val setResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        when (result.resultCode) {
            RESULT_OK -> {
                session.resetSetProgress()
                if (session.hasMoreQuestions()) {
                    displayCurrentQuestion()
                } else {
                    showNoMoreQuestions()
                }
            }
            RESULT_CANCELED -> {
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        initViews()
        initSession()
        setupListeners()
        displayCurrentQuestion()
    }

    private fun initViews() {
        tvSetProgress = findViewById(R.id.tvSetProgress)
        tvScore = findViewById(R.id.tvScore)
        tvQuestionNumber = findViewById(R.id.tvQuestionNumber)
        tvCategory = findViewById(R.id.tvCategory)
        tvLevel = findViewById(R.id.tvLevel)
        tvQuestion = findViewById(R.id.tvQuestion)
        cardQuestion = findViewById(R.id.cardQuestion)
        radioGroupChoices = findViewById(R.id.radioGroupChoices)
        radioChoice1 = findViewById(R.id.radioChoice1)
        radioChoice2 = findViewById(R.id.radioChoice2)
        radioChoice3 = findViewById(R.id.radioChoice3)
        radioChoice4 = findViewById(R.id.radioChoice4)
        btnAnswer = findViewById(R.id.btnAnswer)
        btnReset = findViewById(R.id.btnReset)
        btnBack = findViewById(R.id.btnBack)
        btnHint = findViewById(R.id.btnHint)
    }

    private fun initSession() {
        repository = QuizRepository(this)
        achievementManager = AchievementManager(this)
        val category = intent.getStringExtra(EXTRA_CATEGORY) ?: ""
        val level = intent.getIntExtra(EXTRA_LEVEL, 1)

        val questions = if (category.isNotEmpty()) {
            repository.getQuestionsByCategoryAndLevel(category, level)
        } else {
            repository.getQuestionsByLevel(level)
        }

        session = QuizSession(
            selectedLevel = level,
            selectedCategory = category,
            questions = questions
        )
    }

    private fun setupListeners() {
        btnAnswer.setOnClickListener {
            val selectedId = radioGroupChoices.checkedRadioButtonId
            if (selectedId == -1) {
                Toast.makeText(this, R.string.select_answer_warning, Toast.LENGTH_SHORT).show()
                shakeView(radioGroupChoices)
                return@setOnClickListener
            }

            val selectedIndex = when (selectedId) {
                R.id.radioChoice1 -> 0
                R.id.radioChoice2 -> 1
                R.id.radioChoice3 -> 2
                R.id.radioChoice4 -> 3
                else -> -1
            }

            animateButtonAndCheck(selectedIndex)
        }

        btnReset.setOnClickListener {
            showResetConfirmDialog()
        }

        btnBack.setOnClickListener {
            showExitConfirmDialog()
        }

        btnHint.setOnClickListener {
            useHint()
        }
    }

    private fun shakeView(view: android.view.View) {
        view.animate()
            .translationX(-10f)
            .setDuration(50)
            .withEndAction {
                view.animate()
                    .translationX(10f)
                    .setDuration(50)
                    .withEndAction {
                        view.animate()
                            .translationX(0f)
                            .setDuration(50)
                            .start()
                    }
                    .start()
            }
            .start()
    }

    private fun animateButtonAndCheck(selectedIndex: Int) {
        btnAnswer.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(100)
            .withEndAction {
                btnAnswer.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .withEndAction {
                        checkAnswer(selectedIndex)
                    }
                    .start()
            }
            .start()
    }

    private fun showExitConfirmDialog() {
        AlertDialog.Builder(this)
            .setTitle("終了確認")
            .setMessage("クイズを終了しますか？\n進捗は保存されません。")
            .setPositiveButton(R.string.yes) { _, _ ->
                finish()
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    private fun useHint() {
        if (usedHintForCurrentQuestion) {
            Toast.makeText(this, "この問題では既にヒントを使用しました", Toast.LENGTH_SHORT).show()
            return
        }

        val question = session.getCurrentQuestion() ?: return
        val correctIndex = question.answerIndex

        // Find 2 wrong answers to eliminate
        val wrongIndices = (0..3).filter { it != correctIndex }.shuffled().take(2)

        val choices = listOf(radioChoice1, radioChoice2, radioChoice3, radioChoice4)
        wrongIndices.forEach { index ->
            choices[index].apply {
                alpha = 0.3f
                isEnabled = false
                setTextColor(resources.getColor(android.R.color.darker_gray, theme))
            }
            hintUsedChoices.add(index)
        }

        usedHintForCurrentQuestion = true
        btnHint.isEnabled = false
        btnHint.alpha = 0.5f

        Toast.makeText(this, "2つの不正解を消去しました", Toast.LENGTH_SHORT).show()
    }

    private fun displayCurrentQuestion() {
        val question = session.getCurrentQuestion() ?: return

        // Reset hint state
        usedHintForCurrentQuestion = false
        hintUsedChoices.clear()
        btnHint.isEnabled = true
        btnHint.alpha = 1f

        radioGroupChoices.clearCheck()

        // Reset all choices
        val choices = listOf(radioChoice1, radioChoice2, radioChoice3, radioChoice4)
        choices.forEach { choice ->
            choice.alpha = 1f
            choice.isEnabled = true
            choice.setTextColor(resources.getColor(android.R.color.white, theme))
        }

        val questionNum = String.format("Q.%02d", session.setAnswered + 1)
        tvQuestionNumber.text = questionNum
        tvSetProgress.text = getString(R.string.set_progress, session.setAnswered + 1, QuizSession.QUESTIONS_PER_SET)
        tvScore.text = "正解 ${session.totalCorrect}/${session.totalAnswered}"
        tvCategory.text = question.category
        tvLevel.text = "Lv.${question.level}"
        tvQuestion.text = question.question

        val labels = listOf("A", "B", "C", "D")
        radioChoice1.text = "${labels[0]}. ${question.choices[0]}"
        radioChoice2.text = "${labels[1]}. ${question.choices[1]}"
        radioChoice3.text = "${labels[2]}. ${question.choices[2]}"
        radioChoice4.text = "${labels[3]}. ${question.choices[3]}"

        // Animate question card
        val scaleIn = AnimationUtils.loadAnimation(this, R.anim.scale_in)
        cardQuestion.startAnimation(scaleIn)

        // Animate choices
        choices.forEachIndexed { index, choice ->
            choice.alpha = 0f
            choice.translationX = 50f
            choice.postDelayed({
                choice.animate()
                    .alpha(1f)
                    .translationX(0f)
                    .setDuration(300)
                    .setInterpolator(android.view.animation.DecelerateInterpolator())
                    .start()
            }, (100 + index * 60).toLong())
        }
    }

    private fun checkAnswer(selectedIndex: Int) {
        val question = session.getCurrentQuestion() ?: return
        val isCorrect = selectedIndex == question.answerIndex

        session.recordAnswer(isCorrect)

        // Track achievements
        val newAchievements = achievementManager.onQuestionAnswered(
            isCorrect = isCorrect,
            category = question.category,
            usedHint = usedHintForCurrentQuestion
        )

        // Show achievement notifications
        newAchievements.forEach { achievement ->
            showAchievementUnlocked(achievement)
        }

        session.moveToNextQuestion()

        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra(ResultActivity.EXTRA_IS_CORRECT, isCorrect)
            putExtra(ResultActivity.EXTRA_CORRECT_ANSWER, question.choices[question.answerIndex])
            putExtra(ResultActivity.EXTRA_EXPLANATION, question.explanation)
            putExtra(ResultActivity.EXTRA_IS_SET_COMPLETE, session.isSetComplete())
        }
        resultLauncher.launch(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun showAchievementUnlocked(achievement: Achievement) {
        Toast.makeText(
            this,
            "${achievement.icon} 実績解除: ${achievement.title}",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showSetResult() {
        // Check for perfect set achievement
        if (session.setCorrect == QuizSession.QUESTIONS_PER_SET) {
            achievementManager.onPerfectSet()?.let { achievement ->
                showAchievementUnlocked(achievement)
            }
        }

        val intent = Intent(this, SetResultActivity::class.java).apply {
            putExtra(SetResultActivity.EXTRA_SET_CORRECT, session.setCorrect)
            putExtra(SetResultActivity.EXTRA_TOTAL_CORRECT, session.totalCorrect)
            putExtra(SetResultActivity.EXTRA_TOTAL_ANSWERED, session.totalAnswered)
            putExtra(SetResultActivity.EXTRA_HAS_MORE, session.hasMoreQuestions())
        }
        setResultLauncher.launch(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun showNoMoreQuestions() {
        AlertDialog.Builder(this)
            .setTitle(R.string.no_more_questions)
            .setMessage("累計結果: ${session.totalCorrect} / ${session.totalAnswered} 正解")
            .setPositiveButton(R.string.back_to_home) { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun showResetConfirmDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.reset_confirm_title)
            .setMessage(R.string.reset_confirm_message)
            .setPositiveButton(R.string.yes) { _, _ ->
                resetQuiz()
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    private fun resetQuiz() {
        session.resetAll()
        usedHintForCurrentQuestion = false
        hintUsedChoices.clear()
        val questions = if (session.selectedCategory.isNotEmpty()) {
            repository.getQuestionsByCategoryAndLevel(session.selectedCategory, session.selectedLevel)
        } else {
            repository.getQuestionsByLevel(session.selectedLevel)
        }
        session = session.copy(questions = questions)
        displayCurrentQuestion()
        Toast.makeText(this, "リセットしました", Toast.LENGTH_SHORT).show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("currentIndex", session.currentQuestionIndex)
        outState.putInt("totalAnswered", session.totalAnswered)
        outState.putInt("totalCorrect", session.totalCorrect)
        outState.putInt("setAnswered", session.setAnswered)
        outState.putInt("setCorrect", session.setCorrect)
        outState.putBoolean("usedHint", usedHintForCurrentQuestion)
        outState.putIntegerArrayList("hintUsedChoices", ArrayList(hintUsedChoices))
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        session.currentQuestionIndex = savedInstanceState.getInt("currentIndex", 0)
        session.totalAnswered = savedInstanceState.getInt("totalAnswered", 0)
        session.totalCorrect = savedInstanceState.getInt("totalCorrect", 0)
        session.setAnswered = savedInstanceState.getInt("setAnswered", 0)
        session.setCorrect = savedInstanceState.getInt("setCorrect", 0)
        usedHintForCurrentQuestion = savedInstanceState.getBoolean("usedHint", false)
        hintUsedChoices = savedInstanceState.getIntegerArrayList("hintUsedChoices")?.toMutableList() ?: mutableListOf()
        displayCurrentQuestion()

        // Restore hint state visually
        if (usedHintForCurrentQuestion) {
            btnHint.isEnabled = false
            btnHint.alpha = 0.5f
            val choices = listOf(radioChoice1, radioChoice2, radioChoice3, radioChoice4)
            hintUsedChoices.forEach { index ->
                choices[index].apply {
                    alpha = 0.3f
                    isEnabled = false
                    setTextColor(resources.getColor(android.R.color.darker_gray, theme))
                }
            }
        }
    }
}

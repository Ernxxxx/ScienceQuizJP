package com.example.kadai09_pi12a_36

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class QuizActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CATEGORY = "extra_category"
        const val EXTRA_LEVEL = "extra_level"
    }

    private lateinit var tvSetProgress: TextView
    private lateinit var tvScore: TextView
    private lateinit var tvQuestionNumber: TextView
    private lateinit var tvCategory: TextView
    private lateinit var tvLevel: TextView
    private lateinit var tvQuestion: TextView
    private lateinit var radioGroupChoices: RadioGroup
    private lateinit var radioChoice1: RadioButton
    private lateinit var radioChoice2: RadioButton
    private lateinit var radioChoice3: RadioButton
    private lateinit var radioChoice4: RadioButton
    private lateinit var btnAnswer: MaterialButton
    private lateinit var btnReset: MaterialButton
    private lateinit var btnBack: ImageButton

    private lateinit var repository: QuizRepository
    private lateinit var session: QuizSession

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
        radioGroupChoices = findViewById(R.id.radioGroupChoices)
        radioChoice1 = findViewById(R.id.radioChoice1)
        radioChoice2 = findViewById(R.id.radioChoice2)
        radioChoice3 = findViewById(R.id.radioChoice3)
        radioChoice4 = findViewById(R.id.radioChoice4)
        btnAnswer = findViewById(R.id.btnAnswer)
        btnReset = findViewById(R.id.btnReset)
        btnBack = findViewById(R.id.btnBack)
    }

    private fun initSession() {
        repository = QuizRepository(this)
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
                return@setOnClickListener
            }

            val selectedIndex = when (selectedId) {
                R.id.radioChoice1 -> 0
                R.id.radioChoice2 -> 1
                R.id.radioChoice3 -> 2
                R.id.radioChoice4 -> 3
                else -> -1
            }

            checkAnswer(selectedIndex)
        }

        btnReset.setOnClickListener {
            showResetConfirmDialog()
        }

        btnBack.setOnClickListener {
            showExitConfirmDialog()
        }
    }

    private fun showExitConfirmDialog() {
        AlertDialog.Builder(this)
            .setTitle("終了確認")
            .setMessage("クイズを終了しますか？\n進捗は保存されません。")
            .setPositiveButton(R.string.yes) { _, _ ->
                finish()
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    private fun displayCurrentQuestion() {
        val question = session.getCurrentQuestion() ?: return

        radioGroupChoices.clearCheck()

        tvQuestionNumber.text = getString(R.string.question_number, session.totalAnswered + 1)
        tvSetProgress.text = getString(R.string.set_progress, session.setAnswered + 1, QuizSession.QUESTIONS_PER_SET)
        tvScore.text = getString(R.string.score_display, session.totalCorrect, session.totalAnswered)
        tvCategory.text = getString(R.string.category_label, question.category)
        tvLevel.text = getString(R.string.level_label, question.level)
        tvQuestion.text = question.question

        val labels = listOf("A", "B", "C", "D")
        radioChoice1.text = "${labels[0]}. ${question.choices[0]}"
        radioChoice2.text = "${labels[1]}. ${question.choices[1]}"
        radioChoice3.text = "${labels[2]}. ${question.choices[2]}"
        radioChoice4.text = "${labels[3]}. ${question.choices[3]}"
    }

    private fun checkAnswer(selectedIndex: Int) {
        val question = session.getCurrentQuestion() ?: return
        val isCorrect = selectedIndex == question.answerIndex

        session.recordAnswer(isCorrect)
        session.moveToNextQuestion()

        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra(ResultActivity.EXTRA_IS_CORRECT, isCorrect)
            putExtra(ResultActivity.EXTRA_CORRECT_ANSWER, question.choices[question.answerIndex])
            putExtra(ResultActivity.EXTRA_EXPLANATION, question.explanation)
            putExtra(ResultActivity.EXTRA_IS_SET_COMPLETE, session.isSetComplete())
        }
        resultLauncher.launch(intent)
    }

    private fun showSetResult() {
        val intent = Intent(this, SetResultActivity::class.java).apply {
            putExtra(SetResultActivity.EXTRA_SET_CORRECT, session.setCorrect)
            putExtra(SetResultActivity.EXTRA_TOTAL_CORRECT, session.totalCorrect)
            putExtra(SetResultActivity.EXTRA_TOTAL_ANSWERED, session.totalAnswered)
            putExtra(SetResultActivity.EXTRA_HAS_MORE, session.hasMoreQuestions())
        }
        setResultLauncher.launch(intent)
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
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        session.currentQuestionIndex = savedInstanceState.getInt("currentIndex", 0)
        session.totalAnswered = savedInstanceState.getInt("totalAnswered", 0)
        session.totalCorrect = savedInstanceState.getInt("totalCorrect", 0)
        session.setAnswered = savedInstanceState.getInt("setAnswered", 0)
        session.setCorrect = savedInstanceState.getInt("setCorrect", 0)
        displayCurrentQuestion()
    }
}

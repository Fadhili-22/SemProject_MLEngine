package com.example.mindcheck.ui

import androidx.lifecycle.ViewModel
import com.example.mindcheck.data.Question
import com.example.mindcheck.data.QuestionBank
import com.example.mindcheck.screening.LevelClassifier
import com.example.mindcheck.screening.RiskLevel
import com.example.mindcheck.screening.ScreeningResult

/** How a student scored in one area, e.g. Sleep: 6 out of 24. */
data class SectionScore(val section: String, val score: Int, val maxScore: Int)

/**
 * Step 5: shared by the Check-in and Result screens, so the answers survive moving between
 * screens and rotating the phone.
 * The answers are private (OOP: encapsulation): screens can only change them through recordAnswer().
 */
class ScreeningViewModel : ViewModel() {

    val questions: List<Question> = QuestionBank.all

    var currentIndex = 0

    private val answers = mutableMapOf<String, Int>()

    var result: ScreeningResult? = null
        private set

    fun answerFor(questionId: String): Int? = answers[questionId]

    fun recordAnswer(questionId: String, value: Int) {
        answers[questionId] = value
    }

    fun isLastQuestion(): Boolean = currentIndex == questions.lastIndex

    /** Index of the first question with no answer, or -1 if all are answered. */
    fun firstUnansweredIndex(): Int = questions.indexOfFirst { it.id !in answers }

    /**
     * Step 7: runs the ML model on the 21 answers it uses, and checks the safety question separately.
     * LevelClassifier throws IllegalArgumentException if an answer is missing or out of range.
     */
    fun finish() {
        val modelAnswers = answers.filterKeys { it in LevelClassifier.featureNames }
        val level = LevelClassifier.predictLevel(modelAnswers)
        val safetyAnswer = answers[QuestionBank.SAFETY_QUESTION_ID] ?: 0
        result = ScreeningResult(
            riskLevel = RiskLevel.from(level),
            needsUrgentSupport = safetyAnswer > 0,
        )
    }

    /** Totals for each area from the 21 model questions. PSS starts at 1, so every score is shifted to start at 0. */
    fun sectionScores(): List<SectionScore> =
        QuestionBank.modelQuestions.groupBy { it.section }.map { (section, sectionQuestions) ->
            SectionScore(
                section = section,
                score = sectionQuestions.sumOf { (answers[it.id] ?: it.minValue) - it.minValue },
                maxScore = sectionQuestions.sumOf { it.maxValue - it.minValue },
            )
        }

    fun reset() {
        currentIndex = 0
        answers.clear()
        result = null
    }
}

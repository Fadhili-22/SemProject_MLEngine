package com.example.mindcheck.data

import com.example.mindcheck.screening.Level
import com.example.mindcheck.screening.LevelClassifier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

// Checks the app's questions fit the ML model, so a typo in QuestionBank cannot silently give wrong results.
class QuestionBankTest {

    @Test
    fun modelQuestionsAreExactlyTheModelsFeatures() {
        val ids = QuestionBank.modelQuestions.map { it.id }
        assertEquals(21, ids.size)
        assertEquals(LevelClassifier.featureNames.toSet(), ids.toSet())
    }

    @Test
    fun safetyQuestionIsNotSentToTheModel() {
        assertFalse(QuestionBank.SAFETY_QUESTION_ID in LevelClassifier.featureNames)
        assertEquals(QuestionBank.safetyQuestion, QuestionBank.all.last())
    }

    @Test
    fun answerScalesMatchTheModel() {
        // PHQ and GAD 0-3, ISI 0-4, PSS 1-5
        for (q in QuestionBank.modelQuestions) {
            val expected = when {
                q.id.startsWith("phq") || q.id.startsWith("gad") -> 0..3
                q.id.startsWith("isi") -> 0..4
                else -> 1..5
            }
            assertEquals("Scale for ${q.id}", expected, q.minValue..q.maxValue)
        }
    }

    @Test
    fun lowestAndHighestAnswersGiveLowestAndHighestLevels() {
        // The model throws if any value is outside its range, so this also checks every option value
        val lowest = QuestionBank.modelQuestions.associate { it.id to it.minValue }
        val highest = QuestionBank.modelQuestions.associate { it.id to it.maxValue }
        assertEquals(Level.OKAY, LevelClassifier.predictLevel(lowest))
        assertEquals(Level.MODERATE_OR_WORSE, LevelClassifier.predictLevel(highest))
    }
}

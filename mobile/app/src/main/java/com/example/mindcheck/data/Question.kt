package com.example.mindcheck.data

// Step 4 (Lectures 2-3): data classes hold data and get equals/toString for free.

/** One answer choice. [value] is the number the ML model expects for that choice. */
data class AnswerOption(val label: String, val value: Int)

/**
 * One screening question.
 * [id] is the name the ML model uses, e.g. "phq_q1".
 * [section] groups questions on screen: Mood, Anxiety, Sleep or Stress.
 */
data class Question(
    val id: String,
    val section: String,
    val prompt: String,
    val text: String,
    val options: List<AnswerOption>,
) {
    val minValue: Int get() = options.minOf { it.value }
    val maxValue: Int get() = options.maxOf { it.value }
}

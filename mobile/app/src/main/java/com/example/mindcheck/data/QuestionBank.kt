package com.example.mindcheck.data

/**
 * Step 4: all questions the app asks.
 *
 * [modelQuestions] are the 21 questions the ML model uses. Their ids and answer
 * values must match the model exactly (see mobile/CLAUDE.md):
 * - PHQ and GAD: 0 to 3
 * - ISI: 0 to 4 (item 6 is not used)
 * - PSS: 1 to 5 (the dataset stores PSS answers as 1 to 5, not 0 to 4)
 *
 * [safetyQuestion] is PHQ-9 question 9. The model does NOT use it. Any answer above
 * "Not at all" sends the student to crisis support, whatever the model says.
 */
object QuestionBank {

    private val frequencyTwoWeeks = listOf(
        AnswerOption("Not at all", 0),
        AnswerOption("Several days", 1),
        AnswerOption("More than half the days", 2),
        AnswerOption("Nearly every day", 3),
    )

    private val sleepSeverity = listOf(
        AnswerOption("None", 0),
        AnswerOption("Mild", 1),
        AnswerOption("Moderate", 2),
        AnswerOption("Severe", 3),
        AnswerOption("Very severe", 4),
    )

    private val sleepSatisfaction = listOf(
        AnswerOption("Very satisfied", 0),
        AnswerOption("Satisfied", 1),
        AnswerOption("Moderately satisfied", 2),
        AnswerOption("Dissatisfied", 3),
        AnswerOption("Very dissatisfied", 4),
    )

    private val sleepNoticeable = listOf(
        AnswerOption("Not at all noticeable", 0),
        AnswerOption("A little", 1),
        AnswerOption("Somewhat", 2),
        AnswerOption("Much", 3),
        AnswerOption("Very much noticeable", 4),
    )

    private val sleepInterfere = listOf(
        AnswerOption("Not at all interfering", 0),
        AnswerOption("A little", 1),
        AnswerOption("Somewhat", 2),
        AnswerOption("Much", 3),
        AnswerOption("Very much interfering", 4),
    )

    // PSS is numbered 1 to 5 because that is how the model was trained
    private val stressFrequency = listOf(
        AnswerOption("Never", 1),
        AnswerOption("Almost never", 2),
        AnswerOption("Sometimes", 3),
        AnswerOption("Fairly often", 4),
        AnswerOption("Very often", 5),
    )

    const val MOOD = "Mood"
    const val ANXIETY = "Anxiety"
    const val SLEEP = "Sleep"
    const val STRESS = "Stress"

    private const val LAST_TWO_WEEKS = "Over the last 2 weeks, how often have you been bothered by the following?"
    private const val SLEEP_PROMPT = "Thinking about your sleep over the last 2 weeks:"
    private const val LAST_MONTH = "In the last month, how often have you…"

    const val SAFETY_QUESTION_ID = "phq_q9"

    val modelQuestions: List<Question> = listOf(
        // PHQ-2 (mood)
        Question("phq_q1", MOOD, LAST_TWO_WEEKS, "Little interest or pleasure in doing things", frequencyTwoWeeks),
        Question("phq_q2", MOOD, LAST_TWO_WEEKS, "Feeling down, depressed, or hopeless", frequencyTwoWeeks),
        // GAD-7 (anxiety)
        Question("gad_q1", ANXIETY, LAST_TWO_WEEKS, "Feeling nervous, anxious, or on edge", frequencyTwoWeeks),
        Question("gad_q2", ANXIETY, LAST_TWO_WEEKS, "Not being able to stop or control worrying", frequencyTwoWeeks),
        Question("gad_q3", ANXIETY, LAST_TWO_WEEKS, "Worrying too much about different things", frequencyTwoWeeks),
        Question("gad_q4", ANXIETY, LAST_TWO_WEEKS, "Trouble relaxing", frequencyTwoWeeks),
        Question("gad_q5", ANXIETY, LAST_TWO_WEEKS, "Being so restless that it is hard to sit still", frequencyTwoWeeks),
        Question("gad_q6", ANXIETY, LAST_TWO_WEEKS, "Becoming easily annoyed or irritable", frequencyTwoWeeks),
        Question("gad_q7", ANXIETY, LAST_TWO_WEEKS, "Feeling afraid, as if something awful might happen", frequencyTwoWeeks),
        // ISI (sleep)
        Question("isi_q1", SLEEP, SLEEP_PROMPT, "Difficulty falling asleep", sleepSeverity),
        Question("isi_q2", SLEEP, SLEEP_PROMPT, "Difficulty staying asleep", sleepSeverity),
        Question("isi_q3", SLEEP, SLEEP_PROMPT, "Problems waking up too early", sleepSeverity),
        Question("isi_q4", SLEEP, SLEEP_PROMPT, "How satisfied or dissatisfied are you with your current sleep pattern?", sleepSatisfaction),
        Question("isi_q5", SLEEP, SLEEP_PROMPT, "How noticeable to others do you think your sleep problem is, in terms of affecting your quality of life?", sleepNoticeable),
        Question("isi_q7", SLEEP, SLEEP_PROMPT, "How much does your sleep problem interfere with your daily functioning (for example tiredness, concentration, mood)?", sleepInterfere),
        // PSS (stress)
        Question("pss_q1", STRESS, LAST_MONTH, "Been upset because of something that happened unexpectedly?", stressFrequency),
        Question("pss_q2", STRESS, LAST_MONTH, "Felt that you were unable to control the important things in your life?", stressFrequency),
        Question("pss_q3", STRESS, LAST_MONTH, "Felt nervous and stressed?", stressFrequency),
        Question("pss_q8", STRESS, LAST_MONTH, "Found that you could not cope with all the things that you had to do?", stressFrequency),
        Question("pss_q11", STRESS, LAST_MONTH, "Been angered because of things that happened that were outside of your control?", stressFrequency),
        Question("pss_q14", STRESS, LAST_MONTH, "Felt difficulties were piling up so high that you could not overcome them?", stressFrequency),
    )

    // PHQ-9 question 9, asked last and never passed to the model
    val safetyQuestion = Question(
        SAFETY_QUESTION_ID, MOOD, LAST_TWO_WEEKS,
        "Thoughts that you would be better off dead, or of hurting yourself in some way",
        frequencyTwoWeeks,
    )

    /** Everything the student answers, in order: the 21 model questions, then the safety question. */
    val all: List<Question> = modelQuestions + safetyQuestion
}

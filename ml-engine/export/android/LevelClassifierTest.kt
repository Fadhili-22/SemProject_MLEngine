package com.example.screening

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

// Test cases made by ExportToAndroid.ipynb. The expected levels and probabilities come from the Python model.
class LevelClassifierTest {

    private class Case(val answers: List<Int>, val level: Level, val probabilities: DoubleArray)

    private val cases = listOf(
        Case(listOf(0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1), Level.OKAY, doubleArrayOf(0.9874818320542379, 0.012510549223799296, 7.618721962840241e-06)),
        Case(listOf(1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 2, 1, 1, 0, 0, 1), Level.OKAY, doubleArrayOf(0.865037320240049, 0.1343165206682243, 0.000646159091726639)),
        Case(listOf(0, 1, 0, 0, 0, 0, 0, 1, 2, 1, 0, 0, 0, 0, 0, 2, 0, 2, 0, 0, 2), Level.OKAY, doubleArrayOf(0.9170182319049918, 0.0826166995845008, 0.0003650685105074961)),
        Case(listOf(0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1), Level.OKAY, doubleArrayOf(0.98407725376361, 0.015911187835056125, 1.1558401333837438e-05)),
        Case(listOf(1, 0, 0, 0, 1, 0, 0, 2, 2, 2, 0, 0, 0, 0, 0, 2, 0, 2, 1, 0, 5), Level.OKAY, doubleArrayOf(0.8376919551731408, 0.16030672263292203, 0.0020013221939371487)),
        Case(listOf(0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 2, 0, 1, 1, 0, 2), Level.OKAY, doubleArrayOf(0.9837073350478385, 0.0162776952388225, 1.4969713339149114e-05)),
        Case(listOf(0, 0, 0, 0, 0, 0, 0, 1, 2, 2, 0, 0, 0, 0, 0, 3, 0, 3, 0, 1, 2), Level.OKAY, doubleArrayOf(0.9790795093239798, 0.02089198789817262, 2.8502777847613258e-05)),
        Case(listOf(0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 1), Level.OKAY, doubleArrayOf(0.9846355690878447, 0.015353591562651713, 1.0839349503756078e-05)),
        Case(listOf(1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 1, 1, 0, 0, 0, 2, 1, 2, 1, 0, 2), Level.MILD, doubleArrayOf(0.06743235114589512, 0.7825956394569954, 0.14997200939710947)),
        Case(listOf(1, 1, 0, 0, 0, 0, 0, 2, 2, 2, 0, 0, 0, 0, 0, 2, 1, 2, 0, 0, 1), Level.MILD, doubleArrayOf(0.47763853316834765, 0.5008856153141821, 0.021475851517470192)),
        Case(listOf(1, 1, 0, 0, 0, 0, 1, 2, 2, 2, 0, 0, 1, 1, 1, 2, 2, 2, 1, 0, 3), Level.MILD, doubleArrayOf(0.14604504227049053, 0.7401782508362001, 0.11377670689330924)),
        Case(listOf(1, 2, 0, 0, 0, 0, 0, 1, 2, 2, 0, 0, 0, 0, 0, 2, 1, 1, 0, 0, 2), Level.MILD, doubleArrayOf(0.15944987636034003, 0.7400599067385708, 0.10049021690108911)),
        Case(listOf(1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 0, 0, 1, 1, 0, 3, 2, 2, 2, 0, 3), Level.MILD, doubleArrayOf(0.03948471321620729, 0.6561538564225152, 0.3043614303612776)),
        Case(listOf(1, 1, 0, 1, 0, 0, 0, 1, 2, 2, 0, 0, 0, 1, 1, 3, 1, 1, 1, 1, 2), Level.MILD, doubleArrayOf(0.24392598156621162, 0.7045317908025633, 0.051542227631225096)),
        Case(listOf(1, 1, 0, 1, 1, 1, 1, 2, 2, 2, 1, 1, 1, 1, 0, 2, 2, 2, 0, 0, 3), Level.MILD, doubleArrayOf(0.040567350397660074, 0.712906858485383, 0.24652579111695694)),
        Case(listOf(1, 1, 0, 0, 0, 0, 0, 3, 3, 3, 0, 0, 1, 1, 0, 2, 0, 1, 1, 0, 1), Level.MILD, doubleArrayOf(0.23957230345240343, 0.6682205703458266, 0.09220712620176988)),
        Case(listOf(3, 1, 1, 3, 1, 1, 2, 4, 3, 4, 0, 2, 4, 1, 0, 3, 4, 3, 0, 0, 4), Level.MODERATE_OR_WORSE, doubleArrayOf(3.3217329503158873e-08, 0.0020318321884320208, 0.9979681345942384)),
        Case(listOf(1, 1, 0, 1, 1, 1, 1, 3, 2, 2, 0, 0, 2, 2, 2, 3, 2, 1, 1, 0, 3), Level.MODERATE_OR_WORSE, doubleArrayOf(0.009535034306061663, 0.46337312291483523, 0.5270918427791031)),
        Case(listOf(1, 2, 2, 3, 3, 3, 3, 4, 4, 3, 2, 1, 4, 3, 4, 4, 2, 5, 0, 1, 4), Level.MODERATE_OR_WORSE, doubleArrayOf(5.584546221884522e-09, 0.0012843220553849489, 0.9987156723600688)),
        Case(listOf(1, 1, 1, 0, 1, 0, 1, 3, 2, 2, 0, 0, 1, 2, 0, 2, 2, 2, 1, 0, 4), Level.MILD, doubleArrayOf(0.05252129872983287, 0.6047951482705904, 0.3426835529995767)),
        Case(listOf(1, 1, 0, 0, 1, 1, 1, 2, 2, 2, 0, 0, 2, 1, 1, 2, 2, 1, 1, 0, 2), Level.MILD, doubleArrayOf(0.05155178992947064, 0.7595411204383969, 0.18890708963213246)),
        Case(listOf(1, 2, 0, 1, 1, 1, 1, 1, 2, 2, 0, 0, 0, 0, 0, 3, 2, 2, 1, 0, 3), Level.MILD, doubleArrayOf(0.03494506394609405, 0.6716578117808444, 0.2933971242730617)),
        Case(listOf(2, 3, 2, 2, 2, 1, 2, 4, 4, 3, 1, 2, 3, 2, 1, 4, 3, 2, 3, 3, 4), Level.MODERATE_OR_WORSE, doubleArrayOf(1.7914044888037884e-10, 0.00024105448337489844, 0.9997589453374846)),
        Case(listOf(1, 1, 1, 1, 1, 1, 1, 3, 3, 3, 1, 1, 1, 2, 2, 4, 2, 2, 1, 1, 4), Level.MODERATE_OR_WORSE, doubleArrayOf(0.002060564314861331, 0.28057896595907533, 0.7173604697260633)),
        Case(listOf(1, 1, 3, 0, 3, 1, 0, 5, 5, 5, 0, 1, 0, 0, 0, 2, 0, 3, 0, 0, 3), Level.MODERATE_OR_WORSE, doubleArrayOf(0.02556572511876497, 0.48700409648891196, 0.48743017839232305)),
        Case(listOf(1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 1, 1, 1, 2, 1, 3, 2, 2, 2, 0, 3), Level.MODERATE_OR_WORSE, doubleArrayOf(0.007524134232599792, 0.49564477596129114, 0.4968310898061092)),
        Case(listOf(1, 1, 0, 0, 0, 0, 0, 1, 2, 1, 0, 1, 0, 0, 0, 1, 0, 2, 0, 0, 1), Level.MILD, doubleArrayOf(0.4942313781148906, 0.49656926634981, 0.009199355535299446)),
        Case(listOf(1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1), Level.OKAY, doubleArrayOf(0.4960800559228577, 0.4929783908859763, 0.010941553191166065)),
        Case(listOf(1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1), Level.OKAY, doubleArrayOf(0.4960800559228577, 0.4929783908859763, 0.010941553191166065)),
        Case(listOf(1, 1, 0, 0, 0, 0, 0, 2, 2, 2, 0, 0, 0, 0, 0, 2, 1, 2, 0, 0, 3), Level.OKAY, doubleArrayOf(0.48913697379799975, 0.48520669575941006, 0.02565633044259026)),
    )

    private fun toMap(answers: List<Int>): Map<String, Int> = LevelClassifier.featureNames.zip(answers).toMap()

    @Test
    fun levelsAndProbabilitiesMatchPython() {
        for (case in cases) {
            val answers = toMap(case.answers)
            assertEquals(case.level, LevelClassifier.predictLevel(answers))
            assertArrayEquals(case.probabilities, LevelClassifier.probabilities(answers), 1e-9)
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun missingAnswerIsRejected() {
        val answers = toMap(cases[0].answers).toMutableMap()
        answers.remove(LevelClassifier.featureNames[0])
        LevelClassifier.predictLevel(answers)
    }

    @Test(expected = IllegalArgumentException::class)
    fun answerOutsideTheTrainingRangeIsRejected() {
        val answers = toMap(cases[0].answers).toMutableMap()
        answers[LevelClassifier.featureNames[0]] = 99
        LevelClassifier.predictLevel(answers)
    }
}

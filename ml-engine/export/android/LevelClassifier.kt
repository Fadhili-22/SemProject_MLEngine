package com.example.screening

// The three levels the screening can return.
enum class Level { OKAY, MILD, MODERATE_OR_WORSE }

// Logistic regression model exported from ExportToAndroid.ipynb. Do not edit the numbers by hand.
// Every question has a weight for each level. A level's score is its intercept plus
// (weight x answer) added up over the 21 questions, and the level with the highest score wins.
object LevelClassifier {

    // question ids, in the same order the model was trained on
    val featureNames: List<String> = listOf("phq_q2", "phq_q1", "gad_q6", "gad_q2", "gad_q3", "gad_q4", "gad_q1", "pss_q2", "pss_q3", "pss_q1", "gad_q5", "gad_q7", "isi_q1", "isi_q7", "isi_q2", "pss_q14", "isi_q4", "pss_q11", "isi_q5", "isi_q3", "pss_q8")

    // lowest and highest answer seen in the training data for each question (same order as featureNames)
    private val lowestAnswers = intArrayOf(0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1)
    private val highestAnswers = intArrayOf(3, 3, 3, 3, 3, 3, 3, 5, 5, 5, 3, 3, 4, 4, 4, 5, 4, 5, 4, 4, 5)

    // one row of 21 weights for each level: okay, mild, moderate or worse
    private val weights = arrayOf(
        doubleArrayOf(-2.0107861012238213, -1.6993804585747831, -0.41052578033928555, -0.2734836931694442, -0.13819657900600268, -0.22138719634828363, -0.2549132039222899, -0.3252579193986125, -0.007904456878753238, 0.04461633806771616, -0.3966498015409053, -0.3212172580522045, -0.5432871417440781, -0.2743786917361925, -0.18278560459957505, -0.23006907224418888, -0.11875645649748835, -0.0146248790633117, -0.06895173106719679, -0.14733598272636567, -0.016413632813924623),
        doubleArrayOf(0.15979920533781028, 0.04278300849997287, 0.029302240404694882, 0.001939694139042054, 0.05464238363630714, 0.022516771362624026, 0.06718797675214352, -0.093896559684696, 0.03256337640982997, -0.05480781482991557, 0.052915321648769324, 0.10334264905979346, 0.05485450813779766, -0.030579050247391888, 0.07655175553200888, -0.018763024294263988, 0.005364519722955996, -0.019096321976220233, 0.014590997698429893, 0.04708542939893473, -0.04420918576570747),
        doubleArrayOf(1.8509868958860223, 1.6565974500748084, 0.38122353993459224, 0.2715439990304034, 0.08355419536969536, 0.19887042498565632, 0.18772522717014628, 0.4191544790833123, -0.02465891953107967, 0.01019147676219836, 0.34373447989213524, 0.2178746089924081, 0.48843263360627964, 0.30495774198358616, 0.10623384906756643, 0.24883209653845056, 0.11339193677453142, 0.0337212010395272, 0.05436073336876413, 0.10025055332742801, 0.06062281857963185),
    )

    // one intercept for each level
    private val intercepts = doubleArrayOf(5.929950494277127, 1.2099205296975897, -7.139871023974697)

    // the score of each level for one student; answers maps a question id to the answer given
    fun scores(answers: Map<String, Int>): DoubleArray {
        val x = IntArray(featureNames.size)
        for (j in featureNames.indices) {
            val name = featureNames[j]
            val value = answers[name] ?: throw IllegalArgumentException("Missing answer for $name")
            require(value in lowestAnswers[j]..highestAnswers[j]) {
                "Answer for $name must be between ${lowestAnswers[j]} and ${highestAnswers[j]}, but was $value"
            }
            x[j] = value
        }
        val result = DoubleArray(intercepts.size)
        for (k in intercepts.indices) {
            var total = intercepts[k]
            for (j in x.indices) {
                total += weights[k][j] * x[j]
            }
            result[k] = total
        }
        return result
    }

    // the score of each level turned into probabilities that add up to 1
    fun probabilities(answers: Map<String, Int>): DoubleArray {
        val s = scores(answers)
        var highest = s[0]
        for (k in 1 until s.size) {
            if (s[k] > highest) highest = s[k]
        }
        val exps = DoubleArray(s.size)
        var sum = 0.0
        for (k in s.indices) {
            exps[k] = Math.exp(s[k] - highest)
            sum += exps[k]
        }
        for (k in exps.indices) {
            exps[k] = exps[k] / sum
        }
        return exps
    }

    // the level with the highest score
    fun predictLevel(answers: Map<String, Int>): Level {
        val s = scores(answers)
        var best = 0
        for (k in 1 until s.size) {
            if (s[k] > s[best]) best = k
        }
        return Level.values()[best]
    }
}

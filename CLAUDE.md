# CLAUDE.md: MHC Screening ML Engine

This file is for an AI coding assistant (and the humans using it) working with this repository. It explains what the project is, what the machine learning part produces, and exactly how to plug it into the Android app. **If you only need to integrate the model into the app, read sections 1, 2, 5 and 6 and ignore the rest.**

---

## 1. What this project is

A university semester project (ICS 3202, Group 5) at Strathmore University. The team is building a **mobile app (Android, Kotlin) that screens university students for depression risk** and sorts them into three risk levels, so the counselling centre can reach the highest-risk students first.

**The problem it addresses.** Today, a Strathmore student books a counselling appointment and waits. How quickly they are helped depends on the length of the queue, not on how unwell they are, and no AI or screening model is used. This project adds a short questionnaire plus a machine-learning model that estimates a student's risk level.

**This repository is the ML engine only.** It contains the data preparation, the model training and comparison, and the exported model. The mobile app lives in a separate repository (the "main app") and is built by teammates. The two meet at one file: `export/android/LevelClassifier.kt`.

**This is a screening tool, not a diagnosis.** The study determines which model is best at screening. It does not claim the model is 100% accurate and it does not endorse AI as a diagnostic tool. Keep that wording in any user-facing text (see section 7).

### The three levels

The label comes from the full PHQ-9 depression questionnaire total (0 to 27):

| `Level` (Kotlin) | Class number (Python) | Meaning | PHQ-9 total | Suggested risk name | What the app should do |
|---|---|---|---|---|---|
| `OKAY` | 0 | okay | 0 to 4 | Low | Short reassuring message, optional resources |
| `MILD` | 1 | mild | 5 to 9 | Moderate | In-app resources, easy way to contact a counsellor |
| `MODERATE_OR_WORSE` | 2 | moderate or worse | 10 to 27 | High | Ask the student to see a counsellor as soon as possible, support resources for the wait; flag to the counselling centre so this student is seen first |

The proposal talks about "low, moderate and high" risk, the code says OKAY / MILD / MODERATE_OR_WORSE. They are the same three groups in the same order. Pick one set of names in the UI and keep the mapping above.

### The key idea

Students answer only **21 questions** instead of the full PHQ-9 (9 questions). The model predicts the level the full PHQ-9 *would* have given. The 21 questions are:

- PHQ-9 questions 1 and 2 (together called the PHQ-2)
- all 7 GAD-7 anxiety questions
- 6 Insomnia Severity Index (ISI) questions
- 6 Perceived Stress Scale (PSS-14) questions

PHQ-9 questions 3 to 9 and the PHQ-9 total are **never** features, because they make up the label (that would be data leakage).

---

## 2. What the model is

| Item | Value |
|---|---|
| Model | Multinomial logistic regression (scikit-learn), `class_weight='balanced'`, `C = 1` |
| Inputs | 21 integer answers, **raw, with no scaling or encoding** |
| Output | One of 3 levels, plus 3 probabilities that add up to 1 |
| Size | 66 numbers (3 levels x 21 weights, plus 3 intercepts) |
| Runs | Fully on the phone, offline, no library, no network, no Python |
| Training data | Zenodo university-student dataset, 24,292 students raw, 19,251 after cleaning |

### Why logistic regression was chosen

Four models were compared on the same held-out test set (3,851 students, 257 of them level 2): logistic regression, decision tree, random forest and XGBoost, against the simple PHQ-2 rule as baseline. The main metric is **recall for level 2**, because a missed level 2 student gets no counsellor referral.

| Model | Level 2 recall | Macro recall |
|---|---|---|
| Logistic regression (chosen) | 78.2% | 0.783 |
| Random forest | 78.6% | 0.774 |
| XGBoost | 75.9% | 0.779 |
| Decision tree | 70.4% | 0.759 |

Logistic regression, random forest and XGBoost cannot be told apart on level 2 recall with this test set (bootstrap 95% ranges overlap). Logistic regression won on practical grounds: fewest level 2 students predicted as okay (1 against 2 to 5), fewest two-level mistakes, and the simplest thing to ship to a phone (66 numbers that can be checked exactly against Python). Full reasoning is in `notebooks/ModelComparison.ipynb`, section 7.

### Known limits (be honest about these in the app and the report)

- The model flags about 9.8% of students as level 2, while only 6.7% truly are, so some students are flagged who do not need it (precision for level 2 is about 53%). Almost all missed level 2 students are predicted *mild*, not okay.
- Data is from **one campus in China** (Xinxiang Medical University, February to March 2021), collected by questionnaire. It has not been validated on Strathmore students.
- **The model never sees PHQ-9 question 9** (thoughts of self-harm). The app must ask it separately and handle the answer outside the model (see section 7).
- The export only proves that the app calculates the same numbers as Python. It does not prove the app asks the questions with the same wording and scales as the training data.

---

## 3. Repository layout

```
ML Engine/
|-- CLAUDE.md                     this file
|-- README.md                     short human overview and run instructions
|-- export/android/               ** the only folder the app needs **
|   |-- LevelClassifier.kt        the model as plain Kotlin (no library)
|   `-- LevelClassifierTest.kt    JUnit test with 30 students checked against Python
|-- models/                       saved Python models (.pkl); Python only, NOT used by the app
|-- data/
|   |-- zenodo.csv                raw merged dataset (never modified)
|   |-- zenodo_clean.csv          after cleaning (19,251 students)
|   |-- train.csv / test.csv      the 80/20 stratified split used by every model
|   `-- phq9, gad7, isi, pss, demographic .csv   the original source files
`-- notebooks/                    CRISP-DM notebooks, run in this order:
    |-- 00_merge.ipynb                          merges the source csv files into zenodo.csv
    |-- 01_data_understanding_and_cleaning.ipynb
    |-- 02_eda.ipynb                            exploration, the 3-level target, PHQ-2 baseline
    |-- 03_preprocessing.ipynb                  picks the 21 questions, makes train.csv / test.csv
    |-- LogisticRegression.ipynb                base model
    |-- DecisionTree.ipynb
    |-- RandomForest.ipynb
    |-- XGBoost.ipynb
    |-- ModelComparison.ipynb                   compares all four, makes the decision
    `-- ExportToAndroid.ipynb                   writes the two Kotlin files in export/android/
```

---

## 4. Data preparation (summary)

Details and numbers are in notebooks 01 to 03.

- Raw data: 24,292 students, 84 columns, no missing values, no duplicates.
- Cleaning removed 5,041 students (20.8%): students who scored 0 on PHQ-9, GAD-7 and ISI together (4,553, likely straight-lining), ages outside 16 to 35 (40), and students averaging under 1 second per question (448). The raw file is never changed.
- Result: 19,251 students. Levels: 11,933 okay (62.0%), 6,034 mild (31.3%), 1,284 moderate or worse (6.7%).
- Features: 30 candidate questions ranked by correlation with the PHQ-9 total, top 21 kept.
- Split: 80/20 stratified (15,400 train, 3,851 test). Class imbalance handled with balanced weights, not resampling.

---

## 5. How to integrate the model into the app

### Step 1: copy the two files

| From this repo | To the app |
|---|---|
| `export/android/LevelClassifier.kt` | `app/src/main/java/<your/package/path>/LevelClassifier.kt` |
| `export/android/LevelClassifierTest.kt` | `app/src/test/java/<your/package/path>/LevelClassifierTest.kt` |

### Step 2: set the package name

Both files start with `package com.example.screening`. Change the first line of **both** to the app's real package (it must match the folder). Do not change anything else in the files.

### Step 3: Gradle

No new dependency is needed for `LevelClassifier.kt`. The test needs JUnit 4 (new Android Studio projects already include it):

```kotlin
testImplementation("junit:junit:4.13.2")
```

### Step 4: run the test first

Run `LevelClassifierTest` (Android Studio: right-click the file, Run; or `./gradlew test`). It checks that the Kotlin code returns the **same level and the same probabilities as the Python model** for 30 students (11 okay, 12 mild, 7 moderate or worse, including the 6 students whose top two scores are closest), and that a missing or out-of-range answer is rejected. If this test fails, do not ship; something was altered or copied wrongly.

### Step 5: collect the 21 answers

The model needs one answer for each of these question ids. Build a `Map<String, Int>` from id to answer.

| Id(s) | Questionnaire item | Answer scale | Meaning of the number |
|---|---|---|---|
| `phq_q1`, `phq_q2` | PHQ-9 item 1 (little interest or pleasure) and item 2 (feeling down, depressed or hopeless) | **0 to 3** | 0 not at all, 1 several days, 2 more than half the days, 3 nearly every day |
| `gad_q1` ... `gad_q7` | GAD-7 items 1 to 7 (all seven) | **0 to 3** | same four options as above |
| `isi_q1`, `isi_q2`, `isi_q3`, `isi_q4`, `isi_q5`, `isi_q7` | Insomnia Severity Index items (note: **item 6 is not used**) | **0 to 4** | higher means worse insomnia |
| `pss_q1`, `pss_q2`, `pss_q3`, `pss_q8`, `pss_q11`, `pss_q14` | Perceived Stress Scale (PSS-14) items | **1 to 5** | higher means more stress |

The full list in the model's training order is also available in code as `LevelClassifier.featureNames`, and the app does **not** need to send answers in that order, because it looks them up by id.

**Critical: the PSS scale is 1 to 5.** The standard PSS-14 is scored 0 to 4, but this dataset codes it 1 to 5 and the model was trained on that. If the app numbers PSS answers 0 to 4, every PSS answer is one too low and predictions shift **without any error**, because the values still fall inside the allowed range. Number the five PSS options 1, 2, 3, 4, 5.

**Question wording.** The dataset contains only numbers, not question text. Use the standard wording of each instrument (PHQ-9, GAD-7, ISI, PSS-14) in the item order above, and check the ISI and PSS item numbering against the dataset's documentation at https://zenodo.org/records/10423537 before finalising the UI. All four scales are "higher number = more symptoms", which matches the data (average answers rise from level 0 to level 2). Keep the answer labels in the app consistent with these scales.

### Step 6: call the model

```kotlin
val answers = mapOf(
    "phq_q1" to 2, "phq_q2" to 1,
    "gad_q1" to 1, "gad_q2" to 0, "gad_q3" to 2, "gad_q4" to 1, "gad_q5" to 0, "gad_q6" to 1, "gad_q7" to 0,
    "isi_q1" to 1, "isi_q2" to 2, "isi_q3" to 1, "isi_q4" to 2, "isi_q5" to 1, "isi_q7" to 1,
    "pss_q1" to 3, "pss_q2" to 3, "pss_q3" to 4, "pss_q8" to 3, "pss_q11" to 2, "pss_q14" to 3
)

val level: Level = LevelClassifier.predictLevel(answers)
val probabilities: DoubleArray = LevelClassifier.probabilities(answers)   // [okay, mild, moderate or worse]

when (level) {
    Level.OKAY -> { /* short reassuring message, optional resources */ }
    Level.MILD -> { /* in-app resources with an easy way to contact a counsellor */ }
    Level.MODERATE_OR_WORSE -> { /* ask the student to see a counsellor as soon as possible; flag for the counselling centre */ }
}
```

API of `object LevelClassifier`:

| Member | Returns | Notes |
|---|---|---|
| `featureNames` | `List<String>` | the 21 ids |
| `predictLevel(answers)` | `Level` | highest-scoring level |
| `probabilities(answers)` | `DoubleArray` (3) | softmax, adds up to 1, order okay / mild / moderate or worse |
| `scores(answers)` | `DoubleArray` (3) | raw scores before softmax |

Calls throw `IllegalArgumentException` if an answer is missing or outside the allowed range. Validate in the UI so the user cannot reach the call with an incomplete questionnaire, and catch the exception as a safety net. It is a plain calculation, so it can run on the main thread.

### Step 7: wire up the app-side behaviour

- **Counsellor triage (the whole point).** Level 2 results should reach the counselling centre (for example a prioritised list or a flag in the booking queue) so high-risk students are seen first. This is app and backend work, outside this repo.
- **Privacy.** Questionnaire answers are sensitive health data. Store and transmit them securely, tell the student what is kept, and get consent. The ML side stores nothing; the model runs locally.
- **Probabilities.** Use them if you want to show a softer message, or to escalate when the level 2 probability is high even though the top level is mild. Do not show raw probabilities to students as a diagnosis.

---

## 6. Rules for anyone (human or AI) editing in this repo

1. **Never hand-edit the numbers in `LevelClassifier.kt` or the cases in `LevelClassifierTest.kt`.** They are generated by `notebooks/ExportToAndroid.ipynb`. If the model changes, re-run that notebook and replace **both** files together so weights and test cases always match.
2. **Do not change the feature order, the answer scales, or the leakage rule** (no PHQ-9 items 3 to 9, no `phq_total`) without retraining and re-exporting.
3. **Do not touch `data/zenodo.csv`** (raw data, never modified). Cleaned and split data are regenerated by notebooks 01 and 03.
4. **Keep the same split.** Every model uses `data/train.csv` and `data/test.csv`; the test set is for the final comparison only and must not be used for tuning.
5. **The `.pkl` files in `models/` are for Python only** and need a compatible scikit-learn version to load (they were saved from the notebook environment; if loading fails, use the same scikit-learn version as the notebooks, or re-run the model notebook). The app never uses them.

### Re-running the pipeline (only if you need to retrain)

Requirements: Python 3.11, pandas, numpy, matplotlib, seaborn, scikit-learn, xgboost, jupyter. Open Jupyter from the `notebooks/` folder (paths are relative, for example `../data/train.csv`) and run in order: `01`, `02`, `03`, then the four model notebooks, `ModelComparison`, and finally `ExportToAndroid`.

---

## 7. Safety and wording requirements for the app

- **Self-harm question.** The model does not use PHQ-9 question 9. The app should ask it separately, and any answer above "not at all" must trigger an immediate support or emergency pathway regardless of the model's level. This is not optional.
- **Not a diagnosis.** Present results as a screening result that suggests who may benefit from support. Do not say "you have depression". Encourage anyone worried to contact a counsellor whatever the result.
- **False alarms and misses are both possible.** About half of the students flagged level 2 may not truly be level 2, and some true level 2 students are predicted mild. Keep an easy way to reach a counsellor on the mild screen too.
- **Validation on Strathmore students has not been done yet.** The project plan includes a user evaluation (students: were they categorised well and did resources help; counsellors: did it ease their work), after the app is built.

---

## 8. Quick reference for the integrator

- Copy two files, set the package name, run the test, then call `LevelClassifier.predictLevel(answers)`.
- 21 answers in a `Map<String, Int>`: PHQ and GAD 0 to 3, ISI 0 to 4, **PSS 1 to 5**.
- Three outputs: `OKAY` (low), `MILD` (moderate), `MODERATE_OR_WORSE` (high, counsellor first).
- Ask PHQ-9 question 9 separately; the model does not see it.
- Never edit the generated numbers by hand; re-run `ExportToAndroid.ipynb` instead.

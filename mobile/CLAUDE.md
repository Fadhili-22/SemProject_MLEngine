# MindCheck (Android app)

Mobile development unit project (ICS 3202, Group 5, Strathmore University). An Android app that screens university students for depression risk with 21 short questions (mood, anxiety, sleep, stress), sorts them into three risk levels with an on-device ML model, and points them to support. The point of the project: the counselling centre can see high-risk students first instead of first-come-first-served.

It sits inside the group repo `SemProject_MLEngine`. The ML engine (owned by teammates) is in `../ml-engine/`.

## How we work
- **Guided build.** The student is building the app step by step to learn it. Do one step at a time, explain what each part does and which lecture it comes from, and keep code simple enough for a student to explain in class. Wait for the student to run the app and confirm before moving to the next step.
- **Do not edit the ML engine** (everything in `../ml-engine/`). The app only copies the two files from `../ml-engine/export/android/` (step 6).
- A finished reference version of the app (built before the ML export existed) is in `D:\MindCheck_reference`. Use it as a guide for code and wording, but build it up step by step here. Its `Phq2Screener` placeholder is replaced by the real model in this version.
- Work on the local `integration` branch. Do not commit or push unless asked.

## Follow the course (lectures in `../docs/mobile-dev-notes/`)
- Kotlin + **XML Views**. Do not use Jetpack Compose (not taught).
- `findViewById` (no view binding or data binding).
- Navigation Component: one `MainActivity` + Fragments, `res/navigation/nav_graph.xml`, `NavHostFragment`.
- Layouts: LinearLayout / ConstraintLayout in XML; adaptive layouts with `SlidingPaneLayout` and `layout-sw600dp/`.
- Show OOP from the course where natural: data classes, encapsulation, polymorphism.
- Text in `res/values/strings.xml`.
- Retrofit (lecture 8) is not needed for the model: it runs on the phone, offline.

## Plan
| Step | Build | Lecture | Status |
|---|---|---|---|
| 1 | Fresh project, Hello World on the emulator | 4 | Done |
| 2 | Welcome screen in `activity_main.xml` (LinearLayout in a ScrollView, strings in strings.xml, Start + Help buttons) | 5 | Done |
| 3 | Navigation Component: `WelcomeFragment`, `CheckInFragment`, `ResultFragment`, `HelpFragment`; `nav_graph.xml`; centred toolbar title | 6 | Built |
| 4 | `Question` / `AnswerOption` data classes + `QuestionBank` (21 model questions + PHQ-9 Q9 safety question, 22 in total) | 2-3 | Built |
| 5 | Check-in screen: one question per page, answer cards (`item_option.xml`, MaterialCardView + RadioButton) built in code, Back/Next, progress bar, shared `ScreeningViewModel` | 5-6 | Built |
| 6 | ML model copied from `../ml-engine/export/android/` (only the package line changed); `LevelClassifierTest` passes, plus `QuestionBankTest` | - | Built, tests pass |
| 7 | Result screen: `LevelClassifier.predictLevel`, `RiskLevel` sealed class (Low / Moderate / High), score per area, crisis card when Q9 > 0 | OOP | Built |
| 8 | Help screen: list + detail with `SlidingPaneLayout`, Call button with an `ACTION_DIAL` Intent | 6-7 | Built |
| 9 | Deep purple + white theme (`values/colors.xml`, `values-night/colors.xml`, `themes.xml`), Poppins font (`res/font/`, styles in `values/type.xml`), gradient Welcome banner, purple app icon background | - | Built |

All steps compile and unit tests pass. Still to do: test every screen on the emulator (light and dark mode, rotation, tablet for the two-pane Help screen), and check the help phone numbers.

Update the Status column as steps are finished.

## The ML model (from the ML team's integration guide)
The full guide is kept locally (not in Git) at `../docs/ml-integration-guide.md`, if present.
- Multinomial logistic regression, 21 integer answers in, one of 3 levels out (plus 3 probabilities). Runs on the phone, no library, no network.
- Files: `../ml-engine/export/android/LevelClassifier.kt` → `app/src/main/java/com/example/mindcheck/screening/LevelClassifier.kt`, and `../ml-engine/export/android/LevelClassifierTest.kt` → `app/src/test/java/com/example/mindcheck/screening/LevelClassifierTest.kt`.
- Both files start with `package com.example.screening`. Change that line in both to `package com.example.mindcheck.screening`. **Change nothing else**: never hand-edit the weights or test cases. If the model changes, the ML team re-exports and both files are replaced together.
- Run `LevelClassifierTest` before using the model (right-click → Run, or `./gradlew test`). If it fails, the file was copied wrongly.
- API: `LevelClassifier.predictLevel(answers: Map<String, Int>): Level` (`OKAY`, `MILD`, `MODERATE_OR_WORSE`), `LevelClassifier.probabilities(answers): DoubleArray` (okay, mild, moderate or worse). Throws `IllegalArgumentException` if an answer is missing or out of range; the UI must only allow the call when all 21 are answered, and catch the exception as a safety net. It is quick enough to run on the main thread.

| `Level` | Show the student as | What the app does |
|---|---|---|
| `OKAY` | Low | Short reassuring message, optional resources |
| `MILD` | Moderate | In-app resources and an easy way to contact a counsellor |
| `MODERATE_OR_WORSE` | High | Ask them to see a counsellor as soon as possible, with support resources while they wait |

## Rules that must not change
- **Question ids and answer values must match the model exactly**: `phq_q1`, `phq_q2` (0-3); `gad_q1`-`gad_q7` (0-3); `isi_q1`-`isi_q5`, `isi_q7` (0-4, no `isi_q6`); `pss_q1`, `pss_q2`, `pss_q3`, `pss_q8`, `pss_q11`, `pss_q14` (**1-5, not 0-4**: a 0-4 PSS scale gives wrong results with no error). Copy them from `D:\MindCheck_reference\app\src\main\java\com\example\mindcheck\data\QuestionBank.kt`.
- **Self-harm question (required).** The model does not use PHQ-9 question 9 ("Thoughts that you would be better off dead, or of hurting yourself in some way", 0-3). Ask it separately and do not pass it to the model. Any answer above 0 ("Not at all") must show the crisis support screen straight away (emergency 999 / 112, Kenya Red Cross 1199, Befrienders Kenya, student counselling), whatever level the model gives.
- **Wording.** It is a screening, not a diagnosis. Never say "you have depression". Every result, including Low, keeps an easy way to reach a counsellor. Results come with a short note that the screening can be wrong both ways.
- `activity_main.xml` root must keep `android:id="@+id/main"` while `MainActivity` uses it for window insets (until step 3 moves the content into fragments).
- Help phone numbers must be checked before any demo.

## Build and run
- Android Studio: pick the **Pixel 8** emulator (not other people's phones on the Wi-Fi), press Run. The PC has little free RAM: keep the emulator running between runs.
- Command line: `./gradlew assembleDebug` / `./gradlew test` with `JAVA_HOME` set to Android Studio's `jbr` folder.

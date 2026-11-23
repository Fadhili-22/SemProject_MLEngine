# MHC Screening: ML Engine + MindCheck App

ICS 3202 semester project (Group 5), Strathmore University. A mobile app that screens university students for depression risk with 21 short questions and sorts them into three risk levels, so the counselling centre can reach the highest-risk students first.

This is a screening tool, not a diagnosis.

## The two parts

| Folder | What it is | Open with |
|---|---|---|
| [`ml-engine/`](ml-engine/) | Data cleaning, model training and comparison (logistic regression, decision tree, random forest, XGBoost), and the chosen model exported to Kotlin | Jupyter (see [ml-engine/README.md](ml-engine/README.md)) |
| [`mobile/`](mobile/) | MindCheck, the Android app (Kotlin, XML layouts, Navigation Component) | Android Studio: open the `mobile/` folder (see [mobile/CLAUDE.md](mobile/CLAUDE.md)) |
| [`docs/`](docs/) | Reference material | |

## How they connect

The ML engine exports the chosen model as plain Kotlin: `ml-engine/export/android/LevelClassifier.kt` and its test. The app copies those two files and runs the model on the phone, offline, with no server. When the model is retrained, the ML team re-runs `ExportToAndroid.ipynb` and the two files are copied into the app again.

```
ml-engine/notebooks/ExportToAndroid.ipynb
        │ writes
        ▼
ml-engine/export/android/LevelClassifier.kt  ──copy──▶  mobile/app/src/main/java/.../LevelClassifier.kt
```

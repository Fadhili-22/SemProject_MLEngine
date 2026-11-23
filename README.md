# MHC Screening Model

ICS 3202 semester project (Group 5): the machine learning engine for a mobile app that screens university students for depression.

The project uses the Zenodo university-student dataset (PHQ-9, GAD-7, ISI and PSS-14 questionnaires). A short questionnaire of 21 questions is used to predict one of three levels, which are worked out from the full PHQ-9 total:

| Level | Meaning | PHQ-9 total |
|---|---|---|
| 0 | okay | 0 to 4 |
| 1 | mild | 5 to 9 |
| 2 | moderate or worse | 10 to 27 |

The features are PHQ-9 questions 1 and 2 (the PHQ-2) plus selected GAD-7, ISI and PSS questions. PHQ-9 questions 3 to 9 and the PHQ-9 total are never used as features, because they make up the label. Four models (logistic regression, decision tree, random forest and XGBoost) were compared with the PHQ-2 rule, the simple two-question screening baseline, on the same data and with the same metrics. **Logistic regression was chosen for the app** and exported to Kotlin so it runs fully on the phone.

**Integrating with the Android app? Read [CLAUDE.md](CLAUDE.md)** (step-by-step, with the answer scales the app must use) and copy the two files in `export/android/`.

## Folder structure

```
ML Engine/           
|-- README.md
|-- export/android/
|   |-- LevelClassifier.kt        the chosen model as plain Kotlin (no library)
|   `-- LevelClassifierTest.kt    JUnit test: 30 students checked against Python
|-- models/                       saved Python models (.pkl), not used by the app
|-- data/
|   |-- zenodo.csv                raw merged dataset (24,292 students, never modified)
|   |-- zenodo_clean.csv          created by notebook 01 (19,251 students)
|   |-- train.csv                 created by notebook 03
|   |-- test.csv                  created by notebook 03
|   `-- phq9, gad7, isi, pss, demographic .csv   original source files
`-- notebooks/
    |-- 00_merge.ipynb
    |-- 01_data_understanding_and_cleaning.ipynb
    |-- 02_eda.ipynb
    |-- 03_preprocessing.ipynb
    |-- LogisticRegression.ipynb
    |-- DecisionTree.ipynb
    |-- RandomForest.ipynb
    |-- XGBoost.ipynb
    |-- ModelComparison.ipynb
    `-- ExportToAndroid.ipynb
```

## Notebook order (CRISP-DM)

Run the notebooks from top to bottom, in this order. Each notebook needs the files created by the one before it.

1. `00_merge.ipynb` (Data understanding): merges the source csv files into `data/zenodo.csv`.
2. `01_data_understanding_and_cleaning.ipynb` (Data understanding + Data preparation): data quality checks and step-by-step cleaning of the raw data. Reads `data/zenodo.csv` and writes `data/zenodo_clean.csv`.
3. `02_eda.ipynb` (Data understanding): the 3-level target, exploratory analysis with written observations, and the PHQ-2 baseline. Reads `data/zenodo_clean.csv`.
4. `03_preprocessing.ipynb` (Data preparation): feature selection (21 questions) and the stratified 80/20 train/test split. Writes `data/train.csv` and `data/test.csv`.
5. `LogisticRegression.ipynb`, `DecisionTree.ipynb`, `RandomForest.ipynb`, `XGBoost.ipynb` (Modelling + Evaluation): each model with stratified 5-fold cross-validation and tuning on macro recall. Each reads `data/train.csv` and `data/test.csv` and saves its final model to `models/`.
6. `ModelComparison.ipynb` (Evaluation): compares the four saved models on the test set, with bootstrap ranges and a decision on the model for the app.
7. `ExportToAndroid.ipynb` (Deployment): turns the chosen logistic regression model into `export/android/LevelClassifier.kt` and a matching test file.

Paths inside the notebooks are relative to the `notebooks/` folder (for example `../data/train.csv`), so open the notebooks from that folder.

## How to run

1. Install the requirements below.
2. Open Jupyter from the `notebooks/` folder and choose any Python kernel that has the packages installed.
3. Run the notebooks in the order above with "Run All". The files in `data/`, `models/` and `export/android/` are recreated by the notebooks.

To run everything from the command line, from the `notebooks/` folder:

```
jupyter nbconvert --to notebook --execute --inplace 01_data_understanding_and_cleaning.ipynb
jupyter nbconvert --to notebook --execute --inplace 02_eda.ipynb
jupyter nbconvert --to notebook --execute --inplace 03_preprocessing.ipynb
jupyter nbconvert --to notebook --execute --inplace LogisticRegression.ipynb
jupyter nbconvert --to notebook --execute --inplace DecisionTree.ipynb
jupyter nbconvert --to notebook --execute --inplace RandomForest.ipynb
jupyter nbconvert --to notebook --execute --inplace XGBoost.ipynb
jupyter nbconvert --to notebook --execute --inplace ModelComparison.ipynb
jupyter nbconvert --to notebook --execute --inplace ExportToAndroid.ipynb
```

## Requirements

- Python 3.11
- pandas, numpy, matplotlib, seaborn, scikit-learn, xgboost, joblib
- jupyter (to run the notebooks)

Install with `pip install pandas numpy matplotlib seaborn scikit-learn jupyter xgboost joblib`.

The Android app needs none of this: it only needs the two files in `export/android/`.

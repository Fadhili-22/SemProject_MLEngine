# MHC Screening Model

ICS 3202 semester project (Group 5): the machine learning engine for a mobile app that screens university students for depression.

The project uses the Zenodo university-student dataset (PHQ-9, GAD-7, ISI and PSS-14 questionnaires). A short questionnaire of 21 questions is used to predict one of three levels, which are worked out from the full PHQ-9 total:

| Level | Meaning | PHQ-9 total |
|---|---|---|
| 0 | okay | 0 to 4 |
| 1 | mild | 5 to 9 |
| 2 | moderate or worse | 10 to 27 |

The features are PHQ-9 questions 1 and 2 (the PHQ-2) plus selected GAD-7, ISI and PSS questions. PHQ-9 questions 3 to 9 and the PHQ-9 total are never used as features, because they make up the label. The base model is logistic regression. It is compared with the PHQ-2 rule, the simple two-question screening baseline. Random Forest and XGBoost will be added later as comparison models, on the same data and with the same metrics.

## Folder structure

```
ML Engine/
|-- data/
|   |-- zenodo.csv            raw merged dataset (24,292 students, never modified)
|   |-- zenodo_clean.csv      created by notebook 01 (19,251 students)
|   |-- train.csv             created by notebook 03
|   `-- test.csv              created by notebook 03
|-- notebooks/
|   |-- 01_data_understanding_and_cleaning.ipynb
|   |-- 02_eda.ipynb
|   |-- 03_preprocessing.ipynb
|   `-- LogisticRegression_Base.ipynb
`-- README.md
```

## Notebook order (CRISP-DM)

Run the notebooks from top to bottom, in this order. Each notebook needs the files created by the one before it.

1. `01_data_understanding_and_cleaning.ipynb` (Data understanding + Data preparation): data quality checks and step-by-step cleaning of the raw data. Reads `data/zenodo.csv` and writes `data/zenodo_clean.csv`.
2. `02_eda.ipynb` (Data understanding): the 3-level target, exploratory analysis with written observations, and the PHQ-2 baseline. Reads `data/zenodo_clean.csv`.
3. `03_preprocessing.ipynb` (Data preparation): feature selection (21 questions) and the stratified 80/20 train/test split. Writes `data/train.csv` and `data/test.csv`.
4. `LogisticRegression_Base.ipynb` (Modelling + Evaluation): the base model, stratified 5-fold cross-validation, test-set evaluation (per-class recall, macro F1, confusion matrix), comparison with the PHQ-2 rule, and the plan for further validation. Reads `data/train.csv` and `data/test.csv`.

Paths inside the notebooks are relative to the `notebooks/` folder (for example `../data/train.csv`), so open the notebooks from that folder.

## How to run

1. Install the requirements below.
2. Open Jupyter from the `notebooks/` folder and choose any Python kernel that has the packages installed.
3. Run `01`, `02`, `03` and then `LogisticRegression_Base` with "Run All". The files in `data/` are recreated by notebooks 01 and 03.

To run everything from the command line, from the `notebooks/` folder:

```
jupyter nbconvert --to notebook --execute --inplace 01_data_understanding_and_cleaning.ipynb
jupyter nbconvert --to notebook --execute --inplace 02_eda.ipynb
jupyter nbconvert --to notebook --execute --inplace 03_preprocessing.ipynb
jupyter nbconvert --to notebook --execute --inplace LogisticRegression_Base.ipynb
```

## Requirements

- Python 3.11
- pandas, numpy, matplotlib, seaborn, scikit-learn
- jupyter (to run the notebooks)
- xgboost (for the later comparison model)

Install with `pip install pandas numpy matplotlib seaborn scikit-learn jupyter xgboost`.

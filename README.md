# MHC Screening Model

Early depression screening for university students. The project uses the Zenodo university-student dataset (PHQ-9, GAD-7, ISI and PSS-14 questionnaires, 19,251 students) to predict moderate or worse depression (PHQ-9 total of 10 or more) from a short questionnaire that does not include PHQ-9 questions 3 to 9. Logistic regression, decision tree, random forest and XGBoost models are compared against the PHQ-2 rule, the simple two-question screening baseline.

## Folder structure

```
ML Engine/
|-- data/
|   |-- zenodo_clean.csv      cleaned dataset (input to notebooks 01 and 02)
|   |-- train.csv             created by notebook 02
|   `-- test.csv              created by notebook 02
|-- notebooks/
|   |-- 01_data_understanding_and_eda.ipynb
|   |-- 02_preprocessing.ipynb
|   `-- 03_logistic_regression.ipynb
`-- README.md
```

The notebooks for the decision tree, random forest, XGBoost and the final model comparison will be added later.

## Running order

Run the notebooks from top to bottom, in this order. Each one runs on its own, but notebook 03 needs the files that notebook 02 creates.

1. `01_data_understanding_and_eda.ipynb`: data quality checks, target definition and exploratory analysis. Reads `data/zenodo_clean.csv`.
2. `02_preprocessing.ipynb`: feature selection (21 questions) and the stratified 80/20 train/test split. Writes `data/train.csv` and `data/test.csv`.
3. `03_logistic_regression.ipynb`: PHQ-2 baseline, logistic regression, class weighting, threshold tuning and coefficients. Reads `data/train.csv` and `data/test.csv`.

Paths inside the notebooks are relative to the `notebooks/` folder (for example `../data/train.csv`), so open the notebooks from that folder.

## Requirements

- Python 3.11
- pandas, numpy, matplotlib, seaborn, scikit-learn
- jupyter (to run the notebooks)

Install with `pip install pandas numpy matplotlib seaborn scikit-learn jupyter`. The notebooks were saved with a kernel called `MHC Screening`; if it is not available on your machine, choose any Python kernel that has the packages above.


import sys
import pandas as pd
import numpy as np
from lightgbm import LGBMClassifier

n = int(sys.argv[1])

yesno_columns = ['Partner', 'Dependents', 'PhoneService', 
                 'MultipleLines', 'OnlineSecurity', 'OnlineBackup', 
                 'DeviceProtection', 'TechSupport', 'StreamingTV', 
                 'StreamingMovies', 'PaperlessBilling', 'Churn']

def acc(actual, expected):
    return np.count_nonzero(actual == expected) / len(actual)

df = pd.read_csv('WA_Fn-UseC_-Telco-Customer-Churn.csv')

for c in yesno_columns:
    df[c] = df[c].apply(lambda x: 1 if x == 'Yes' else 0)

df['TotalCharges'] = df['TotalCharges'].replace({' ': 0}).apply(float)

data = pd.get_dummies(df[df.columns.difference(['Churn', 'customerID'])])
label = df['Churn']

model = LGBMClassifier()

m = model.fit(data[:n], label[:n])

prd = m.predict(data[n:])

print(f'accuracy : {acc(prd, label[n:])}')

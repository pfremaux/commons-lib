import numpy as np
import pandas as pd
import tensorflow as tf
from sklearn.compose import ColumnTransformer
from sklearn.preprocessing import OneHotEncoder


dataset = pd.read_csv('fichier.csv')
x = dataset.iloc[:,  3:-1].values
y = dataset.iloc[:, -1].valuesct

# [1] = column a traiter
ct = ColumnTransformer(transformers=[('encoder', OneHotEncoder(), [1])], remainder='passthrough')
x = np.array(ct.fit_transform(x))


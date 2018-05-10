#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#Calling format: python LoadPrediction.py <inputfile> <prediction file> <actual file>
"""
Created on Tue Mar 27 14:14:40 2018

@author: Bansal
"""
import sys
import pandas as pd
import numpy as np
from statsmodels.tsa.arima_model import ARIMA
from sklearn.metrics import mean_squared_error
from math import sqrt

#Input details of data center location
if(len(sys.argv) < 2):
    load_input = 'Interpolated_data_file.csv'
else:
    load_input = sys.argv[1]
if(len(sys.argv) < 3):
    load_pred_file = 'load_pred_file.txt'
else:
    load_pred_file = sys.argv[2]
if(len(sys.argv) < 4):
    load_act_file = 'load_act_file.txt'
else:
    load_act_file = sys.argv[3]


data=pd.read_csv(load_input)


data_matrix=data.as_matrix()
X=data_matrix[:,1]
Total_size=len(X)
size = Total_size - 1440  

train, test = X[0:size], X[size:Total_size]
history = [x for x in train]
predictions = list()

slot=1440

def difference(dataset, interval=slot):
	diff = list()
	for i in range(interval, len(dataset)):
		value = dataset[i] - dataset[i - slot]
		diff.append(value)
	return np.array(diff)

def inverse_difference(history, yhat, interval=slot):
	return yhat + history[-interval]


differenced=difference(X)


t=0
while t<len(test):
    model = ARIMA(differenced, order=(5,1,0))
    model_fit = model.fit(disp=0)
    forecast = model_fit.forecast(steps=slot)[0]
#    for cnt in range(0,15):
    min=1
    for yhat in forecast:
        inverted = inverse_difference(history, yhat)
        # print("test t={} cnt={} ".format(t,min))
#        output = model_fit.forecast()
#        yhat=output[0]
        predictions.append(inverted)
        obs=test[t]
#        history.append(obs)
        history.append(obs)
        # print("predicted={}, expected={}".format(inverted,obs))
        min+=1
        t+=1
        if(t==len(test)):
            break

def load_on_text_file(predictions):
    f= open(load_pred_file,"w")
    size=len(predictions)
    for i in range(1,size+1):
        f.write("%0.3f " % predictions[i-1])
        if(i%60==0):
            f.write("\n")
    f.close() 
load_on_text_file(predictions)


def actual_load_file(test):
    f= open(load_act_file,"w")
    size=len(test)
    for i in range(1,size+1):
        f.write("%0.3f " % test[i-1])
        if(i%60==0):
            f.write("\n")
    f.close() 
#    np.savetxt(load_act_file, test, delimiter=" ")
actual_load_file(test)

error = mean_squared_error(test, predictions)
mse = sqrt(error)
print('Test MSE: %.3f, RMSE: %.3f' % (error,mse))
print()
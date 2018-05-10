#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#Calling format: python PlotResults.py <csvfile1> <csvfile2>
"""
Created on Sat Apr 21 14:14:40 2018

@author: Sumitesh
"""
import pandas as pd
import numpy as np 
from matplotlib import pyplot
import sys

if(len(sys.argv) < 2):
    csvfile1 = 'RenPercents.csv'
else:
    csvfile1 = sys.argv[1]
if(len(sys.argv) < 3):
    csvfile2 = 'RenPercents_nosched.csv'
else:
    csvfile2 = sys.argv[2]
if(len(sys.argv) < 4):
    outfilePath = "./z_plots/RenPercent_fig_"
else:
    outfilePath = sys.argv[3]

def plotResults(csv1, csv2, outfile):
    input1=pd.read_csv(csv1)
    input2=pd.read_csv(csv2)
    x1=input1.as_matrix()
    x2=input2.as_matrix()
    numOfDays = len(input1)
    y=np.arange(1,numOfDays+1,1)

    for ren_iter in range(0,len(input1.keys())):
        pyplot.clf()
        pyplot.plot(y,x1[:,[ren_iter]].reshape(-1),'r',label='Load Dist. based on Predicted Ren. Energy')
        pyplot.plot(y,x2[:,[ren_iter]].reshape(-1),'g',label='Randomised Load Distribution')
        pyplot.xlabel('Days',fontsize=14)
        pyplot.ylabel('Renpercent',fontsize=14)
        pyplot.title('Renpercent for '+input1.keys()[ren_iter],fontsize=17)
        pyplot.legend()
        pyplot.savefig(outfile+input1.keys()[ren_iter]+".png")
#         pyplot.show()


plotResults(csvfile1, csvfile2, outfilePath)

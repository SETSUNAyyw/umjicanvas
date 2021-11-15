import numpy as np
import pandas as pd
import os
import datetime
from matplotlib import pyplot as plt

def contributionPlot(date, activity_observed):
	data_path = "/tmp/.umjicanvas/"
	png_path = data_path + "temp.png"
	activity = activity_observed
	if not (os.path.exists(data_path)):
		os.makedirs(data_path)

	if (len(activity) < 366):
		for i in range(366 - len(activity)):
			activity = np.insert(activity, 0, 0)
			# print("insert")
	elif (len(activity) > 366):
		activity = activity[-366:]
	for i in range(5):
		activity = np.append(activity, 0)

	x0 = np.zeros(7, dtype = int)
	y0 = np.arange(7)[::-1]
	x = x0
	y = y0
	for i in range(52):
		x = np.append(x, x0 + i + 1)
		y = np.append(y, y0)
	# print(x)
	# print(y)
	c = activity
	plt.figure(figsize = (30, 5))
	plt.scatter(x = x, y = y, c = c, s = 700, marker = ",", cmap = "Blues")
	# plt.text(-10, 6.5, "June", fontsize = 20)
	# plt.colorbar()
	plt.axis("off")
	plt.savefig(png_path)
	os.system("gthumb " + png_path)
	os.system("rm " + png_path)
import numpy as np
import pandas as pd
import os
import datetime
from matplotlib import pyplot as plt

def contributionPlot(date, activity_observed, by = "month"):
	data_path = "/tmp/.umjicanvas/"
	png_path = data_path + "temp.png"
	activity = activity_observed
	if not (os.path.exists(data_path)):
		os.makedirs(data_path)

	if (by == "month"):
		row = 5
		days = 31
		fig_len = 3
	elif (by == "year"):
		row = 53
		days = 366
		fig_len = 30
	if (len(activity) < days):
		for i in range(days - len(activity)):
			activity = np.insert(activity, 0, 0)
			# print("insert")
	elif (len(activity) > days):
		activity = activity[-days:]
	for i in range(row * 7 - days):
		activity = np.append(activity, 0)

	x0 = np.zeros(7, dtype = int)
	y0 = np.arange(7)[::-1]
	x = x0
	y = y0
	for i in range(row - 1):
		x = np.append(x, x0 + i + 1)
		y = np.append(y, y0)
	# print(x)
	# print(y)
	c = activity#np.random.randint(100, size = 35)#
	plt.figure(figsize = (fig_len, 4))
	plt.style.use('dark_background')
	plt.scatter(x = x, y = y, c = c, s = 700, marker = ",", cmap = "bone")
	# plt.text(-10, 6.5, "June", fontsize = 20)
	# plt.colorbar()
	plt.axis("off")
	plt.savefig(png_path)
	os.system("gthumb " + png_path)
	os.system("rm " + png_path)
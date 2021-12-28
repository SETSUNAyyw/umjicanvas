import pandas as pd
import numpy as np
import argparse
import os
import sys
import warnings
warnings.filterwarnings("ignore")
import contribution
import csv

def readUsers():
	course_raw_data = "data/temp.txt"
	with open(course_raw_data, "r") as f:
		li = f.readlines()
	for i in range(len(li)):
		li[i] = li[i].replace("null", "None")
		li[i] = li[i].replace("false", "False")
		li[i] = li[i].replace("true", "True")
		li[i] = eval(li[i])
	df = pd.DataFrame(li[0][0])
	for i in range(len(li)):
		for j in range(len(li[i])):
			if ((i == 0) & (j == 0)):
				continue
			df = df.append(li[i][j], ignore_index = True)
	df["name"] = df["name"].str.replace(",", "")
	df["sortable_name"] = df["sortable_name"].str.replace(",", "")
	df["sortable_name"] = df["sortable_name"].str.replace("[^a-zA-Z|\\s]", "")
	df["short_name"] = df["short_name"].str.replace(",", "")
	df["short_name"] = df["short_name"].str.replace("[^a-zA-Z]|\\s", "")
	return df

def extractEnroll(row, string):
	row = row["enrollments"]
	if (isinstance(row, list)):
		row = row[0]
	try:
		row[string]
	except KeyError:
		return None
	return (row[string])

def main():

	# Parse arguments
	parser = argparse.ArgumentParser(prog = "canvas")
	parser.add_argument("-p", "--plot", help = "Plot my contribution.", nargs = "?", const = "./")
	parser.add_argument("-d", "--directory", help = "Specify a running directory.", nargs = "?", const = "")
	args = parser.parse_args()

	if (args.directory):
		png_save_path = args.directory + "img/"
		data_path = args.directory + "data/"
	else:
		png_save_path = "img/"
		data_path = "data/"

	if (args.plot):
		with open("data/temp.txt", "r") as f:
			delta = f.readline();
		delta = eval(delta);
		
		contribution.contributionPlot(delta, by = "month", save = png_save_path)
		# print(delta)
		sys.exit(0)

	df = readUsers()#.to_csv(course_csv)
	df = df.rename(columns = {"id": "student_id"})
	enrollment_keys = list(df["enrollments"][1][0].keys())
	for key in enrollment_keys:
		df[key] = df.apply(lambda row: extractEnroll(row, key), axis = 1)
	df = df.drop("enrollments", axis = 1)
	df = df.rename(columns = {"id": "enrollment_id"})
	df.to_csv("data/tmp.csv", index = False, quoting = csv.QUOTE_NONNUMERIC)


if __name__ == "__main__":
    main()

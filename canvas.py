import pandas as pd
import numpy as np
import json
import argparse
import datetime
import os
import sys
import warnings
from oauthlib.oauth2.rfc6749 import tokens
from oauthlib.oauth2 import Server

def warn(*args, **kwargs):
	pass
warnings.warn = warn

def readName():
	names = pd.DataFrame()
	with open("./VE370.txt", "r") as f:
		for item in f:
			if ((item == "\n") | (item[0] == "#")):
				continue
			row = json.loads(item)
			df = pd.DataFrame(row, index = [0])
			names = names.append(df, ignore_index = True)
	print(names)


def readCourse():
	courses = pd.DataFrame()
	with open("./courses.txt", "r") as f:
		for item in f:
			if ((item == "\n") | (item[0] == "#")):
				continue
			row = json.loads(item)
			df = pd.DataFrame(row, index = [0])
			courses = pd.concat([courses, df], ignore_index = True)
	print(courses)

def validate_client_id(self, client_id, request):
    request.claims = {
        'aud': self.client_id
    }
    return True

def decode():
	private_pem_key = 12345
	validator = 12345

	server = Server(
	  validator,
	  token_generator = tokens.signed_token_generator(private_pem_key, issuer="foobar")
	)
	print(server)

def readUsers(course_name):
	course_raw_data = "./" + course_name + ".txt"
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
	# readName()
	# readCourse()
	# decode()

	# Parse arguments
	parser = argparse.ArgumentParser(prog = "canvas")
	parser.add_argument("course", help = "Input course name that you want to operate on.", type = str)
	parser.add_argument("-m", "--mine", help = "Check my activity.", action = "store_true")
	parser.add_argument("-s", "--summary", help = "Summarize today's activity.", type = int)
	parser.add_argument("-r", "--rank", help = "Rank user daily activity in the course.", action = "store_true")
	args = parser.parse_args()

	data_path = "./data/"
	if not (os.path.exists(data_path)):
		os.makedirs(data_path)
	if not (os.path.exists(data_path + datetime.date.today().isoformat() + "/")):
		os.makedirs(data_path + datetime.date.today().isoformat() + "/")
	course_csv = "./data/" + datetime.date.today().isoformat() + "/" + args.course + ".csv"
	activity_csv = "./data/my_activity.csv"

	if (args.summary):
		with open(activity_csv, "a") as f:
			f.write("{},{}\n".format(datetime.date.today().isoformat(), args.summary))
		sys.exit(0)

	if (args.rank):
		yesterday_csv = "./data/" + (datetime.date.today() - datetime.timedelta(1)).isoformat() + "/" + args.course + ".csv"
		rank_csv = "./data/" + (datetime.date.today() - datetime.timedelta(1)).isoformat() + "/" + args.course + "_rank.csv"
		delta = []
		df0 = pd.read_csv(yesterday_csv, index_col = 0)
		df1 = pd.read_csv(course_csv, index_col = 0)
		for i in range(len(df0)):
			row = df0.iloc[i]
			# print(row["total_activity_time"])
			activity_yesterday = row["total_activity_time"]
			activity_today = df1[df1["student_id"] == row["student_id"]]["total_activity_time"].values[0]
			delta_activity = activity_today - activity_yesterday
			delta.append(delta_activity)
		dfN = df0[["student_id", "name"]]
		dfN["day_activity"] = delta
		dfNsorted = dfN.groupby(["student_id", "name"]).agg(sum).sort_values("day_activity", ascending = False).head(15)
		dfNsorted.to_csv(rank_csv)
		print("-"*50 + "\nToday's 15 best contributors of " + args.course + "!\n" + "-"*50 + "\n")
		print(dfNsorted)
		sys.exit(0)

	if (args.mine):
		df = pd.read_csv(course_csv, index_col = 0)
		sys.stdout.write("{}".format(df[df["student_id"] == 4416]["total_activity_time"].values[0]))
		sys.exit(0)
	# print("no")
	# print(args.summary)
	# Write course data into csv
	df = readUsers(args.course)#.to_csv(course_csv)
	df = df.rename(columns = {"id": "student_id"})
	enrollment_keys = list(df["enrollments"][1][0].keys())
	for key in enrollment_keys:
		df[key] = df.apply(lambda row: extractEnroll(row, key), axis = 1)
	df = df.drop("enrollments", axis = 1)
	df = df.rename(columns = {"id": "enrollment_id"})
	df.to_csv(course_csv)
	# dfN = df.sort_values("total_activity_time", ascending = False)
	# dfN = dfN[["student_id", "name", "total_activity_time"]]
	# print(dfN.head(20))
	# print(args.course)


if __name__ == "__main__":
    main()

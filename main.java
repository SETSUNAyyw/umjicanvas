// package src;

import java.io.*;
import src.canvas;
// import java.net.URL;
// import java.net.HttpURLConnection;


public class main {
	public static void main(String[] args) {
		canvas c = new canvas();
		// String courses = c.getCourse();
		// canvas.runPython();

		canvas.readToken();
		canvas.getID();
		canvas.fetchCourseList();
		canvas.fetchCourses();

		// java.time.LocalDate date1 = java.time.LocalDate.now();
		// java.time.format.DateTimeFormatter dateTimeFormatter = java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd");
		// System.out.println(date1.format(dateTimeFormatter));
		// java.time.LocalDate date2 = java.time.LocalDate.of(2021,12,27);
		// // System.out.println(date);
		// // System.out.println(java.time.temporal.ChronoUnit.DAYS.between(date1,date2));
		// System.out.println(date1.minusDays(10));
		// canvas.updateActivity();

		// System.out.println(java.time.LocalDate.now());
		// canvas.createInfoDatabase();

		// System.out.println(System.getProperty("user.dir"));
	}
}
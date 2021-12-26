package src;

import java.io.*;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.regex.*;


public class canvas {
	private static String[] courses;
	private static String token;
	private static int student_id;
	private static String getURL(String url_string) {
		String content = "", line = "";
		try {
			URL url = new URL(url_string);
			HttpURLConnection http = (HttpURLConnection)url.openConnection();
			http.setRequestMethod("GET");
			http.connect();
			BufferedReader reader = new BufferedReader(new InputStreamReader(http.getInputStream()));
			while((line = reader.readLine()) != null)
				content += line;
			reader.close();
			return content;
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
			return "error";
		}

	}

	public static void readToken() {
		File file = new File("token.txt");
		BufferedReader br = null;
		try {
			try {
				br = new BufferedReader(new FileReader(file));
			}
			catch (Exception e) {
				System.out.println(e.getMessage());
			}
			String text;
			try {
				token = br.readLine();
			}
			catch (Exception e) {
				System.out.println(e.getMessage());
			}
		} finally {
			if (br != null) {
				try {
					br.close();
				}
				catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		}
	}

	public static void getID() {
		String content = getURL("https://umjicanvas.com/api/v1/users/self?access_token=" + token);
		System.out.println(content);

	}
	public static void fetchCourse() {
		String content = getURL("https://umjicanvas.com/api/v1/users/self/favorites/courses?access_token=" + token);
		Matcher m = Pattern.compile("\"course_code\":\"(.*?)\"").matcher(content);
		while (m.find()) {
			System.out.println(m.group());

		}

	}
	public canvas() {

	}
}
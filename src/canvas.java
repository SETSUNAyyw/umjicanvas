package src;

import java.io.*;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.*;
// import java.util.Map.*;
import java.util.regex.*;
import java.sql.*;
// import com.google.common.base.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;


public class canvas {
	private static ArrayList<String> courses;
	private static ArrayList<String> courses_id;
	private static String token;
	private static String student_id;
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
		// System.out.println(content);
		Matcher m = Pattern.compile("\"id\":(\\d+)").matcher(content);
		if (m.find()) {
			student_id = m.group(1);
			// System.out.println(student_id);
		}
	}

	public static void fetchCourseList() {
		String content = getURL("https://umjicanvas.com/api/v1/users/self/favorites/courses?access_token=" + token);
		Matcher m = Pattern.compile("\"course_code\":\"(.*?)\"").matcher(content);
		Matcher m_id = Pattern.compile("\"id\":(\\d+)").matcher(content);
		while (m.find() && m_id.find()) {
			Matcher m1 = Pattern.compile("Entry|FOCS|Undergraduate").matcher(m.group());
			if (!m1.find()) {
				// System.out.println(m.group(1));
				courses_id.add(m_id.group(1));
				courses.add(m.group(1));
			}

		}
		// System.out.println(courses_id.toString());

	}

	public static void runCommandLine(String command) {
	    Runtime rt = Runtime.getRuntime();
	    try {
			Process pr = rt.exec(command);
	    	String content = "", line = "";
			// System.out.println(pr.getOutputStream());
			BufferedReader reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			while((line = reader.readLine()) != null)
				content += line;
			reader.close();
			System.out.println(content);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void fetchCourses() {
		Connection connection = null;
        Statement statement = null;
        ArrayList<String> requests = new ArrayList<String> ();
        try {
            Class.forName("org.sqlite.JDBC");

            connection = DriverManager.getConnection("jdbc:sqlite:data/data.db");
            statement = connection.createStatement();
            
            
            // requests.add("SELECT name FROM sqlite_master WHERE type='table' AND name='VE320_info'");
            // String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='VE320_info'";
            // ResultSet rs = statement.executeQuery(sql);
            // requests.add("insert into VE320_info values");
			// for (int i = 0; i < courses.size(); i ++) {
			for (int i = 0; i < 1; i ++) {
				String course = courses.get(i);
				String course_id = courses_id.get(i);
				String content = null;
				Map<String, String> activities_today = new HashMap<> ();
				requests.add("create table if not exists " + course + "_info("
	            	+ "student_id integer unique, "
	            	+ "name text, "
	            	+ "sortable_name text, "
	            	+ "short_name text, "
	            	+ "email text, "
	            	+ "enrollment_id integer, "
	            	+ "user_id integer, "
	            	+ "course_id integer, "
	            	+ "type text, "
	            	+ "created_at text, "
	            	+ "updated_at text, "
	            	+ "associated_user_id integer, "
	            	+ "start_at text, "
	            	+ "end_at text, "
	            	+ "course_section_id integer, "
	            	+ "root_account_id integer, "
	            	+ "limit_privileges_to_course_section text, "
	            	+ "enrollment_state text, "
	            	+ "role text, "
	            	+ "role_id integer, "
	            	+ "last_activity_at text, "
	            	+ "total_activity_time integer, "
	            	+ "grades text, "
	            	+ "html_url text"
	            	+ ")");
				System.out.println("Fetching data from " + course + "...");
				for (int j = 1; j <= 5; j ++) {
					String web_content = getURL("https://umjicanvas.com/api/v1/courses/"
						+ course_id +"/users?access_token="
						+ token +"&include[]=email&include[]=enrollments&per_page=50&page="
						+ j);
					if (web_content.length() < 10) {
						// System.out.println("Break at " + j);
						break;
					}
					content = content + web_content + '\n';
				}
				try {
			      File file = new File("temp.txt");
			      FileOutputStream out = new FileOutputStream(file);
			      // OutputStreamWriter osw  = new OutputStreamWriter(new FileOutputStream(f));
			      // BufferedWriter bw = new BufferedWriter(osw);
			 
			      out.write(content.substring(4).getBytes());
			    } catch (IOException e) {
			      System.out.println(e);
			    }
			    runCommandLine("python canvas.py xxx");
				try {
					BufferedReader lineReader = new BufferedReader(new FileReader("tmp.csv"));
					lineReader.readLine();
					String lineText = null;
					while ((lineText = lineReader.readLine()) != null) {
						String[] line = lineText.split(",");
						// System.out.println(line[0] + " " + line.length);
						String current_student_id = line[0];
						String activity = line[21];
						activities_today.put(current_student_id, activity);
						String insert_row = "insert or replace into " + course + "_info values(" + lineText + ")";
						// System.out.println(insert_row);
						// for (int i = 1; i < line.size(); i ++) {
						// 	insert_row = insert_row + ", " + line[i];
						// }
						// insert_row = insert_row + ")";
						requests.add(insert_row);
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
	            connection.setAutoCommit(false);
	            for (String request: requests) {
	            	statement.addBatch(request);
	            }
	            try {
	            	statement.executeBatch();
	            	connection.commit();
	            }
	            catch (Exception e) {
	            	e.printStackTrace();
	            }
	            System.out.println("Info batch executed");
	            requests.clear();
	            System.out.println("Update activity...");
            	ResultSet rs = statement.executeQuery("select student_id from " + course + "_info");
            	ArrayList<String> student_id_list = new ArrayList<String> ();
            	while (rs.next()) {
	                // System.out.println(rs.getString(1));
	                student_id_list.add(rs.getString(1));
	            }
	            requests.add("create table if not exists " + course + "_activity("
	            	+ "student_id integer unique, "
	            	+ "record_start text, "
	            	+ "time_delta_0 integer"
	            	+ ")");
	            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	            for (String student_id: student_id_list) {
	            	requests.add("insert into " + course + "_activity values(" 
	            		+ student_id + ", \"" 
	            		+ LocalDate.now().format(dateTimeFormatter) + "\", "
	            		+ activities_today.get(student_id)
	            		+ ")");
	            }
	            for (String request: requests) {
	            	statement.addBatch(request);
	            }
	   //          for (String key: activities_today.keySet()){  
				// 	System.out.println(key+ " = " + activities_today.get(key));
				// }
	            try {
	            	statement.executeBatch();
	            }
	            catch (BatchUpdateException e) {
	            	// Pass
	            }
	            connection.commit();
	            System.out.println("Activity init batch executed");
	            requests.clear();
	            // rs = statement.executeQuery("pragma table_info( " + course + "_activity)");
	            Map<String, String> record_start_list = new HashMap<> ();
	            rs = statement.executeQuery("select student_id, record_start from " + course + "_activity");
	            while (rs.next()) {
	            	record_start_list.put(rs.getString(1), rs.getString(2));
	            	// System.out.println(rs.getString(1));
	            }
	            // System.out.println(record_start_list.values());
	            String course_start = Collections.min(record_start_list.values());
	            dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	            LocalDate course_start_date = LocalDate.parse(course_start, dateTimeFormatter);
	            long time_delta = ChronoUnit.DAYS.between(LocalDate.now(), course_start_date) + 1;
	            // System.out.println(time_delta);
	            try {
	            	rs = statement.executeQuery("select time_delta_" + time_delta + " from " + course + "_activity");
	            }
	            catch (Exception e) {
	            	System.out.println("Oh, seems that you have survived " + LocalDate.now().minusDays(1) + "!");
	            	// System.out.println("alter table " + course + "_activity add column time_delta_" + time_delta + " integer default null");
	            	statement.execute("alter table " + course + "_activity add column time_delta_" + time_delta + " integer default null");
	            	// statement.executeBatch();
	            	connection.commit();
	            }
	            requests.clear();
	            for (String student_id: activities_today.keySet()) {
	            	requests.add("update " + course + "_activity set time_delta_" + time_delta + " = " + activities_today.get(student_id) + " where student_id = " + student_id);
	            }
	            for (String request: requests) {
	            	statement.addBatch(request);
	            }
	            statement.executeBatch();
	            connection.commit();
	            requests.clear();
	            // rs = statement.executeQuery("select 2021-12-00 from " + course + "_activity");
            	// while (rs.next()) {
	            //     System.out.println(rs.getString(1));
	            // }
	            // System.out.println(rs.getString(2));
	            // for (String key: activities_today.keySet()) {
	            // 	requests.add("update " + course + "_activity set  = 'Nakajima' where id = 3;");
	            // }
			}
            // String sql = "create table test(id integer, name text)";
            // String sql = "insert into test values(4416, \"Yang Yiwen\")";
			// statement.executeQuery(sql);
            // String sql = "drop table test";
            // ResultSet rs = statement.executeQuery(sql);
            // statement.executeQuery(sql);
            // while (rs.next()) {
            //     System.out.println(rs.getString(2));
            // }
	    } catch (ClassNotFoundException e) {
	      e.printStackTrace();
	    } catch (SQLException e) {
	      e.printStackTrace();
	    } finally {
	        try {
	            if (statement != null) {
	                statement.close();
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        try {
	            if (connection != null) {
	                connection.close();
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
    
			// Matcher m;
			// m = Pattern.compile("\"user_id\":(\\d+)").matcher(content);
			// while (m.find()) {
			// 	System.out.println(m.group(1));
			// }
			// content = content.replaceAll("\"enrollments\":\\[\\{\"id\":(.*?),\"user_id\":(.*?),\"course_id\":(.*?),\"type\":\"(.*?)\",\"created_at\":\"(.*?)\",\"updated_at\":\"(.*?)\",\"associated_user_id\":(.*?),\"start_at\":(.*?),\"end_at\":(.*?),\"course_section_id\":(.*?),\"root_account_id\":(.*?),\"limit_privileges_to_course_section\":(.*?),\"enrollment_state\":\"(.*?)\",\"role\":\"(.*?)\",\"role_id\":(.*?),\"last_activity_at\":(.*?),\"total_activity_time\":(.*?),\"grades\":\\{\"html_url\":\"(.*?)\"\\},\"html_url\":\"(.*?)\"\\}\\]", 
				// "type:($4),created_at:($5),updated_at($6),start_at:($8),end_at:($9),course_section_id:($10),root_account_id:($11),limit_privileges_to_course_section:($12),enrollment_state:($13),role:($14),role_id:($15),last_activity_at:($16),total_activity_time:($17),grades_html_url:($17),html_url:($18)");
				// "type:($4),created_at:(),updated_at:(),start_at:(),end_at:(),course_section_id:($10),root_account_id:($11),limit_privileges_to_course_section:($12),enrollment_state:($13),role:($14),role_id:($15),last_activity_at:(),total_activity_time:($17),grades_html_url:(),html_url:()");
			// Map<String, String> map = Splitter.on(",").withKeyValueSeparator(":").split(content);
			// for (String key: map.keySet()){  
			// 	System.out.println(key+ " = " + map.get(key));
			// }
	}


	public static void createInfoDatabase() {
        Connection connection = null;
        Statement statement = null;
        ArrayList<String> requests = new ArrayList<String> ();
        try {
            Class.forName("org.sqlite.JDBC");

            connection = DriverManager.getConnection("jdbc:sqlite:data/data.db");
            statement = connection.createStatement();
            requests.add("create table if not exists VE320_info("
            	+ "student_id integer, "
            	+ "name text, "
            	+ "sortable_name text, "
            	+ "short_name text, "
            	+ "email text, "
            	+ "enrollment_id integer, "
            	+ "user_id integer, "
            	+ "course_id integer, "
            	+ "type text, "
            	+ "created_at text, "
            	+ "updated_at text, "
            	+ "associated_user_id integer, "
            	+ "start_at text, "
            	+ "end_at text, "
            	+ "course_section_id integer, "
            	+ "root_account_id integer, "
            	+ "limit_privileges_to_course_section text, "
            	+ "enrollment_state text, "
            	+ "role text, "
            	+ "role_id integer, "
            	+ "last_activity_at text, "
            	+ "total_activity_time integer, "
            	+ "grades text, "
            	+ "html_url text"
            	+ ")");
            
            // requests.add("SELECT name FROM sqlite_master WHERE type='table' AND name='VE320_info'");
            // String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='VE320_info'";
            // ResultSet rs = statement.executeQuery(sql);
            // requests.add("insert into VE320_info values");
            connection.setAutoCommit(false);
            for (int i = 0; i < requests.size(); i ++) {
            	statement.addBatch(requests.get(i));
            }
            statement.executeBatch();
            connection.commit();
            System.out.println("Batch executed");
            // String sql = "create table test(id integer, name text)";
            // String sql = "insert into test values(4416, \"Yang Yiwen\")";
			// statement.executeQuery(sql);
            // String sql = "select * from test";
            // String sql = "drop table test";
            // ResultSet rs = statement.executeQuery(sql);
            // statement.executeQuery(sql);
            // while (rs.next()) {
            //     System.out.println(rs.getString(2));
            // }
        } catch (ClassNotFoundException e) {
          e.printStackTrace();
        } catch (SQLException e) {
          e.printStackTrace();
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    
    }

	public canvas() {
		courses = new ArrayList<String>();
		courses_id = new ArrayList<String>();
	}
}
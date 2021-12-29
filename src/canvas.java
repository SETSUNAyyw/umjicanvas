package src;

import java.io.*;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.*;
import java.util.regex.*;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.nio.file.*;


public class canvas {
	private static ArrayList<String> courses;
	private static ArrayList<String> courses_id;
	private static String token;
	private static String my_student_id;
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

	public static int getID() {
		String content = getURL("https://umjicanvas.com/api/v1/users/self?access_token=" + token);
		Matcher m = Pattern.compile("\"id\":(\\d+)").matcher(content);
		if (m.find()) {
			my_student_id = m.group(1);
		}
		return (Integer.parseInt(my_student_id));
	}

	public static void fetchCourseList() {
		String content = getURL("https://umjicanvas.com/api/v1/users/self/favorites/courses?access_token=" + token);
		Matcher m = Pattern.compile("\"course_code\":\"(.*?)\"").matcher(content);
		Matcher m_id = Pattern.compile("\"id\":(\\d+)").matcher(content);
		while (m.find() && m_id.find()) {
			Matcher m1 = Pattern.compile("Entry|FOCS|Undergraduate").matcher(m.group());
			if (!m1.find()) {
				courses_id.add(m_id.group(1));
				courses.add(m.group(1));
			}

		}
	}

	public static void runCommandLine(String command) {
	    Runtime rt = Runtime.getRuntime();
	    try {
			Process pr = rt.exec(command);
	    	String content = "", line = "";
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
            
			for (int i = 0; i < courses.size(); i ++) {
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
						break;
					}
					content = content + web_content + '\n';
				}
				try {
			    	File file = new File("data/temp.txt");
			    	FileOutputStream out = new FileOutputStream(file);	 
			    	out.write(content.substring(4).getBytes());
			    	out.close();
			    } catch (IOException e) {
					System.out.println(e);
			    }
			    runCommandLine("python3 src/canvas.py");
			    Path path = Paths.get("data/temp.txt");
				try {
					Files.delete(path);
				}
				catch (IOException e) {
					System.out.println(e.getMessage());
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
				Map<String, String> info_old = new HashMap<> ();
				Map<String, String> info_new = new HashMap<> ();
				ResultSet rs = statement.executeQuery("select student_id, name from " + course + "_info");
				while (rs.next()) {
					info_old.put(rs.getString(1), rs.getString(2));
				}
				try {
					BufferedReader lineReader = new BufferedReader(new FileReader("data/tmp.csv"));
					lineReader.readLine();
					String lineText = null;
					while ((lineText = lineReader.readLine()) != null) {
						String[] line = lineText.split("\\s*,\\s*");
						String current_student_id = line[0];
						String current_student_name = line[1];
						String activity = line[21];
						info_new.put(current_student_id, current_student_name);
						activities_today.put(current_student_id, activity);
						String insert_row = "insert or replace into " + course + "_info values(" + lineText + ")";
						requests.add(insert_row);
					}
					lineReader.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				for (String request: requests) {
	            	statement.addBatch(request);
	            }
	            try {
	            	statement.executeBatch();
	            }
	            catch (BatchUpdateException e) {
	            	
	            }
	            connection.commit();
	            requests.clear();
				path = Paths.get("data/tmp.csv");
				try {
					Files.delete(path);
				}
				catch (IOException e) {
					System.out.println(e.getMessage());
				}
	            ArrayList<String> info_log_string = new ArrayList<String> ();
	            for (String id_old: info_old.keySet()) {
	            	if (!info_new.containsKey(id_old)) {
	            		info_log_string.add("[" + LocalDateTime.now() + "] Missing member in " + course + ": id = " + id_old + " name = " + info_old.get(id_old) + ".");
	            	}
	            }
	            for (String id_new: info_new.keySet()) {
	            	if (!info_old.containsKey(id_new)) {
	            		info_log_string.add("[" + LocalDateTime.now() + "] New member in " + course + ": id = " + id_new + " name = " + info_new.get(id_new) + ".");
	            	}
	            }
	            if (!info_log_string.isEmpty()) {
	            	try {
	            		File file = new File("data/info.log");
						FileOutputStream out = new FileOutputStream(file, true);
						for (String info_log_line: info_log_string) {
					    	out.write(info_log_line.getBytes());
					    	out.write('\n');
						}
						out.close();
					} catch (IOException e) {
						System.out.println(e.getMessage());
					}
	            }
	            System.out.println("Update activity...");
            	rs = statement.executeQuery("select student_id from " + course + "_info");
            	ArrayList<String> student_id_list = new ArrayList<String> ();
            	while (rs.next()) {
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
	            try {
	            	statement.executeBatch();
	            }
	            catch (BatchUpdateException e) {
	            	
	            }
	            connection.commit();
	            System.out.println("Activity init batch executed");
	            requests.clear();
	            Map<String, String> record_start_list = new HashMap<> ();
	            rs = statement.executeQuery("select student_id, record_start from " + course + "_activity");
	            while (rs.next()) {
	            	record_start_list.put(rs.getString(1), rs.getString(2));
	            }
	            String course_start = Collections.min(record_start_list.values());
	            dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	            LocalDate course_start_date = LocalDate.parse(course_start, dateTimeFormatter);
	            long time_delta = ChronoUnit.DAYS.between(course_start_date, LocalDate.now())/* + 1*/;
	            try {
	            	rs = statement.executeQuery("select time_delta_" + time_delta + " from " + course + "_activity");
	            }
	            catch (Exception e) {
	            	System.out.println("Oh, seems that you have survived " + LocalDate.now().minusDays(1) + "!");
	            	statement.execute("alter table " + course + "_activity add column time_delta_" + time_delta + " integer default null");
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
			}
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


	public static void rankActivity() {
		Connection connection = null;
        Statement statement = null;
        ArrayList<String> requests = new ArrayList<String> ();
        try {
            Class.forName("org.sqlite.JDBC");

            connection = DriverManager.getConnection("jdbc:sqlite:data/data.db");
            statement = connection.createStatement();
            for (String course: courses) {
            	ArrayList<Integer> activities_today = new ArrayList<Integer> ();
            	ArrayList<Integer> activities_yesterday = new ArrayList<Integer> ();
            	ArrayList<Integer> activities_delta = new ArrayList<Integer> ();
            	ArrayList<Integer> student_id_list = new ArrayList<Integer> ();
            	ArrayList<String> name_list = new ArrayList<String> ();
            	ArrayList<User> user_list = new ArrayList<User> ();
            	Map<String, Integer> id_check = new HashMap<> ();
            	ArrayList<String> record_start_list = new ArrayList<String> ();
            	ResultSet rs = statement.executeQuery("select record_start from " + course + "_activity");
            	while (rs.next()) {
            		record_start_list.add(rs.getString(1));
            	}
            	String record_start = Collections.min(record_start_list);
            	DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            	LocalDate record_start_date = LocalDate.parse(record_start, dateTimeFormatter);
            	long time_delta = ChronoUnit.DAYS.between(record_start_date, LocalDate.now());
            	String column_today = "time_delta_" + time_delta;
            	String column_yesterday = "time_delta_" + (time_delta - 1);
            	if (record_start.equals(LocalDate.now().format(dateTimeFormatter))) {
            		System.out.println("No past data, please try again tomorrow.");
            		return;
            	}

            	rs = statement.executeQuery("select student_id, " + column_yesterday + ", " + column_today + " from " + course + "_activity");
            	int id_check_index = 0;
            	while (rs.next()) {
            		id_check.put(rs.getString(1), id_check_index);
            		student_id_list.add(Integer.parseInt(rs.getString(1)));
            		try {
            			activities_yesterday.add(Integer.parseInt(rs.getString(2)));
            		}
            		catch (NumberFormatException e) {
            			activities_yesterday.add(0);
            		}
            		try {
            			activities_today.add(Integer.parseInt(rs.getString(3)));
            		}
            		catch (NumberFormatException e) {
            			activities_today.add(0);
            		}

            		id_check_index ++;
            	}
            	for (int index = 0; index < activities_today.size(); index ++) {
            		activities_delta.add(activities_today.get(index) - activities_yesterday.get(index));
            	}
            	rs = statement.executeQuery("select student_id, name from " + course + "_info");
            	while (rs.next()) {
            		try {
            			int index = id_check.get(rs.getString(1));
            			user_list.add(new User(student_id_list.get(index), rs.getString(2), activities_delta.get(index)));
            		} catch (Exception e) {
            			
            		}
            	}
            	Collections.sort(user_list, new UserComparator());
            	System.out.println("-----------------------------------------------");
            	System.out.println("Today's 15 best contributors to " + course + "!");
            	System.out.println("-----------------------------------------------");
            	System.out.println("id\tname\t\t\t\tactivity");
            	for (int index = 0; index < 15; index ++) {
            		user_list.get(index).print();
            	}

            }
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

	public static void plotHeatMap(int student_id) {
		Connection connection = null;
        Statement statement = null;
        ArrayList<String> requests = new ArrayList<String> ();
        if (student_id == Integer.parseInt(my_student_id)) {
	        try {
	            Class.forName("org.sqlite.JDBC");

	            connection = DriverManager.getConnection("jdbc:sqlite:data/data.db");
	            statement = connection.createStatement();
	            ArrayList<Integer> total_activity = null;
	            for (String course: courses) {
	            	ArrayList<Integer> activities = new ArrayList<Integer> ();
	            	ResultSet rs = statement.executeQuery("select * from " + course + "_activity where student_id = " + student_id);
	            	int while_count = 1;
	            	while (true) {
		            	try {
		            		if (while_count >= 3) {
		            			activities.add(Integer.parseInt(rs.getString(while_count)));
		            		}
		            		while_count ++;
		            	} catch (SQLException e) {
		            		break;
		            	}
		            }
		            if (total_activity == null) {
		            	total_activity = new ArrayList<Integer> (Collections.nCopies(activities.size(), 0));
		            }
		            for (int j = 1; j < activities.size(); j ++) {
		            	total_activity.set(j, total_activity.get(j) + activities.get(j) - activities.get(j - 1));
		            }

	            }
				try {
					File file = new File("data/temp.txt");
					FileOutputStream out = new FileOutputStream(file);
					out.write(total_activity.toString().getBytes());
					out.close();
				} catch (IOException e) {
					System.out.println(e);
				}
				runCommandLine("python3 src/canvas.py -p");
				Path path = Paths.get("data/temp.txt");
				try {
					Files.delete(path);
				}
				catch (IOException e) {
					System.out.println(e.getMessage());
				}
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
	}

	public canvas() {
		courses = new ArrayList<String>();
		courses_id = new ArrayList<String>();
	}
}
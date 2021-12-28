import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.zip.GZIPInputStream;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;

public class setup {
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

	public static void main(String[] args) {
		String data_path = "data/";
		String cache_path = data_path + ".cache/";
		String line = null;
		// System.out.println(csv_lines.size());
		File file = new File(cache_path);
		String[] directories = file.list(new FilenameFilter() {
		  @Override
		  public boolean accept(File current, String name) {
		    return new File(current, name).isDirectory();
		  }
		});
		String info_cache_path = cache_path + directories[1] + "/";
		file = new File(info_cache_path);
		String[] courses_csv = file.list(new FilenameFilter() {
		  @Override
		  public boolean accept(File current, String name) {
		    return new File(current, name).isFile();
		  }
		});
		// System.out.println(Arrays.toString(directories));
		// System.out.println(Arrays.toString(files));
		Connection connection = null;
        Statement statement = null;
        ArrayList<String> requests = new ArrayList<String> ();
        try {
            Class.forName("org.sqlite.JDBC");

            connection = DriverManager.getConnection("jdbc:sqlite:data/data.db");
            statement = connection.createStatement();
			connection.setAutoCommit(false);
            for (int i = 0; i < courses_csv.length; i ++) {
            // for (int i = 1; i < 2; i ++) {
            	String course = courses_csv[i].split("\\.")[0];
            	System.out.println(course);
            	// System.out.println(courses_csv[i].split("\\.")[0]);
            	// System.out.println(course);
				Path path = Paths.get(data_path + courses_csv[i]);
				ArrayList<String> csv_lines = new ArrayList<String> ();
				try(
				  InputStream is = Files.newInputStream(path);
				  GZIPInputStream gis = new GZIPInputStream(is);
				  InputStreamReader isReader = new InputStreamReader(gis, StandardCharsets.UTF_8);
				  BufferedReader br = new BufferedReader(isReader); 
				) {
				  while((line = br.readLine()) != null) {
				  	csv_lines.add(line);
				}
				  br.close();
				} catch (Exception e) {

				}
				ArrayList<String> csv_lines_copy = new ArrayList<String> ();
				String[] csv_lines_copy_split = csv_lines.get(0).split("\\s*,\\s*");
				String record_start = csv_lines_copy_split[2];
				String record_end = csv_lines_copy_split[csv_lines_copy_split.length - 1];
				for (String csv_line: csv_lines) {
					// String[] line_split = csv_line.split(",");
					ArrayList<String> line_split = new ArrayList<String>(Arrays.asList(csv_line.split("\\s*,\\s*")));
					line_split.add(2, "\"" + record_start + "\"");
					csv_line = line_split.subList(1, line_split.size()).toString().replace("[", "").replace("]", "");
					csv_lines_copy.add(csv_line);
					// System.out.println(csv_line);
				}
				// String record_start = csv_lines_copy.get(0);
				// System.out.println(record_start);
				// System.out.println(record_end);
				DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	            LocalDate record_start_date = LocalDate.parse(record_start, dateTimeFormatter);
	            LocalDate record_end_date = LocalDate.parse(record_end, dateTimeFormatter);
	            long time_delta = ChronoUnit.DAYS.between(record_start_date, record_end_date);
	            // System.out.println(time_delta);
				// ArrayList<String> requests = new ArrayList<String> ();
				requests.clear();
				String request_string = "create table if not exists " + course + "_activity(student_id integer unique, record_start text";
	            for (int j = 0; j <= time_delta; j ++) {
	            	request_string = request_string + ", time_delta_" + j + " integer default null";
	            }
	            request_string = request_string + ")";
				requests.add(request_string);
				for (int j = 1; j < csv_lines_copy.size(); j ++) {
					requests.add("insert into " + course + "_activity values(" + csv_lines_copy.get(j) + ")");
				}
	            for (String request: requests) {
	            	statement.addBatch(request);
	            }
	            
	            statement.executeBatch();
	            connection.commit();
				// path = Paths.get(info_cache_path + courses_csv[i]);
				// System.out.println(path);
				// csv_lines.clear();
				// try(
				//   InputStream is = Files.newInputStream(path);
				//   GZIPInputStream gis = new GZIPInputStream(is);
				//   InputStreamReader isReader = new InputStreamReader(gis, StandardCharsets.UTF_8);
				//   BufferedReader br = new BufferedReader(isReader); 
				// ) {
				//   while((line = br.readLine()) != null) {
				//   	csv_lines.add(line);
				//   	// System.out.println(line);
				// }
				//   br.close();
				// } catch (Exception e) {

				// }
				// try {
			 //    	file = new File("temp.txt");
			 //    	FileOutputStream out = new FileOutputStream(file);
			 //      // OutputStreamWriter osw  = new OutputStreamWriter(ne	FileOutputStream(f));
			 //    	// BufferedWriter bw = new BufferedWriter(osw)
			 //    	for (String csv_line: csv_lines) {
			 //    		out.write(csv_line.getBytes());
			 //    	}
			 //    } catch (IOException e) {
			 //    	System.out.println(e);
			 //    }
				// runCommandLine("python canvas.py xxx");
				// ArrayList<String> newest_activity = new ArrayList<String> ();
				// for (String csv_line: csv_lines) {
				// 	// String[] line_split = csv_line.split(",");
				// 	ArrayList<String> line_split = new ArrayList<String>(Arrays.asList(csv_line.split("\\s*,\\s*")));
				// 	// line_split.add(2, "\"" + record_start + "\"");
				// 	// csv_line = line_split.subList(1, line_split.size()).toString().replace("[", "").replace("]", "");
				// 	// csv_lines_copy.add(csv_line);
				// 	// System.out.println(csv_line);
				// 	newest_activity.add(line_split.get(22));
				// 	// System.out.println();
				// }
				// for (String activity_line: newest_activity) {
				// 	System.out.println(activity_line);
				// }
	            
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
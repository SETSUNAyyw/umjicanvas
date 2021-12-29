
import java.io.*;
import src.canvas;
import java.nio.file.*;


public class main {
	public static void main(String[] args) {
		File path = new File("data/");
		if (!path.exists()) {
			path.mkdir();
		}
		canvas c = new canvas();

		canvas.readToken();
		int my_student_id = canvas.getID();
		canvas.fetchCourseList();
		canvas.fetchCourses();
		canvas.rankActivity();
		// Only my heat map (currently)
		canvas.plotHeatMap(my_student_id);
	}
}
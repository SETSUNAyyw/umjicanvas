
import java.io.*;
import src.canvas;


public class main {
	public static void main(String[] args) {
		canvas c = new canvas();

		canvas.readToken();
		int my_student_id = canvas.getID();
		canvas.fetchCourseList();
		canvas.fetchCourses();
		canvas.rankActivity();
		canvas.plotHeatMap(my_student_id);
	}
}
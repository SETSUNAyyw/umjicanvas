package src;

public class User {
	public int student_id;
	public String name;
	public int activity;

	public User(int student_id, String name, int activity) {
		this.student_id = student_id;
		this.name = name;
		this.activity = activity;
	}

	public void print() {
		System.out.println(String.format("%-8d%-30s%6d", this.student_id, this.name, this.activity));
		// System.out.println(this.name.length());
	}

}
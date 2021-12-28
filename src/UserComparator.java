package src;

import java.util.Comparator;
import src.User;

public class UserComparator implements Comparator<User> {
	@Override
	public int compare(User u1, User u2) {
		if (u1.activity == u2.activity) {
			return 0;
		}
		return u1.activity > u2.activity ? -1 : 1;
	}
}
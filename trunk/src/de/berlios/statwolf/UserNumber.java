package de.berlios.statwolf;

public class UserNumber implements Comparable<UserNumber> {
	
	String user;
	Integer number;

	public UserNumber() {
		// do nothing
	}
	
	public UserNumber(String user, Integer number) {
		this.user = user;
		this.number = number;
	}

	public int compareTo(UserNumber o) {
		if ( number < o.number) { return -1; }
		if ( number > o.number) { return 1 ;}
		return user.compareTo(o.user);
	}

}

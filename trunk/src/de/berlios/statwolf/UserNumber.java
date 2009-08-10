package de.berlios.statwolf;

/** helper object to sort a list of names with associated numbers. */
public class UserNumber implements Comparable<UserNumber> {
	
	/** string identifier for user. */
	private String user;
	/** integer identifier for user. */
	private Integer number;
	
	/**
	 * Create object.
	 * @param pUser string identifier for user
	 * @param pNumber integer associated with user
	 */
	public UserNumber(final String pUser, final Integer pNumber) {
		user = pUser;
		number = pNumber;
	}

	/**
	 * compare two <code>UserNumber</code> objects to each other.
	 * If <code>number</code> already allows a distinction, use number, 
	 * otherwise compare <code>user</code> 
	 * @param o other <code>UserNumber</code> to compare this object with
	 * @return -1, 0 or 1
	 */
	public final int compareTo(final UserNumber o) {
		if (number < o.number) { return -1; }
		if (number > o.number) { return 1; }
		return user.compareTo(o.user);
	}
	
	/** return the user property of this object. */
	public final String getUser() { return user; }
	
	/** return the number property of this object. */
	public final Integer getNumber() { return number; }

}

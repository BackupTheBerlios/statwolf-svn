package de.berlios.statwolf;

// OK

/** helper object to sort a list of names with associated numbers. */
public class UserNumber implements Comparable<UserNumber> {
	
	/** string identifier for user. */
	private final transient String user;
	/** integer identifier for user. */
	private final transient Integer number;
	
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
	 * @param object other <code>UserNumber</code> to compare this object with
	 * @return -1, 0 or 1
	 */
	public final int compareTo(final UserNumber object) {
		int ret;
		if (number < object.number) { 
			ret = -1; 
		} else if (number > object.number) { 
			ret = 1; 
		} else { 
			ret = user.compareTo(object.user); 
		}
		return ret;
	}
	
	/** return the user property of this object. */
	public final String getUser() { return user; }
	
	/** return the number property of this object. */
	public final Integer getNumber() { return number; }

}

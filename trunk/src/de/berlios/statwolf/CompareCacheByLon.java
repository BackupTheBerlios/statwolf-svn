package de.berlios.statwolf;

import java.util.Comparator;

public final class CompareCacheByLon implements Comparator < Cache > {

	/** do nothing. */
	public CompareCacheByLon() { }
	
	public int compare(final Cache ob1, final Cache ob2) {
		return ob1.getLon().compareTo(ob2.getLon());
	}

}

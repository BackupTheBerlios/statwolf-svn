package de.berlios.statwolf;

import java.util.Comparator;

public final class CompareCacheByLat implements Comparator<Cache> {

	/** do nothing. */
	public CompareCacheByLat() { }
	
	public int compare(final Cache ob1, final Cache ob2) {
		return ob1.getLat().compareTo(ob2.getLat());
	}

}

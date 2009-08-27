package de.berlios.statwolf;

// OK

import java.util.Comparator;

/** comparator for the latitude field of two caches. */
public final class CompareCacheByLat implements Comparator<Cache> {
	
	/**
	 * compare the latitude of two given Cache objects.
	 * 
	 * @param ob1
	 *            a Cache object
	 * @param ob2
	 *            a Cache object
	 * @return negative integer, zero or positive integer if the the latitude
	 *         of ob1 is south of, the same or north of the latitude in ob2
	 */
	public int compare(final Cache ob1, final Cache ob2) {
		return ob1.getLat().compareTo(ob2.getLat());
	}

}

package de.berlios.statwolf;

// OK

import java.util.Comparator;

/** comparator for the longitude field of two caches. */
public final class CompareCacheByLon implements Comparator<Cache> {
	
	/**
	 * compare the longitude of two given Cache objects.
	 * 
	 * @param ob1
	 *            a Cache object
	 * @param ob2
	 *            a Cache object
	 * @return negative integer, zero or positive integer if the the longitude
	 *         of ob1 is west of, the same or east of the longitude in ob2
	 */
	public int compare(final Cache ob1, final Cache ob2) {
		return ob1.getLon().compareTo(ob2.getLon());
	}

}

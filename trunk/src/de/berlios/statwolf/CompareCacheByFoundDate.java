package de.berlios.statwolf;

// OK

import java.util.Comparator;

/** compare two cache objects by found date. */
public final class CompareCacheByFoundDate implements Comparator<Cache> {

	/**
	 * compare the calendar objects of found date for two given waypoints.
	 * 
	 * @param ob1
	 *            a Cache object
	 * @param ob2
	 *            a Cache object
	 * @return negative integer, zero or positive integer if the the date in ob1
	 *         is before, the same or after the date in ob2
	 */
	public int compare(final Cache ob1, final Cache ob2) {
		return ob1.getFoundDate().compareTo(ob2.getFoundDate());
	}
}

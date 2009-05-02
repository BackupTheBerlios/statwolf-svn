package de.berlios.statwolf;

import java.util.Comparator;

public class CompareCacheByFoundDate implements Comparator<Cache> {

	public CompareCacheByFoundDate() {
		// do nothing
	}

	public int compare(Cache ob1, Cache ob2) {
		return ob1.found.compareTo(ob2.found);
	}
}

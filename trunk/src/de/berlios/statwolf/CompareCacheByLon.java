package de.berlios.statwolf;

import java.util.Comparator;

public class CompareCacheByLon implements Comparator<Cache> {

	public CompareCacheByLon() {
		// do nothing
	}
	
	public int compare(Cache ob1, Cache ob2) {
		return ob1.getLon().compareTo(ob2.getLon());
	}

}

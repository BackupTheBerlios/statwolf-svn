package de.berlios.statwolf;

import java.util.Comparator;

public class CompareCacheByLon implements Comparator<Cache> {

	public CompareCacheByLon() {
		// do nothing
	}
	
	public int compare(Cache ob1, Cache ob2) {
		return ob1.lon.compareTo(ob2.lon);
	}

}

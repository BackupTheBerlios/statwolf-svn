package de.berlios.statwolf;

import java.util.Comparator;

public class CompareCacheByLat implements Comparator<Cache> {

	public CompareCacheByLat() {
		// do nothing
	}
	
	public int compare(Cache ob1, Cache ob2) {
		return ob1.getLat().compareTo(ob2.getLat());
	}

}

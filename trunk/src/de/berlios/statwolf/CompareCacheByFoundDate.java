package de.berlios.statwolf;

import java.util.Comparator;

public final class CompareCacheByFoundDate implements Comparator < Cache > {

	/** do nothing. */
	public CompareCacheByFoundDate() { }

	public final int compare(final Cache ob1, final Cache ob2) {
		return ob1.getFoundDate().compareTo(ob2.getFoundDate());
	}
}

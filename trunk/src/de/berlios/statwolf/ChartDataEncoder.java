package de.berlios.statwolf;

/**
 * encode and scale arrays of values suitable for usage with Googles chart API.
 * @author greis
 *
 */
public final class ChartDataEncoder {
	
	/** code string for simple encoding. */
	private static final String SIMPLECODE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	/** code string for extended encoding. */
	private static final String EXTENDEDCODE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-.";
	/** maximum value that can be encoded using simple encoding. */
	public static final Integer MAXSIMPLE = 61;
	/** maximum value that can be encoded using extended encoding. */
	public static final Integer MAXEXTENDED = 4095;

	/** constructor does nothing. */
	private ChartDataEncoder() {
	}
	
	/**
	 * scales and converts an array of integers to the google chart API special formats.
	 * @param values array of positive integers to scale and convert 
	 * @param max maximum value the data should be scaled to
	 * @param extended use extended format if true or simple format if false
	 * @return
	 * @see 
	 */
	public static String scale(final Integer[] values, final Integer max, final Boolean extended) {
		final StringBuffer ret = new StringBuffer();
		for (Integer value : values) {
			if (extended) {
				ret.append(extended(value * MAXEXTENDED / max));
			} else {
				ret.append(simple(value * MAXSIMPLE / max));
			}
		}
		return ret.toString();
	}
	
	/**
	 * encodes a value to the simple data format of google charts API.
	 * @param value value to encode (0 <= value <= <code>MAXSIMPLE</code>)
	 * @return simple encoding for <code>value</code> or _ if value can not be converted successfully
	 * @see MAXSIMPLE 
	 */
	public static String simple(final Integer value) {
		if (value == null || value < 0 || value > MAXSIMPLE) { return "_"; }
		return String.valueOf(SIMPLECODE.codePointAt(value));
	}
	
	/**
	 * converts a value to the google charts api extened format.
	 * @param value value to encode (0 <= value <= <code>MAXEXTENDED</code>)
	 * @return extended encoding for <code>value</code> or __ if value can not be converted successfully
	 * @see MAXEXTENDED
	 */
	public static String extended(final Integer value) {
		Integer high;
		Integer low;
		
		if (value == null || value < 0 || value > MAXEXTENDED) { return "__"; }
		high = (int) Math.floor(value / EXTENDEDCODE.length());
		low = (value % EXTENDEDCODE.length());
		return String.valueOf(EXTENDEDCODE.charAt(high)).concat(String.valueOf(EXTENDEDCODE.charAt(low)));
	}
	
	/**
	 * converts an array of integers to google maps text encoded string.
	 * @param values values to ancode
	 * @return comma seprated string of values
	 */
	public static String simple(final Integer[] values) {
		final StringBuffer ret = new StringBuffer();
		for (Integer index = 0; index < values.length; index++) {
			ret.append(values[index]);
			if (index < values.length - 1) { ret.append(','); }
		}
		return ret.toString();
	}
}

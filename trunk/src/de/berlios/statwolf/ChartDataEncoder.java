package de.berlios.statwolf;

// OK

/**
 * encode and scale arrays of values suitable for usage with Google chart API
 * (http://code.google.com/apis/chart/formats.html)
 */
public final class ChartDataEncoder {
	
	/** code string for simple encoding. */
	private static final String SIMPLECODE = 
		"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	/** code string for extended encoding. */
	private static final String EXTENDEDCODE = 
		"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-.";
	/** maximum value that can be encoded using simple encoding. */
	public static final Integer MAXSIMPLE = 61;
	/** maximum value that can be encoded using extended encoding. */
	public static final Integer MAXEXTENDED = 4095;

	/** constructor does nothing. */
	private ChartDataEncoder() { }
	
	/**
	 * this object must not be cloned but only be used in a static way. any
	 * attempt to clone the object will cause a CloneNotSupportedException
	 * 
	 * @return will not return but throw an exception when called
	 * @throws CloneNotSupportedException
	 *             always thrown
	 */
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
	
	/**
	 * scales and converts an array of integers to the Google chart API special
	 * formats. for details see http://code.google.com/apis/chart/formats.html
	 * 
	 * @param values
	 *            array of positive integers to scale and convert
	 * @param max
	 *            maximum value the data should be scaled to
	 * @param extended
	 *            use extended format if true or simple format if false
	 * @return encoded and scaled representation of <code>values</code>
	 * @see http://code.google.com/apis/chart/formats.html
	 */
	public static String scale(final Integer[] values, 
			final Integer max, final Boolean extended) {
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
	 * 
	 * @param value
	 *            value to encode (0 <= value <= <code>MAXSIMPLE</code>)
	 * @return simple encoding for <code>value</code> or _ if value can not be
	 *         converted successfully
	 * @see MAXSIMPLE
	 */
	public static String simple(final Integer value) {
		String ret;
		if (value == null || value < 0 || value > MAXSIMPLE) { 
			ret = "_"; 
		} else {
			ret = String.valueOf(SIMPLECODE.codePointAt(value)); 
		}
		return ret;
	}
	
	/**
	 * converts a value to the google charts api extened format.
	 * 
	 * @param value
	 *            value to encode (0 <= value <= <code>MAXEXTENDED</code>)
	 * @return extended encoding for <code>value</code> or __ if value can not
	 *         be converted successfully
	 * @see MAXEXTENDED
	 */
	public static String extended(final Integer value) {
		Integer high;
		Integer low;
		String ret;
		
		if (value == null || value < 0 || value > MAXEXTENDED) { 
			ret = "__"; 
		} else {
			high = (int) Math.floor(value / EXTENDEDCODE.length());
			low = (value % EXTENDEDCODE.length());
			ret = String.valueOf(EXTENDEDCODE.charAt(high))
				.concat(String.valueOf(EXTENDEDCODE.charAt(low)));
		}
		return ret;
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

package de.berlios.statwolf;

// OK

import java.io.File;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;

/** load and parse the details file for a given waypoint.
 * at the moment only state and country information is extracted
 */
public class CacheDetails {
	
	/** state and country are not null. */
	private transient Boolean valid = false;
	/** country waypoint is listed in. */
	private transient String country;
	/** state waypoint is listed in. */
	private transient String state;
	/** reference to logging object for debugging. */
	private static final Logger LOGGER = Logger.getLogger(CacheDetails.class);

	/**
	 * create a details object.<br>
	 * checks for a file with the name of lowercase <code>cacheId</code>.xml in
	 * the given <code>directory</code> and tries to parse the content als XML<br>
	 * logs an error if parsing fails.
	 * 
	 * @param cacheId
	 *            waypoint id. will be converted to lower case.
	 * @param directory
	 *            profile directory
	 */
	public CacheDetails(final String cacheId, final String directory) {
		final String wptFileName = directory
			.concat(File.separator)
			.concat(cacheId.toLowerCase(Locale.getDefault())).concat(".xml");
		final File wptFile = new File(wptFileName);

		if (wptFile.canRead()) {
			final SAXBuilder parser = new SAXBuilder();
			try {
				final Document document = parser.build(wptFile);
				country = document.getRootElement().getChildText("COUNTRY");
				state = document.getRootElement().getChildText("STATE");

				if (country == null || state == null) {
					valid = false;
				} else {
					valid = true;
				}
			} catch (Exception ex) {
				LOGGER.info("parsing error for details of " + cacheId, ex);
				return;
			}
		} else {
			return;
		}
	}
	
	/** @return true if both state and country are not null. */ 
	public final Boolean isValid() { return valid; }
	/** @return state waypoint is located in. may be null. */
	public final String getState() { return state; }
	/** @return country waypoint is located in. may be null. */
	public final String getCountry() { return country; }
}

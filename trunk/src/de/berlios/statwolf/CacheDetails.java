package de.berlios.statwolf;

import java.io.File;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;

public class CacheDetails {
	
	private Boolean valid = false;
	private String country;
	private String state;
	private static final Logger LOGGER = Logger.getLogger(CacheDetails.class);

	public CacheDetails(final String cacheId, final String directory) {
		final String wptFileName = directory.concat(File.separator).concat(cacheId.toLowerCase()).concat(".xml");
		final File wptFile = new File(wptFileName);
		if (wptFile.canRead()) {
			final SAXBuilder parser = new SAXBuilder();
			try {
				final Document document = parser.build(wptFile);
				country = document.getRootElement().getChildText("COUNTRY");
				state = document.getRootElement().getChildText("STATE");

				if ((country != null) && (state != null)) {
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
	
	public final Boolean isValid() { return valid; }
	
	public final String getState() { return state; }
	
	public final String getCountry() { return country; }
}

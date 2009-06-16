package de.berlios.statwolf;

import java.io.File;
import org.apache.log4j.*;
import org.jdom.*;
import org.jdom.input.*;

public class CacheDetails {
	
	/**  */
	private Boolean valid = false;
	private String country;
	private String state;
	private static Logger logger = Logger.getLogger(CacheDetails.class);

	/** constructor does nothing */
	public CacheDetails(String cacheId, String directory) {
		String wptFileName = directory.concat(File.separator).concat(cacheId.toLowerCase()).concat(".xml");
		File wptFile = new File(wptFileName);
		if (wptFile.canRead()) {
			SAXBuilder parser = new SAXBuilder();
			try {
				Document document = parser.build(wptFile);
				country = document.getRootElement().getChildText("COUNTRY");
				state = document.getRootElement().getChildText("STATE");

				if ((country != null) && (state != null)) {
					valid = true;
				}
			} catch (Exception ex) {
				logger.info("parsing error for details of "+cacheId, ex);
				return;
			}
		} else {
			return;
		}
	}
	
	public Boolean isValid() {
		return valid;
	}
	
	public String getState() {
		return state;
	}
	
	public String getCountry() {
		return country;
	}

}

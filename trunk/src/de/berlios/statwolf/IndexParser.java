package de.berlios.statwolf;

import org.jdom.*;
import org.jdom.input.*;
import java.util.*;
import com.bbn.openmap.*;
import org.apache.log4j.*;

public class IndexParser {

	private List<Cache> foundCaches = new ArrayList<Cache>();
	private LatLonPoint homeCoordinates;
	private static Logger logger = Logger.getLogger(HTMLOutput.class);

	// jdom uses old syntax for compatibility
	@SuppressWarnings("unchecked")
	public IndexParser(Properties prefs) {

		SAXBuilder parser = new SAXBuilder();
		foundCaches = new ArrayList<Cache>();
		String indexFileName;

		indexFileName = prefs.getProperty("foundindex");

		try {
			Document document = parser.build(indexFileName);
			filterCaches(document.getRootElement().getChildren("CACHE"));
			setHomeCoordinates(document.getRootElement().getChildren("CENTRE"));
		} catch (Exception ex) {
			logger.fatal(ex.getLocalizedMessage());
			logger.debug(ex.getStackTrace());
			System.exit(1);
		}
	}
	
	private void filterCaches(List<Element> caches) {
		Iterator<Element> iter = (Iterator<Element>) caches.iterator();
		while(iter.hasNext()) {
			Element cache = (Element)iter.next();
			String cacheId = cache.getAttributeValue("wayp");
			String cacheFound = cache.getAttributeValue("found");
			
			if (cacheId.indexOf("GC") == 0 && cacheFound.equals("true")) {
				Cache fc = new Cache();

				fc.setArchived(cache.getAttributeValue("archived"));
				fc.setDifficulty(cache.getAttributeValue("dif"));
				fc.setFound(cache.getAttributeValue("status"));
				fc.setHidden(cache.getAttributeValue("hidden"));
				fc.setId(cache.getAttributeValue("wayp"));
				fc.setLat(cache.getAttributeValue("lat"));
				fc.setLon(cache.getAttributeValue("lon"));
				fc.setName(cache.getAttributeValue("name"));
				fc.setOnline(cache.getAttributeValue("online"));
				fc.setOwner(cache.getAttributeValue("owner"));
				fc.setSize(cache.getAttributeValue("size"));
				fc.setTerrain(cache.getAttributeValue("terrain"));
				fc.setType(cache.getAttributeValue("type"));
				
				foundCaches.add(fc);
			}
		}
	}

	public List<Cache> getFoundCaches () {
		return foundCaches;
	}

	public void setHomeCoordinates(List<Element> centers) {
		homeCoordinates = new LatLonPoint();
		try {
			Element center = centers.get(0);
			String lat;
			String lon;
			lat=center.getAttributeValue("lat");
			lon=center.getAttributeValue("lon");
			homeCoordinates.setLatLon(Float.parseFloat(lat), Float.parseFloat(lon));
		} catch (Exception ex) {
			homeCoordinates.setLatLon(0, 0);
		}
	}

	public LatLonPoint getHomeCoordinates() {
		return homeCoordinates;
	}
}

package de.berlios.statwolf;

import org.jdom.*;
import org.jdom.input.*;
import java.util.*;
import java.io.*;
import com.bbn.openmap.*;
import org.apache.log4j.*;
import java.net.*;

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

		indexFileName = prefs.getProperty("indexfile");
		if (indexFileName != null) {
			try {
				Document document = parser.build(indexFileName);
				filterCaches(document.getRootElement().getChildren("CACHE"));
				setHomeCoordinates(document.getRootElement().getChildren("CENTRE"));
			} catch (FileNotFoundException ex) {
				logger.fatal("index file not found: ".concat(indexFileName));
				System.exit(1);
			} catch (JDOMException ex) {
				logger.fatal(ex);
				System.exit(1);
			} catch (MalformedURLException ex) {
				logger.fatal("Unable to parse file ".concat(indexFileName));
				System.exit(1);
			} catch (IOException ex) {
				logger.fatal(ex);
				System.exit(1);
			}
		} else {
			logger.fatal("index file not set. please check preferences.properties");
			System.exit(1);
		}
		
		if (foundCaches.size() == 0) {
			logger.error("no found caches in index file");
			System.exit(0);
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
			logger.warn("unable to determine home coordinates. using N 0 E 0");
			homeCoordinates.setLatLon(0, 0);
		}
	}

	public LatLonPoint getHomeCoordinates() {
		return homeCoordinates;
	}
}

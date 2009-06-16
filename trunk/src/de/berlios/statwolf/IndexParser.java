package de.berlios.statwolf;

import org.jdom.*;
import org.jdom.input.*;
import java.util.*;
import java.io.*;
import com.bbn.openmap.*;

import de.cachewolf.CacheType;

import org.apache.log4j.*;
import java.net.*;

public class IndexParser {

	private List<Cache> foundCaches = new ArrayList<Cache>();
	private LatLonPoint homeCoordinates;
	private static Logger logger = Logger.getLogger(IndexParser.class);

	// jdom uses old syntax for compatibility
	@SuppressWarnings("unchecked")
	public IndexParser(String indexdir) {
		String indexfile = indexdir.concat("index.xml");
		Byte idxVersion = null;

		SAXBuilder parser = new SAXBuilder();
		foundCaches = new ArrayList<Cache>();

		if (indexfile != null) {
			try {
				Document document = parser.build(indexfile);
				if (document.getRootElement().getChildren("VERSION").size() > 0) {
					idxVersion = 1;
					Element versionElement = (Element) document.getRootElement().getChildren("VERSION").iterator().next();
					logger.debug("version information is: "+versionElement.getAttributes().toString());
					idxVersion = Byte.parseByte(versionElement.getAttributeValue("value"));
					logger.debug("index version is: "+idxVersion);
				} else {
					idxVersion = 0;
				}
				filterCaches(document.getRootElement().getChildren("CACHE"), idxVersion, indexdir);
				setHomeCoordinates(document.getRootElement().getChildren("CENTRE"));
			} catch (FileNotFoundException ex) {
				logger.fatal("index file not found: ".concat(indexfile));
				System.exit(1);
			} catch (JDOMException ex) {
				logger.fatal(ex);
				System.exit(1);
			} catch (MalformedURLException ex) {
				logger.fatal("Unable to parse file ".concat(indexfile));
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
	
	private void filterCaches(List<Element> caches, Byte version, String indexdir) {
		logger.debug("filter caches for version "+version);
		if (version != 0 && version != 3) {
			throw new IllegalArgumentException("unsopported file format version "+version);			
		}
		for (Element cacheElement: caches) {
			Cache cache = new Cache(cacheElement, version, indexdir);
			if (cache.isAdditional()) {
				logger.debug(cache.getId()+" sorted out. Reason: is additional waypoint");
			} else if (cache.isIncomplete()) {
				logger.warn(cache.getId()+" sorted out. Reason: incomplete information");
			} else if (cache.isFound() && isGcCache(cache)) {
				foundCaches.add(cache);
			} else {
				logger.warn(cache.getId()+" sorted out for unknown reason");
			}
		}
	}
	
	private Boolean isGcCache(Cache cache) {
		return ! CacheType.isAddiWpt(cache.getType().byteValue()) && 
			cache.getType() != CacheType.CW_TYPE_CUSTOM &&
			cache.getId().startsWith("GC");
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
			lat=center.getAttributeValue("lat").replace(',', '.');
			lon=center.getAttributeValue("lon").replace(',', '.');
			homeCoordinates.setLatLon(Double.parseDouble(lat), Double.parseDouble(lon));
			logger.debug(homeCoordinates);
		} catch (Exception ex) {
			logger.warn("unable to determine home coordinates. Using N 00 00.000 E 00 00.000");
			logger.debug(ex,ex);
			homeCoordinates.setLatLon(0, 0);
		}
	}

	public LatLonPoint getHomeCoordinates() {
		return homeCoordinates;
	}
}

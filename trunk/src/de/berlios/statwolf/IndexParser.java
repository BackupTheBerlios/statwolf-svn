package de.berlios.statwolf;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.bbn.openmap.LatLonPoint;

import de.cachewolf.CacheType;

public class IndexParser {

	private List<Cache> foundCaches = new ArrayList<Cache>();
	private static LatLonPoint homeCoordinates;
	private static Logger logger = Logger.getLogger(IndexParser.class);
	private Integer readCounter = 0;

	// jdom uses old syntax for compatibility
	@SuppressWarnings("unchecked")
	public IndexParser(final String indexdir) {
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
					logger.debug("version information is: " + versionElement.getAttributes().toString());
					idxVersion = Byte.parseByte(versionElement.getAttributeValue("value"));
					logger.debug("index version is: " + idxVersion);
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
	
	private void filterCaches(final List<Element> caches, final Byte version, final String indexdir) {
		logger.debug("filter caches for version " + version);
		if (version != 0 && version != 3) {
			throw new IllegalArgumentException("unsopported file format version " + version);			
		}
		logger.info("reading cache data");
		for (Element cacheElement : caches) {
			readCounter++;
			if (readCounter % 50 == 0) {
				System.out.print(".");
			}
			Cache cache = new Cache(cacheElement, version, indexdir);
			if (cache.isAdditional() || cache.getType() == CacheType.CW_TYPE_CUSTOM) {
				logger.debug(cache.getId() + " sorted out. Reason: is additional/custom waypoint");
			} else if (cache.isIncomplete()) {
				logger.warn(cache.getId() + " sorted out. Reason: incomplete information");
			} else if (cache.isFound() && isGcCache(cache)) {
				foundCaches.add(cache);
			} else {
				logger.warn(cache.getId() + " sorted out for unknown reason");
			}
		}
		System.out.println();
	}
	
	private Boolean isGcCache(final Cache cache) {
		return !CacheType.isAddiWpt(cache.getType().byteValue()) 
			&& cache.getType() != CacheType.CW_TYPE_CUSTOM 
			&& cache.getId().startsWith("GC");
	}
	
	public final List<Cache> getFoundCaches () {
		return foundCaches;
	}

	public final void setHomeCoordinates(final List<Element> centers) {
		homeCoordinates = new LatLonPoint();
		try {
			Element center = centers.get(0);
			String lat;
			String lon;
			lat = center.getAttributeValue("lat").replace(',', '.');
			lon = center.getAttributeValue("lon").replace(',', '.');
			homeCoordinates.setLatLon(Double.parseDouble(lat), Double.parseDouble(lon));
			logger.debug(homeCoordinates);
		} catch (Exception ex) {
			logger.warn("unable to determine home coordinates. Using N 00 00.000 E 00 00.000");
			logger.debug(ex, ex);
			homeCoordinates.setLatLon(0, 0);
		}
	}

	public final LatLonPoint getHomeCoordinates() {
		return homeCoordinates;
	}
}

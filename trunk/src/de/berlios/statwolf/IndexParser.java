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
	private static Logger logger = Logger.getLogger(HTMLOutput.class);

	// jdom uses old syntax for compatibility
	@SuppressWarnings("unchecked")
	public IndexParser(String indexfile) {
		
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
				filterCaches(document.getRootElement().getChildren("CACHE"), idxVersion);
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
	
	private void filterCaches(List<Element> caches, Byte version) {
		logger.debug("filter caches for version "+version);
		if (version != 0 && version != 3) {
			throw new IllegalArgumentException("unsopported file format version "+version);			
		}
		for (Element cacheElement: caches) {
			Cache cache = new Cache(cacheElement, version);
			if (cache.isIncomplete()) {
				logger.warn(cache.getId()+" sorted out. Reason: incomplete information");
			} else if (cache.isFound() && isGcCache(cache)) {
				foundCaches.add(cache);
			} else {
				logger.debug(cache.getId()+" sorted out.");
			}
		}
	}
	
	private Boolean isGcCache(Cache cache) {
		return ! CacheType.isAddiWpt(cache.getType().byteValue()) && cache.getType() != CacheType.CW_TYPE_CUSTOM;
	}
	
//	private void filterCaches0(List<Element> caches) {
//		for (Element cache: caches) {
//			String cacheId = cache.getAttributeValue("wayp");
//			String cacheFound = cache.getAttributeValue("found");
//			String isincomplete = cache.getAttributeValue("is_INCOMPLETE");
//			
//			// TODO: check for valid cache type
//			if ( (cacheId.indexOf("GC") == 0) && cacheFound.equals("true") && isincomplete.equals("false")) {
//				logger.debug("includeing ".concat(cacheId));
//				Cache fc = new Cache(new Element(), 0);
//
//				try {
//					fc.setId(cache.getAttributeValue("wayp"));
//					fc.setFound(cache.getAttributeValue("status"));
//					fc.setArchived(cache.getAttributeValue("archived"));
//					fc.setHidden(cache.getAttributeValue("hidden"));
//					fc.setName(cache.getAttributeValue("name"));
//					fc.setOwner(cache.getAttributeValue("owner"));
//					fc.setLat(cache.getAttributeValue("lat").replace(',', '.'));
//					fc.setLon(cache.getAttributeValue("lon").replace(',', '.'));
//					fc.setOnline(cache.getAttributeValue("online"));
//					
//					fc.setDifficulty(cache.getAttributeValue("dif").replace(',', '.'));
//					fc.setTerrain(cache.getAttributeValue("terrain").replace(',', '.'));
//					fc.setSize(cache.getAttributeValue("size"));
//					fc.setType(cache.getAttributeValue("type"));
//					foundCaches.add(fc);
//				} catch (Exception ex) {
//					logger.error(cacheId.concat(" sortout. reason: ").concat(ex.toString()));
//				}
//			} else {
//				logger.debug("ecluding ".concat(cacheId));
//			}
//		}
//	}
	
//	private void filterCaches3(List<Element> caches) {
//
//		for (Element cache: caches) {
//			String cacheId = cache.getAttributeValue("wayp");
//			CwBoolFields boolFields = new CwBoolFields(Long.parseLong(cache.getAttributeValue("boolFields")));
//			
//			if (cacheId.indexOf("GC") == 0) {
//				logger.debug("includeing ".concat(cacheId));
//				try {
//					if (boolFields.isFound) {
//						CwByteFields byteFields = new CwByteFields(Long.parseLong(cache.getAttributeValue("byteFields")));
//						
//						Cache fc = new Cache();
//						fc.setId(cache.getAttributeValue("wayp"));
//						fc.setFound(cache.getAttributeValue("status"));
//						fc.setHidden(cache.getAttributeValue("hidden"));
//						fc.setName(cache.getAttributeValue("name"));
//						fc.setOwner(cache.getAttributeValue("owner"));
//						fc.setLat(cache.getAttributeValue("lat").replace(',', '.'));
//						fc.setLon(cache.getAttributeValue("lon").replace(',', '.'));
//						fc.setOnline(boolFields.isAvailable.toString());
//						Float diff = byteFields.difficulty/10F;
//						fc.setDifficulty(diff.toString());
//						Float terr = byteFields.terrain/10F;
//						fc.setTerrain(terr.toString());
//						fc.setType(getVersion2Type(byteFields.cacheType));
//						logger.info(fc.getId()+" "+fc.getType());
//						foundCaches.add(fc);
//					}
//				} catch (Exception ex) {
//					logger.error(cacheId.concat(" sortout. reason: ").concat(ex.toString()));
//				}
//			} else {
//				logger.debug("ecluding ".concat(cacheId));
//			}
//		}
//		
//	}
	
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
	
//	private String getVersion2Type(Integer type) {
//		if (type > 127 ) { type = type -128;}
//		switch (type) {
//			case 100: return Constants.WHEREIGO.toString();
//			case 101: return Constants.MEGAEVENT.toString();
//			
//			default: return type.toString();
//		}
//	}
//	
//	private String mapVersion2SizeToString(Integer size) {
//		String ret = "unknown";
//		switch (size) {
//			case 1: ; break;
//			case 2: ; break;
//		}
//		return ret;
//	}

}

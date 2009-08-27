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

	private final transient List<Cache> foundCaches;
	private static LatLonPoint homeCoordinates;
	private static final Logger LOGGER = Logger.getLogger(IndexParser.class);
	private transient Integer readCounter = 0;

	// jdom uses old syntax for compatibility
	@SuppressWarnings("unchecked")
	public IndexParser(final String indexdir) {
		final String indexfile = indexdir.concat("index.xml");
		Byte idxVersion = null;

		final SAXBuilder parser = new SAXBuilder();
		foundCaches = new ArrayList<Cache>();

		if (indexfile == null) {
			LOGGER.fatal("index file not set. please check preferences.properties");
			System.exit(1);
		} else {
			try {
				final Document document = parser.build(indexfile);
				if (document.getRootElement().getChildren("VERSION").size() > 0) {
					idxVersion = 1;
					final Element versionElement = (Element) document.getRootElement().getChildren("VERSION").iterator().next();
					LOGGER.debug("version information is: " + versionElement.getAttributes().toString());
					idxVersion = Byte.parseByte(versionElement.getAttributeValue("value"));
					LOGGER.debug("index version is: " + idxVersion);
				} else {
					idxVersion = 0;
				}
				filterCaches(document.getRootElement().getChildren("CACHE"), idxVersion, indexdir);
				setHomeCoordinates(document.getRootElement().getChildren("CENTRE"));
			} catch (FileNotFoundException ex) {
				LOGGER.fatal("index file not found: ".concat(indexfile));
				System.exit(1);
			} catch (JDOMException ex) {
				LOGGER.fatal(ex);
				System.exit(1);
			} catch (MalformedURLException ex) {
				LOGGER.fatal("Unable to parse file ".concat(indexfile));
				System.exit(1);
			} catch (IOException ex) {
				LOGGER.fatal(ex);
				System.exit(1);
			}
		}
		
		if (foundCaches.isEmpty()) {
			LOGGER.error("no found caches in index file");
			System.exit(0);
		}
	}

	private void filterCaches(final List<Element> caches, final Byte version, final String indexdir) {
		LOGGER.debug("filter caches for version " + version);

		if (version != 0 && version != 3) {
			throw new IllegalArgumentException(
					"unsopported file format version " + version);			
		}
		
		LOGGER.info("reading cache data");
		
		for (Element cacheElement : caches) {
			readCounter++;
			
			if (readCounter % 50 == 0) {
				System.out.print("."); // NOPMD by greis on 16.08.09 23:22
			}
			
			final Cache cache = new Cache(cacheElement, version, indexdir); // NOPMD by greis on 18.08.09 09:34
			
			if (cache.isIncomplete()) {
				LOGGER.warn(cache.getId() 
						+ " sorted out. Reason: incomplete information");
			} else if (cache.isFound() && isGcCache(cache)) {
				foundCaches.add(cache);
			} else {
				LOGGER.warn(cache.getId() 
						+ " sorted out for unknown reason (probably additional or custom waypoint)");
			}
		}
		System.out.println(); // NOPMD by greis on 16.08.09 23:23
	}

	/**
	 * check if cache object really is a cache.
	 * 
	 * @param cache
	 *            cache to be checked
	 * @return true if cache is really a cache - not an additional or custom
	 *         waypoint and its ID starts with GC
	 */
	private Boolean isGcCache(final Cache cache) {
		return !cache.isAdditional()
			&& cache.getType() != CacheType.CW_TYPE_CUSTOM 
			&& cache.getId().startsWith("GC");
	}
	
	/** return the lit of found caches. */
	public final List<Cache> getFoundCaches() {
		return foundCaches;
	}

	/**
	 * extract the home coordinates. coordinates are taken from the
	 * <code>&lt;CENTER&gt;</code> tags of the CacheWolf profile. if there is
	 * more than one center tag only use the first one. in case coordinates can
	 * not be extracted fall back to N 0 E 0.
	 * 
	 * @param centers
	 *            list of <code>&lt;CENTER&gt;</code> elements of CW profile as
	 *            extracted by SAX
	 */
	public final void setHomeCoordinates(final List<Element> centers) {
		homeCoordinates = new LatLonPoint();
		try {
			final Element center = centers.get(0);
			String lat;
			String lon;
			lat = center.getAttributeValue("lat").replace(',', '.');
			lon = center.getAttributeValue("lon").replace(',', '.');
			homeCoordinates.setLatLon(Double.parseDouble(lat), 
					Double.parseDouble(lon));
			LOGGER.debug(homeCoordinates);
		} catch (Exception ex) {
			LOGGER.warn("unable to determine home coordinates. "
					+ "Using N 00 00.000 E 00 00.000");
			LOGGER.debug(ex, ex);
			homeCoordinates.setLatLon(0, 0);
		}
	}

	public final LatLonPoint getHomeCoordinates() {
		return homeCoordinates;
	}
}

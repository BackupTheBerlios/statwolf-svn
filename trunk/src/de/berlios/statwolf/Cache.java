package de.berlios.statwolf;

// OK

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.jdom.Element;

import de.cachewolf.CacheSize;
import de.cachewolf.CacheTerrDiff;
import de.cachewolf.CacheType;
//commented out since it is not currently used - leave for future reference
//import org.apache.log4j.Logger;

/**
 * this class will hold the details of the waypoints.<br>
 * if you use some of the available code analysis tools the will complain that
 * this is a data class with too many fields. but since it in fact is a data
 * class ...
 */
public class Cache {
	
	/** name of the cache waypoint. */
	private transient String name;
	/** name of the waypoint owner. */
	private transient String owner;
	/** waypoint latitude in decimal degrees WGS84. */
	private transient Float lat;
	/** waypoint longitude in decimal degrees WGS84. */
	private transient Float lon;
	/** date cache was hidden on. */
	private transient Calendar hidden;
	/** waypoint ID. */
	private transient String waypId;
	/** date cache way found. */
	private transient Calendar foundDate;
	/** cache type. */
	private transient Integer type;
	/** cache terrain rating. */
	private transient Integer terrain;
	/** cache difficulty rating. */
	private transient Integer difficulty;
	/** has the cache been found. */
	private transient Boolean found;
	/** size of cache container. */
	private transient Integer size;
	/** cache is archived. */
	private transient Boolean archived;
	/** additional details. */
	private transient CacheDetails details;
	/** waypoint information is incomplete. */
	private transient Boolean incomplete;
	/** cache is available. */
	private transient Boolean available;
	/** waypoint is additional waypoint of a main cache. */
	private transient Boolean additional = false;
	/** number of recent DNF logs. */
	private transient Integer dnfLogs;
// commented out since it is not currently used - leave for future reference
//	/** logger for debug output. */
//	private static final Logger LOGGER = Logger.getLogger(Cache.class);

//	/** position of filtered information in cwbool. */
//	private static final Long MASK_FILTER = 0x1L << 0;
	/** position of available information in cwbool. */
	private static final Long MASK_AVAILABLE = 0x1L << 1;
	/** position of archived information in cwbool. */
	private static final Long MASK_ARCHIVED = 0x1L << 2;
//	/** position of hasbugs information in cwbool. */
//	private static final Long MASK_BUGS = 0x1L << 3;
//	/** position of blacklist information in cwbool. */
//	private static final Long MASK_BLACK = 0x1L << 4;
//	/** position of owned status information in cwbool. */
//	private static final Long MASK_OWNED = 0x1L << 5;
	/** position of found information in cwbool. */
	private static final Long MASK_FOUND = 0x1L << 6;
//	/** position of new information in cwbool. */
//	private static final Long MASK_NEW = 0x1L << 7;
//	/** position of log update information in cwbool. */
//	private static final Long MASK_LOGUPDATE = 0x1L << 8;
//	/** position of description update information in cwbool. */
//	private static final Long MASK_UPDATE = 0x1L << 9;
//	/** position of html format information in cwbool. */
//	private static final Long MASK_HTML = 0x1L << 10;
	/** position of incomplete information in cwbool. */
	private static final Long MASK_INCOMPLETE = 0x1L << 11;

	/** byte offset for difficulty in cwbyte field. LSB = 0 */
	private static final Byte OFFSET_DIFF = 0;
	/** byte offset for terrain in cwbyte field. LSB = 0 */
	private static final Byte OFFSET_TERR = 1;
	/** byte offset for waypoint type in cwbyte field. LSB = 0 */
	private static final Byte OFFSET_TYPE = 2;
	/** byte offset for container size in cwbyte field. LSB = 0 */
	private static final Byte OFFSET_SIZE = 3;
	/** byte offset for number of DNF logs in cwbyte field. LSB = 0 */
	private static final Byte OFFSET_DNF = 4;

	/**
	 * Generate the Cache object by calling a suitable parser for the file
	 * format version.
	 * 
	 * @param cacheInfo
	 *            jdom element from parsing a CacheWOlf index file
	 * @param version
	 *            version of the CacheWolf index file
	 * @param indexdir
	 *            directory where the waypoint details are stored
	 */
	public Cache(final Element cacheInfo, final Byte version,
			final String indexdir) {
		if (version == 0) {
			readInfo0(cacheInfo, indexdir);
		} else if (version == 3) {
			readInfo3(cacheInfo, indexdir);
		} else {
			throw new IllegalArgumentException(
					"unsupported version " + version);
		}
	}
	
	/**
	 * Parser for a CacheWolf &lt;CACHE&gt; entry of 1.0 stable.
	 * 
	 * @param cacheInfo
	 *            jdom element from parsing a CacheWOlf index file
	 * @param indexdir
	 *            directory where the waypoint details are stored
	 */
	private void readInfo0(final Element cacheInfo, final String indexdir) {
		waypId = cacheInfo.getAttributeValue("wayp");
		name = cacheInfo.getAttributeValue("name");
		owner = cacheInfo.getAttributeValue("owner");

		lat = Float.parseFloat(cacheInfo.getAttributeValue("lat")
				.replace(',', '.'));
		lon = Float.parseFloat(cacheInfo.getAttributeValue("lon")
				.replace(',', '.'));

		hidden = getCalFromString(cacheInfo.getAttributeValue("hidden"));
		foundDate = getCalFromString(cacheInfo.getAttributeValue("status"));
		
		incomplete = Boolean.parseBoolean(
				cacheInfo.getAttributeValue("incomplete"));

		try {
			type = (int) CacheType
				.v1Converter(cacheInfo.getAttributeValue("type"));
		} catch (IllegalArgumentException ex) {
			incomplete = true;
		}

		if (CacheType.isAddiWpt(type.byteValue())) {
			additional = true;
		} else {

			try {
				difficulty = (int) CacheTerrDiff
					.v1Converter(cacheInfo.getAttributeValue("dif"));
			} catch (IllegalArgumentException ex) {
				incomplete = true;
			}

			try {
				terrain = (int) CacheTerrDiff
					.v1Converter(cacheInfo.getAttributeValue("terrain"));
			} catch (IllegalArgumentException ex) {
				incomplete = true;
			}

			try {
				size = (int) CacheSize
					.v1Converter(cacheInfo.getAttributeValue("size"));
			} catch (IllegalArgumentException ex) {
				incomplete = true;
			}

			details = new CacheDetails(waypId, indexdir);
		}

		archived = cacheInfo.getAttributeValue("size").equals("true");
		found = cacheInfo.getAttributeValue("found").equals("true");

	}
	
	/**
	 * Parser for a CacheWolf &lt;CACHE&gt; entry of 1.1 development.
	 * 
	 * @param cacheInfo
	 *            cacheInfo jdom element from parsing a CacheWOlf index file
	 * @param indexdir
	 *            directory where the waypoint details are stored
	 */
	private void readInfo3(final Element cacheInfo, final String indexdir) {
		final Long boolFields = Long.parseLong(cacheInfo
				.getAttributeValue("boolFields"));
		final Long byteFields = Long.parseLong(cacheInfo
				.getAttributeValue("byteFields"));
		
		waypId = cacheInfo.getAttributeValue("wayp");
		name = cacheInfo.getAttributeValue("name");
		owner = cacheInfo.getAttributeValue("owner");

		lat = Float.parseFloat(cacheInfo.getAttributeValue("lat")
				.replace(',', '.'));
		lon = Float.parseFloat(cacheInfo.getAttributeValue("lon")
				.replace(',', '.'));

		hidden = getCalFromString(cacheInfo.getAttributeValue("hidden"));
		foundDate = getCalFromString(cacheInfo.getAttributeValue("status"));

		difficulty = extractByteFromLong(byteFields, 
				OFFSET_DIFF);
		terrain = extractByteFromLong(byteFields, 
				OFFSET_TERR);
		type = extractByteFromLong(byteFields, 
				OFFSET_TYPE);
		size = extractByteFromLong(byteFields, 
				OFFSET_SIZE);
		dnfLogs = extractByteFromLong(byteFields, 
				OFFSET_DNF);
		archived = extractBoolFromLong(boolFields, 
				MASK_ARCHIVED);
		found = extractBoolFromLong(boolFields, 
				MASK_FOUND);
		incomplete = extractBoolFromLong(boolFields, 
				MASK_INCOMPLETE);
		available = extractBoolFromLong(boolFields,
				MASK_AVAILABLE);

		if (CacheType.isAddiWpt(type.byteValue())) { additional = true; }

		details = new CacheDetails(waypId, indexdir);
	}
	
	/**
	 * Generate a <code>Calendar</code> object from a string.
	 * 
	 * @param calString
	 *            ISO formatted date / time info
	 * @return Java <code>Calendar</code> object created by parsing
	 *         <code>calString</code>. If <code>calString</code> can not be
	 *         successfully parsed, retrun Jan 1 1970 midnight.
	 */
	private Calendar getCalFromString(final String calString) {
		final Calendar ret = new GregorianCalendar(1970, 0, 1, 0, 0, 0);
		final SimpleDateFormat isoDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm", Locale.getDefault());
		try {
			ret.setTime(isoDateFormat.parse(calString.concat(" 00:00")));
			ret.setTime(isoDateFormat.parse(calString));
		} catch (ParseException e) { 
			// the code will definitely throw an exception, but we don't care
			// any suggestions how to better handle this?
		}

		return ret;
	}

	/** @return get cache name. */
	public final String getName() { return name; }
	/** @return get cache owner. */
	public final String getOwner() { return owner; }
	/** @return get cache latitude in decimal degrees WGS84. */
	public final Float getLat() { return lat; }
	/** @return get cache longitude in decimal degrees WGS84. */
	public final Float getLon() { return lon; }
	/** @return get date cache was hidden on. */
	public final Calendar getHidden() { return hidden; }
	/** @return get cache short id. */
	public final String getId() { return waypId; }
	/** @return get date cache was found on. */
	public final Calendar getFoundDate() { return foundDate; }
	/** @return get cache found status. */
	public final Boolean isFound() { return found; }
	/** @return get cache type. */
	public final Integer getType() { return type; }
	/** @return get terrain rating. */
	public final Integer getTerrain() { return terrain; }
	/** @return get difficulty rating. */
	public final Integer getDifficulty() { return difficulty; }
	/** @return get cache size. */
	public final Integer getSize() { return size; }
	/** @return get cache archived state. */
	public final Boolean isArchived() { return archived; }
	/** @return "cache" is additional waypoint. */
	public final Boolean isAdditional() { return additional; }
	/** @return is cache information complete. */
	public final Boolean isIncomplete() { return incomplete; }
	/** @return get cache details */
	public final CacheDetails getDetails() { return details; }
	/** @return the dnfLogs */
	public final Integer getDnfLogs() { return dnfLogs; }
	/** @return the available */
	public final Boolean isAvailabel() { return available && !archived; }

	/** @return string representation of cache values for debugging purposes */
	@Override 
	public final String toString() {
		return String.format("ID: %s", waypId).concat(
				String.format(" Name: %s", name)).concat(
				String.format(" Owner: %s", owner)).concat(
				String.format(" Lat: %s", lat)).concat(
				String.format(" Lon: %s", lon)).concat(
				String.format(" Type: %s", type)).concat(
				String.format(" Terrain: %s", terrain)).concat(
				String.format(" Difficulty: %s", difficulty)).concat(
				String.format(" Size: %s", size)).concat(
				String.format(" Archived: %s", archived)).concat(
				String.format(" Hidden: %s", hidden.getTime())).concat(
				String.format(" Found: %s", foundDate.getTime()));
	}
	
	/**
	 * extract a single byte out of a given long value.
	 * @param longvalue long value from which a byte should be extracted
	 * @param offset offset of byte within long with LSB = 0 
	 * @return byte at given offset 
	 */
	private Integer extractByteFromLong(final Long longvalue, final Byte offset) {
		final Long mask = 0xFFL << ((long) offset * 0x8L);
		final Long tmpval = longvalue & mask;
		final Long ret = (tmpval >>> ((long) offset * 0x8L));
		return ret.intValue();
	}
	
	/**
	 * extract a value from the CacheWolf 1.1 boolean values field.
	 * @param value content of boolean values field
	 * @param bitmask bitmaks to extract
	 * @return true if <code>bitmask</code> is set in <code>value</code>, false otherwise 
	 */
	private boolean extractBoolFromLong(final Long value, final Long bitmask) {
		return (value & bitmask) > 0;
	}
}

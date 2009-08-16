package de.berlios.statwolf;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;
import org.jdom.Element;

import de.cachewolf.CacheSize;
import de.cachewolf.CacheTerrDiff;
import de.cachewolf.CacheType;

/**
 * this class will hold the details of the waypoints.
 * @author greis
 *
 */
public class Cache {
	
	/** name of the cache waypoint. */
	private String name;
	/** name of the waypoint owner. */
	private String owner;
	/** waypoint latitude in decimal degrees WGS84. */
	private Float lat;
	/** waypoint longitude in decimal degrees WGS84. */
	private Float lon;
	/** date cache was hidden on. */
	private Calendar hidden;
	/** waypoint ID. */
	private String id;
	/** date cache way found. */
	private Calendar foundDate;
	/** cache type. */
	private Integer type;
	/** cache terrain rating. */
	private Integer terrain;
	/** cache difficulty rating. */
	private Integer difficulty;
	/** has the cache been found. */
	private Boolean found;
	/** size of cache container. */
	private Integer size;
	/** cache is archived. */
	private Boolean archived;
	/** additional details. */
	private CacheDetails details;
	/** waypoint information is incomplete. */
	private Boolean incomplete = false;
	/** logger for debug output. */
	private static final Logger LOGGER = Logger.getLogger(Cache.class);
	/** waypoint is additional waypoint of a main cache. */
	private Boolean isadditional = false;
	/** CacheWolf compact storage format. */
	private Long byteFields;
	/** CacheWolf compact storage format. */
	private Long boolFields;
	/** number of recent DNF logs. */
	private Integer dnfLogs;
	
	/** byte offset for difficulty. */
	private static final Byte BYTEOFFSET_DIFF = 0;
	/** byte offset for terrain. */
	private static final Byte BYTEOFFSET_TERR = 1;
	/** byte offset for waypoint type. */
	private static final Byte BYTEOFFSET_TYPE = 2;
	/** byte offset for container size. */
	private static final Byte BYTEOFFSET_SIZE = 3;
	/** byte offset for number of DNF logs. */
	private static final Byte BYTEOFFSET_DNFLOGS = 4;
	
	private static final Long BITMASK_FILTER = 0x1L << 0L;
	private static final Long BITMASK_AVAILABLE = 0x1L << 1L;
	private static final Long BITMASK_ARCHIVED = 0x1L << 2L;
	private static final Long BITMASK_BUGS = 0x1L << 3L;
	private static final Long BITMASK_BLACK = 0x1L << 4L;
	private static final Long BITMASK_OWNED = 0x1L << 5L;
	private static final Long BITMASK_FOUND = 0x1L << 6L;
	private static final Long BITMASK_NEW = 0x1L << 7L;
	private static final Long BITMASK_LOGUPDATE = 0x1L << 8L;
	private static final Long BITMASK_UPDATE = 0x1L << 9L;
	private static final Long BITMASK_HTML = 0x1L << 10L;
	private static final Long BITMASK_INCOMPLETE = 0x1L << 11L;
	
	/**
	 * 
	 * @param cacheInfo
	 * @param version
	 * @param indexdir
	 */
	public Cache(final Element cacheInfo, final Byte version, final String indexdir) {
		if (version == 0) { readInfo0(cacheInfo, indexdir); }
		else if (version == 3) { readInfo3(cacheInfo, indexdir); }
		else { throw new IllegalArgumentException("unsupported version "+version); }
	}
	
	/**
	 * 
	 * @param cacheInfo
	 * @param indexdir
	 */
	private void readInfo0(final Element cacheInfo, final String indexdir) {
		id = cacheInfo.getAttributeValue("wayp");
		name = cacheInfo.getAttributeValue("name");
		owner = cacheInfo.getAttributeValue("owner");
		
		lat = Float.parseFloat(cacheInfo.getAttributeValue("lat").replace(',', '.'));
		lon = Float.parseFloat(cacheInfo.getAttributeValue("lon").replace(',', '.'));
		
		hidden = getCalFromString(cacheInfo.getAttributeValue("hidden"));
		foundDate = getCalFromString(cacheInfo.getAttributeValue("status"));
		
		try {
			type = (int) CacheType.v1Converter(cacheInfo.getAttributeValue("type"));
		} catch (IllegalArgumentException ex) {
			incomplete = true;
		}
		
		if (CacheType.isAddiWpt(type.byteValue())) {
			isadditional = true;
		} else {
	
			try {
				difficulty = (int) CacheTerrDiff.v1Converter(cacheInfo.getAttributeValue("dif"));
			} catch (IllegalArgumentException ex) {
				incomplete = true;
			}
			
			try {
				terrain = (int) CacheTerrDiff.v1Converter(cacheInfo.getAttributeValue("terrain"));
			} catch (IllegalArgumentException ex) {
				incomplete = true;
			}
			
			try {
				size = (int) CacheSize.v1Converter(cacheInfo.getAttributeValue("size"));
			} catch (IllegalArgumentException ex) {
				incomplete = true;
			}

			details = new CacheDetails(id, indexdir);
		}
		
		archived = 	cacheInfo.getAttributeValue("size").equals("true");	
		found = cacheInfo.getAttributeValue("found").equals("true");
		
	}
	
	/**
	 * 
	 * @param cacheInfo
	 * @param indexdir
	 */
	private void readInfo3(final Element cacheInfo, final String indexdir) {
		id = cacheInfo.getAttributeValue("wayp");
		name = cacheInfo.getAttributeValue("name");
		owner = cacheInfo.getAttributeValue("owner");
		
		lat = Float.parseFloat(cacheInfo.getAttributeValue("lat").replace(',', '.'));
		lon = Float.parseFloat(cacheInfo.getAttributeValue("lon").replace(',', '.'));
		
		hidden = getCalFromString(cacheInfo.getAttributeValue("hidden"));
		foundDate = getCalFromString(cacheInfo.getAttributeValue("status"));
		
		final CwBoolFields boolFields = new CwBoolFields(Long.parseLong(cacheInfo.getAttributeValue("boolFields")));
		byteFields = Long.parseLong(cacheInfo.getAttributeValue("byteFields"));
		
		difficulty = extractByteFromLong(byteFields, BYTEOFFSET_DIFF);
		terrain = extractByteFromLong(byteFields, BYTEOFFSET_TERR);
		type = extractByteFromLong(byteFields, BYTEOFFSET_TYPE);
		size = extractByteFromLong(byteFields, BYTEOFFSET_SIZE);
		dnfLogs = extractByteFromLong(byteFields, BYTEOFFSET_DNFLOGS);

		archived = boolFields.isArchived;
		found = boolFields.isFound;
		
		if (CacheType.isAddiWpt(type.byteValue())) {
			isadditional = true;
		} 
		
		details = new CacheDetails(id, indexdir);
	}
	
	/**
	 * 
	 * @param calString
	 * @return
	 */
	private Calendar getCalFromString(final String calString) {
		final Calendar ret = new GregorianCalendar(1970, 0, 1, 0, 0, 0);
		final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			ret.setTime(df.parse(calString.concat(" 00:00")));
			ret.setTime(df.parse(calString));
		} catch (ParseException e) {
			LOGGER.debug(e);
		}

		return ret;
	}

	/** get cache name. */
	public final String getName() { return name; }
	/** get cache owner. */
	public final String getOwner() { return owner; }
	/** get cache latitude in decimal degrees WGS84. */
	public final Float getLat() { return lat; }
	/** get cache longitude in decimal degrees WGS84. */
	public final Float getLon() { return lon; }
	/** get date cache was hidden on. */
	public final Calendar getHidden() { return hidden; }
	/** get cache short id. */
	public final String getId() { return id; }
	/** get date cache was found on. */
	public final Calendar getFoundDate() { return foundDate; }
	/** get cache found status */
	public final Boolean isFound() { return found; }
	/** get cache type */
	public final Integer getType() { return type; }
	/** get terrain rating */
	public final Integer getTerrain() { return terrain; }
	/** get difficulty rating */
	public final Integer getDifficulty() { return difficulty; }
	/** get cache size */
	public final Integer getSize() { return size; }
	/** get cache archived state */
	public final Boolean isArchived() { return archived; }
	/** "cache" is additional waypoint */
	public final Boolean isAdditional() { return isadditional; }
	/** is cache information complete */
	public final Boolean isIncomplete() { return incomplete; }
	/** 
	 * get cache details
	 * @see CacheDetails
	 */
	public final CacheDetails getDetails() { return details; }
	
	@Override public final String toString() {
 		return String.format("ID: %s", id)
 			.concat(String.format(" Name: %s", name))
			.concat(String.format(" Owner: %s", owner))
			.concat(String.format(" Lat: %s", lat))
			.concat(String.format(" Lon: %s", lon))
			.concat(String.format(" Type: %s", type))
			.concat(String.format(" Terrain: %s", terrain))
			.concat(String.format(" Difficulty: %s", difficulty))
			.concat(String.format(" Size: %s", size))
			.concat(String.format(" Archived: %s", archived))
			.concat(String.format(" Hidden: %s", hidden.getTime()))
			.concat(String.format(" Found: %s", foundDate.getTime()));
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
	 * check if a b
	 * @param value
	 * @param bitmask
	 * @return
	 */
	private boolean extractBoolFromLong(final Long value, final Long bitmask) {
		return (value & bitmask) > 0;
	}
	
//	public boolean extractBoolFromLong(Long value, Byte offset) {
//		isFiltered = ((fields & BITMASK_FILTER) > 0);
//		isAvailable = ((fields & BITMASK_AVAILABLE) > 0);
//		isArchived = ((fields & BITMASK_ARCHIVED) > 0);
//		hasBugs = ((fields & BITMASK_BUGS) > 0);
//		isBlack = ((fields & BITMASK_BLACK) > 0);
//		isOwned = ((fields & BITMASK_OWNED) > 0);
//		isFound = ((fields & BITMASK_FOUND) > 0);
//		isNew = ((fields & BITMASK_NEW) > 0);
//		hasLogUpdate = ((fields & BITMASK_LOGUPDATE) > 0);
//		isUpdated = ((fields & BITMASK_UPDATE) > 0);
//		isHtml = ( (fields & BITMASK_HTML) > 0);
//		isIncomplete = ( (fields & BITMASK_INCOMPLETE) > 0);
//	}
}

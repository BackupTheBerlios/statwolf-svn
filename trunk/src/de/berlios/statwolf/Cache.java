package de.berlios.statwolf;

import org.apache.log4j.*;
import java.util.*;
import java.text.*;
import org.jdom.*;

import de.cachewolf.CacheSize;
import de.cachewolf.CacheTerrDiff;
import de.cachewolf.CacheType;

/**
 * this class will hold the details of the waypoints found 
 * @author greis
 *
 */
public class Cache {
	
	private String name;
	private String owner;
	private Float lat;
	private Float lon;
	private Calendar hidden;
	private String id;
	private Calendar foundDate;
	private Integer type;
	private Integer terrain;
	private Integer difficulty;
	private Boolean found;
	private Integer size;
	private Boolean archived;
	private CacheDetails details;
	private Boolean incomplete = false;
	private static Logger logger = Logger.getLogger(Cache.class);
	private Boolean isadditional = false;
	
	/**
	 * 
	 * @param cacheInfo
	 * @param version
	 */
	public Cache(Element cacheInfo, Byte version, String indexdir) {
		if (version == 0) readInfo0(cacheInfo,indexdir);
		else if (version == 3) readInfo3(cacheInfo,indexdir);
		else throw new IllegalArgumentException("unsupported version "+version);
	}
	
	/**
	 * 
	 * @param cacheInfo
	 */
	private void readInfo0(Element cacheInfo, String indexdir) {
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
		
		isadditional = CacheType.isAddiWpt(type.byteValue());
		
		if (isadditional) {
			// skip next block
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
	 */
	private void readInfo3(Element cacheInfo, String indexdir) {
		id = cacheInfo.getAttributeValue("wayp");
		name = cacheInfo.getAttributeValue("name");
		owner = cacheInfo.getAttributeValue("owner");
		
		lat = Float.parseFloat(cacheInfo.getAttributeValue("lat").replace(',', '.'));
		lon = Float.parseFloat(cacheInfo.getAttributeValue("lon").replace(',', '.'));
		
		hidden = getCalFromString(cacheInfo.getAttributeValue("hidden"));
		foundDate = getCalFromString(cacheInfo.getAttributeValue("status"));
		
		CwBoolFields boolFields = new CwBoolFields(Long.parseLong(cacheInfo.getAttributeValue("boolFields")));
		CwByteFields byteFields = new CwByteFields(Long.parseLong(cacheInfo.getAttributeValue("byteFields")));

		archived = boolFields.isArchived;
		found = boolFields.isFound;
		
		type = byteFields.cacheType;
		
		if (CacheType.isAddiWpt(type.byteValue())) {
			// skip next block
		} else {
			size = byteFields.cacheSize;
			difficulty = byteFields.difficulty;
			terrain = byteFields.terrain;
			details = new CacheDetails(id, indexdir);
		}
	}
	
	/**
	 * 
	 * @param calString
	 * @return
	 */
	private Calendar getCalFromString(String calString) {
		Calendar ret = new GregorianCalendar(1970,1,1,0,0,0);
		SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );
		try {
			ret.setTime(df.parse(calString.concat(" 00:00")));
			ret.setTime(df.parse(calString));
		} catch (ParseException e) {
			logger.debug(e);
		}

		return ret;
	}

	/** get cache name */
	public String getName () { return name; }
	/** get cache owner */
	public String getOwner() { return owner; }
	/** get cache latitude */
	public Float getLat () { return lat; }
	/** get cache longitude */
	public Float getLon () { return lon; }
	/** get cache hidden date */
	public Calendar getHidden () { return hidden; }
	/** get cache id */
	public String getId () { return id; }
	/** get cache id (waypoint) */
	public Calendar getFoundDate () { return foundDate; }
	/** get cache found status */
	public Boolean isFound() { return found; }
	/** get cache type */
	public Integer getType () { return type; }
	/** get terrain rating */
	public Integer getTerrain () { return terrain; }
	/** get difficulty rating */
	public Integer getDifficulty () { return difficulty; }
	/** get cache size */
	public Integer getSize () { return size; }
	/** get cache archived state */
	public Boolean isArchived () { return archived; }
	/** "cache" is additional waypoint */
	public Boolean isAdditional() { return isadditional; }
	/** is cache information complete */
	public Boolean isIncomplete () { return incomplete; }
	/** 
	 * get cache details
	 * @see CacheDetails
	 */
	public CacheDetails getDetails() { return details; }
	
	@Override public String toString() {
		// TODO: make this handle null values
 		return String.format("ID: %s",id)
 			.concat(String.format(" Name: %s",name))
			.concat(String.format(" Owner: %s",owner))
			.concat(String.format(" Lat: %s",lat))
			.concat(String.format(" Lon: %s",lon))
			.concat(String.format(" Type: %s",type))
			.concat(String.format(" Terrain: %s",terrain))
			.concat(String.format(" Difficulty: %s",difficulty))
			.concat(String.format(" Size: %s",size))
			.concat(String.format(" Archived: %s",archived))
			.concat(String.format(" Hidden: %s",hidden.getTime()))
			.concat(String.format(" Found: %s",foundDate.getTime()));
	}
}

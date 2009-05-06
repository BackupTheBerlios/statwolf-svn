package de.berlios.statwolf;

import org.apache.log4j.*;
import java.util.*;
import java.text.*;

public class Cache {
	
	String name;
	String owner;
	Float lat;
	Float lon;
	Calendar hidden;
	String id;
	Calendar found;
	Integer type;
	Float terrain;
	Float difficulty;
	String size;
	Boolean online;
	Boolean archived;
	
	private static Logger logger = Logger.getLogger(Cache.class);
	
	public static enum Field {
		NAME, OWNER, LAT, LON, HIDDEN, ID, FOUND, TYPE, TERRAIN, DIFFICULTY, SIZE, ONLINE, ARCHIVED, UNKNOWN;
					
		public static Field toField (String str) {
			try {
				return valueOf(str.toUpperCase());
			} catch (Exception ex) {
				return UNKNOWN;
			}
		}
		
	}
	
	Object getValue(String str) {
		
		switch (Field.toField(str)) {
			case NAME : 
				return name;
			case OWNER: 
				return owner;
			case LAT:
				return lat;
			case LON:
				return lon;
			case HIDDEN:
				return hidden;
			case ID:
				return id;
			case FOUND:
				return found;
			case TYPE:
				return type;
			case TERRAIN:
				return terrain;
			case DIFFICULTY:
				return difficulty;
			case SIZE:
				return size;
			case ONLINE:
				return online;
			case ARCHIVED:
				return archived;
			default:
				return null;
		}
	}

	public Cache() {
	}

	public void setName (String name) {
		this.name = new String(name);
	}

	public String getName () {
		return name;
	}

	public void setOwner (String owner) {
		this.owner = owner;
	}

	public String getOwner() {
		return owner;
	}

	public void setLat (String lat) {
		this.lat = Float.parseFloat(lat.replace(',', '.'));
	}

	public Float getLat () {
		return lat;
	}

	public void setLon (String lon) {
		this.lon = Float.parseFloat(lon.replace(',', '.'));
	}

	public Float getLon () {
		return lon;
	}

	public void setHidden (String hidden) {
		SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd" );
		this.hidden = new GregorianCalendar();
		
		try {
			this.hidden.setTime(df.parse("1970-01-01"));
			this.hidden.setTime(df.parse(hidden));
		} catch (ParseException e) {
			logger.debug(e);
		}
	}

	public Calendar getHidden () {
		return hidden;
	}

	public void setId (String id) {
		this.id = id;
	}

	public String getId () {
		return id;
	}

	public void setFound (String found) {
		SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );
		this.found = new GregorianCalendar();
				
		try {
			this.found.setTime(df.parse("1970-01-01 00:00"));
			this.found.setTime(df.parse(found.concat(" 00:00")));
			this.found.setTime(df.parse(found));
		} catch (ParseException e) {
			logger.debug(e);
		}
	}

	public Calendar getFound () {
		return found;
	}

	public void setType (String type) {
		this.type = Integer.parseInt(type);
	}
	
	public Integer getType () {
		return type;
	}

	public void setTerrain (String terr) {
		this.terrain = Float.parseFloat(terr.replace(',', '.'));
	}

	public Float getTerrain () {
		return terrain;
	}

	public void setDifficulty (String diff) {
		this.difficulty = Float.parseFloat(diff.replace(',', '.'));
	}

	public Float getDifficulty () {
		return difficulty;
	}
	
	public void setSize (String size) {
		this.size = size;
	}
	
	public String getSize () {
		return size;
	}
	
	public void setOnline (String online) {
		this.online = Boolean.parseBoolean(online);
	}
	
	public Boolean getOnline () {
		return online;
	}
	
	public void setArchived (String archived) {
		this.archived = Boolean.parseBoolean(archived);		
	}
	
	public Boolean getArchived () {
		return archived;
	}
	
	@Override public String toString() {
		return "ID: ".concat(id)
			.concat(" Name: ").concat(name)
			.concat(" Owner: ").concat(owner)
			.concat(" Lat: ").concat(lat.toString())
			.concat(" Lon: ").concat(lon.toString())
			.concat(" Id: ").concat(id)
			.concat(" Type: ").concat(type.toString())
			.concat(" Terrain: ").concat(terrain.toString())
			.concat(" Difficulty: ").concat(difficulty.toString())
			.concat(" Size: ").concat(size)
			.concat(" Online: ").concat(online.toString())
			.concat(" Archived: ").concat(archived.toString())
			.concat(" Hidden: ").concat(hidden.getTime().toString())
			.concat(" Found: ").concat(hidden.getTime().toString());
	}

}

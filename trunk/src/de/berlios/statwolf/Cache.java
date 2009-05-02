package de.berlios.statwolf;

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

	void setName (String name) {
		this.name = new String(name);
	}

	String getName () {
		return name;
	}

	void setOwner (String owner) {
		this.owner = owner;
	}

	String getOwner() {
		return owner;
	}

	void setLat (String lat) {
		this.lat = Float.parseFloat(lat.replace(',', '.'));
	}

	Float getLat () {
		return lat;
	}

	void setLon (String lon) {
		this.lon = Float.parseFloat(lon.replace(',', '.'));
	}

	Float getLon () {
		return lon;
	}

	void setHidden (String hidden) {
		SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd" );
		this.hidden = new GregorianCalendar();
		
		try {
			this.hidden.setTime(df.parse("1970-01-01 00:00"));
			this.hidden.setTime(df.parse(hidden.concat(" 00:00")));
			this.hidden.setTime(df.parse(hidden));
		} catch (ParseException e) {
			//ignore
		}
	}

	Calendar getHidden () {
		return hidden;
	}

	void setId (String id) {
		this.id = id;
	}

	String getId () {
		return id;
	}

	void setFound (String found) {
		SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );
		this.found = new GregorianCalendar();
				
		try {
			this.found.setTime(df.parse("1970-01-01 00:00"));
			this.found.setTime(df.parse(found.concat(" 00:00")));
			this.found.setTime(df.parse(found));
		} catch (ParseException e) {
			// ignore
		}
	}

	Calendar getFound () {
		return found;
	}

	void setType (String type) {
		this.type = Integer.parseInt(type);
	}
	
	Integer getType () {
		return type;
	}

	void setTerrain (String terr) {
		this.terrain = Float.parseFloat(terr.replace(',', '.'));
	}

	Float getTerrain () {
		return terrain;
	}

	void setDifficulty (String diff) {
		this.difficulty = Float.parseFloat(diff.replace(',', '.'));
	}

	Float getDifficulty () {
		return difficulty;
	}
	
	void setSize (String size) {
		this.size = size;
	}
	
	String getSize () {
		return size;
	}
	
	void setOnline (String online) {
		this.online = Boolean.parseBoolean(online);
	}
	
	Boolean getOnline () {
		return online;
	}
	
	void setArchived (String archived) {
		this.archived = Boolean.parseBoolean(archived);		
	}
	
	Boolean getArchived () {
		return archived;
	}
	
	@Override public String toString() {
		DateFormat df = DateFormat.getDateInstance();
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
			.concat(" Hidden: ").concat(df.format(hidden))
			.concat(" Found: ").concat(df.format(found));
	}

}

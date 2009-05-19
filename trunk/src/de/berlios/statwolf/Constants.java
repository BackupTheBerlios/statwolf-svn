package de.berlios.statwolf;

import java.util.*;

import de.cachewolf.CacheSize;
import de.cachewolf.CacheType;

public class Constants {
	
	public static final String CHARTBASE="http://chart.apis.google.com/chart?";
	
	public static final Float MAXHORIZONTALBARLENGTH = 150.0F;
	
	public static final Integer[] CONTAINERS = {CacheSize.CW_SIZE_MICRO, CacheSize.CW_SIZE_SMALL, CacheSize.CW_SIZE_REGULAR, CacheSize.CW_SIZE_LARGE, CacheSize.CW_SIZE_NOTCHOSEN, CacheSize.CW_SIZE_OTHER, CacheSize.CW_SIZE_VIRTUAL};
	
	public static final Integer[] TERRDIFF = {10,15,20,25,30,35,40,45,50};
	
	public static final Integer[] ZEROMONTHS = {0,0,0,0,0,0,0,0,0,0,0,0};
	public static final Integer[] ZERODAYS = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	
	public static final HashMap<Integer,String> TYPEIMAGES = new  HashMap<Integer,String>();
	static {
		TYPEIMAGES.put((int)CacheType.CW_TYPE_TRADITIONAL, "http://tinyurl.com/c5yxbx");
		TYPEIMAGES.put((int)CacheType.CW_TYPE_MULTI, "http://tinyurl.com/cacawh");
		TYPEIMAGES.put((int)CacheType.CW_TYPE_VIRTUAL, "http://tinyurl.com/cfkj2p");
		TYPEIMAGES.put((int)CacheType.CW_TYPE_LETTERBOX, "http://tinyurl.com/df4avk");
		TYPEIMAGES.put((int)CacheType.CW_TYPE_EVENT, "http://tinyurl.com/de2mhp");
		TYPEIMAGES.put((int)CacheType.CW_TYPE_UNKNOWN, "http://tinyurl.com/c535qa");
		TYPEIMAGES.put((int)CacheType.CW_TYPE_WEBCAM, "http://tinyurl.com/cmxedx");
		//TYPEIMAGES.put(LOCATIONLESS, "http://tinyurl.com/dxnjgm");
		TYPEIMAGES.put((int)CacheType.CW_TYPE_LOCATIONLESS, "http://tinyurl.com/cjagc6");
		TYPEIMAGES.put((int)CacheType.CW_TYPE_EARTH, "http://tinyurl.com/d8hkw3");
		TYPEIMAGES.put((int)CacheType.CW_TYPE_MEGA_EVENT, "http://tinyurl.com/c3q3yg");
		TYPEIMAGES.put((int)CacheType.CW_TYPE_WHEREIGO, "http://tinyurl.com/cs9ssk");
		TYPEIMAGES.put((int)CacheType.CW_TYPE_CITO, "http://tinyurl.com/da9hmm");
		TYPEIMAGES.put((int)CacheType.CW_TYPE_APE, "http://tinyurl.com/cwjpne");
		TYPEIMAGES.put((int)CacheType.CW_TYPE_MAZE, "http://tinyurl.com/cqlobv");
	}
	public static final HashMap<Integer,String> SIZEIMAGES = new HashMap<Integer,String>();
	static {
		SIZEIMAGES.put((int) CacheSize.CW_SIZE_VIRTUAL, "http://tinyurl.com/dyc87k");
		SIZEIMAGES.put((int) CacheSize.CW_SIZE_MICRO, "http://tinyurl.com/c4roz8");
		SIZEIMAGES.put((int) CacheSize.CW_SIZE_LARGE, "http://tinyurl.com/dnmpdt");
		SIZEIMAGES.put((int) CacheSize.CW_SIZE_NOTCHOSEN, "http://tinyurl.com/db5g4c");
		SIZEIMAGES.put((int) CacheSize.CW_SIZE_SMALL, "http://tinyurl.com/ct2sh5");
		SIZEIMAGES.put((int) CacheSize.CW_SIZE_REGULAR, "http://tinyurl.com/cgo99n");
		SIZEIMAGES.put((int) CacheSize.CW_SIZE_OTHER, "http://tinyurl.com/d2ocmk");
	}
	
	public static final HashMap<String,String> COUNTRY2ISO = new HashMap<String,String>();
	static {
		COUNTRY2ISO.put("Germany", "DE");
	}

	public static final String[] DIRECTIONS = {"n", "ne", "e", "se", "s", "sw", "w", "nw"};

	public static final Long BITEMASK_FILTER = 0x1L<<0;
	public static final Long BITMASK_AVAILABLE = 0x1L<<1;
	public static final Long BITMASK_ARCHIVED = 0x1L<<2;
	public static final Long BITMASK_BUGS = 0x1L<<3;
	public static final Long BITMASK_BLACK = 0x1L<<4;
	public static final Long BITMASK_OWNED = 0x1L<<5;
	public static final Long BITMASK_FOUND = 0x1L<<6;
	public static final Long BITMASK_NEW = 0x1L<<7;
	public static final Long BITMASK_LOGUPDATE = 0x1L<<8;
	public static final Long BITMASK_UPDATE = 0x1L<<9;
	public static final Long BITMASK_HTML = 0x1L<<10;
	public static final Long BITMASK_INCOMPLETE = 0x1L<<11;

	public static final Integer BYTEOFFSET_DIFF = 0;
	public static final Integer BYTEOFFSET_TERR = 1;
	public static final Integer BYTEOFFSET_TYPE = 2;
	public static final Integer BYTEOFFSET_SIZE = 3;
	public static final Integer BYTEOFFSET_DNFLOGS = 4;
}

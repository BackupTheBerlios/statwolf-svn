package de.berlios.statwolf;

import java.util.*;

public class Constants {
	
	public static final String CHARTBASE="http://chart.apis.google.com/chart?";
	
	public static final Float MAXHORIZONTALBARLENGTH = 150.0F;
	
	public static final String[] CONTAINERS = {"Micro", "Small", "Regular", "Large", "Not chosen", "Other", "Virtual"};
	
	public static final Float[] TERRDIFF = { 1.0F, 1.5F, 2.0F, 2.5F, 3.0F, 3.5F, 4.0F, 4.5F, 5.0F };
	
	public static final Integer[] ZEROMONTHS = {0,0,0,0,0,0,0,0,0,0,0,0};
	public static final Integer[] ZERODAYS = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	
	public static final Integer TRADITIONAL = 2;
	public static final Integer MULTI = 3;
	public static final Integer VIRTUAL = 4;
	public static final Integer LETTERBOX = 5;
	public static final Integer EVENT = 6;
	public static final Integer MYSTERY = 8;
	public static final Integer APE = 9;
	public static final Integer WEBCAM = 11;
	public static final Integer LOCATIONLESS = 12;
	public static final Integer CITO = 13;
	public static final Integer EARTH = 137;
	public static final Integer MEGAEVENT = 453;
	public static final Integer WHEREIGO = 1858;
	// TODO: get real value
	public static final Integer MAZE = 99999;
	
	public static final HashMap<Integer,String> TYPEIMAGES = new  HashMap<Integer,String>();
	static {
		TYPEIMAGES.put(TRADITIONAL, "http://tinyurl.com/c5yxbx");
		TYPEIMAGES.put(MULTI, "http://tinyurl.com/cacawh");
		TYPEIMAGES.put(VIRTUAL, "http://tinyurl.com/cfkj2p");
		TYPEIMAGES.put(LETTERBOX, "http://tinyurl.com/df4avk");
		TYPEIMAGES.put(EVENT, "http://tinyurl.com/de2mhp");
		TYPEIMAGES.put(MYSTERY, "http://tinyurl.com/c535qa");
		TYPEIMAGES.put(WEBCAM, "http://tinyurl.com/cmxedx");
		//TYPEIMAGES.put(LOCATIONLESS, "http://tinyurl.com/dxnjgm");
		TYPEIMAGES.put(LOCATIONLESS, "http://tinyurl.com/cjagc6");
		TYPEIMAGES.put(EARTH, "http://tinyurl.com/d8hkw3");
		TYPEIMAGES.put(MEGAEVENT, "http://tinyurl.com/c3q3yg");
		TYPEIMAGES.put(WHEREIGO, "http://tinyurl.com/cs9ssk");
		TYPEIMAGES.put(CITO, "http://tinyurl.com/da9hmm");
		TYPEIMAGES.put(APE, "http://tinyurl.com/cwjpne");
		TYPEIMAGES.put(MAZE, "http://tinyurl.com/cqlobv");
	}
	public static final HashMap<String,String> SIZEIMAGES = new HashMap<String,String>();
	static {
		SIZEIMAGES.put("Virtual", "http://tinyurl.com/dyc87k");
		SIZEIMAGES.put("Micro", "http://tinyurl.com/c4roz8");
		SIZEIMAGES.put("Large", "http://tinyurl.com/dnmpdt");
		SIZEIMAGES.put("Not chosen", "http://tinyurl.com/db5g4c");
		SIZEIMAGES.put("Small", "http://tinyurl.com/ct2sh5");
		SIZEIMAGES.put("Regular", "http://tinyurl.com/cgo99n");
		SIZEIMAGES.put("Other", "http://tinyurl.com/d2ocmk");
	}
	
	public static final HashMap<String,String> COUNTRY2ISO = new HashMap<String,String>();
	static {
		COUNTRY2ISO.put("Germany", "DE");
	}

	public static final String[] DIRECTIONS = {"n", "ne", "e", "se", "s", "sw", "w", "nw"};
}

package de.berlios.statwolf;

// OK

import java.util.HashMap;
import java.util.Map;

import de.cachewolf.CacheSize;
import de.cachewolf.CacheType;

/** predefined constants for use with statistics generation and HTML output. */
public final class Constants {

	/** base URL of google chart API. */
	public static final String CHARTBASE = "http://chart.apis.google.com/chart?";

	/** maximum length of a horizontal bar chart in pixel. */
	public static final Float MAXHORBARLENGTH = 150.0F;

	/** cast back the byte values from the imported CacheSize to integers. */
	public static final Integer[] CONTAINERS = {
		(int) CacheSize.CW_SIZE_MICRO, 
		(int) CacheSize.CW_SIZE_SMALL, 
		(int) CacheSize.CW_SIZE_REGULAR, 
		(int) CacheSize.CW_SIZE_LARGE, 
		(int) CacheSize.CW_SIZE_NOTCHOSEN, 
		(int) CacheSize.CW_SIZE_OTHER, 
		(int) CacheSize.CW_SIZE_VIRTUAL};

	/** possible values for terrain or difficulty rating of a cache. */
	public static final Integer[] TERRDIFF = 
		{10, 15, 20, 25, 30, 35, 40, 45, 50};

	/** 12 zeros to initialize the months of a year. */
	public static final Integer[] ZEROMONTHS = 
		{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

	/** 31 zeros to initialize days of a month. */
	public static final Integer[] ZERODAYS = 
		{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

	/** short URLs for cache type images at gc.com site. image for locless is 
	 * loaded from an external site, since the image at gc.com is wrong. */
	public static final Map<Integer,String> TYPEIMAGES = new  HashMap<Integer,String>();
	static {
		TYPEIMAGES.put((int) CacheType.CW_TYPE_TRADITIONAL, 
				"http://tinyurl.com/c5yxbx");
		TYPEIMAGES.put((int) CacheType.CW_TYPE_MULTI, 
				"http://tinyurl.com/cacawh");
		TYPEIMAGES.put((int) CacheType.CW_TYPE_VIRTUAL, 
				"http://tinyurl.com/cfkj2p");
		TYPEIMAGES.put((int) CacheType.CW_TYPE_LETTERBOX, 
				"http://tinyurl.com/df4avk");
		TYPEIMAGES.put((int) CacheType.CW_TYPE_EVENT, 
				"http://tinyurl.com/de2mhp");
		TYPEIMAGES.put((int) CacheType.CW_TYPE_UNKNOWN, 
				"http://tinyurl.com/c535qa");
		TYPEIMAGES.put((int) CacheType.CW_TYPE_WEBCAM, 
				"http://tinyurl.com/cmxedx");
		//TYPEIMAGES.put(LOCATIONLESS, "http://tinyurl.com/dxnjgm");
		TYPEIMAGES.put((int) CacheType.CW_TYPE_LOCATIONLESS, 
				"http://tinyurl.com/cjagc6");
		TYPEIMAGES.put((int) CacheType.CW_TYPE_EARTH, 
				"http://tinyurl.com/d8hkw3");
		TYPEIMAGES.put((int) CacheType.CW_TYPE_MEGA_EVENT, 
				"http://tinyurl.com/c3q3yg");
		TYPEIMAGES.put((int) CacheType.CW_TYPE_WHEREIGO, 
				"http://tinyurl.com/cs9ssk");
		TYPEIMAGES.put((int) CacheType.CW_TYPE_CITO, 
				"http://tinyurl.com/da9hmm");
		TYPEIMAGES.put((int) CacheType.CW_TYPE_APE, 
				"http://tinyurl.com/cwjpne");
		TYPEIMAGES.put((int) CacheType.CW_TYPE_MAZE, 
				"http://tinyurl.com/cqlobv");
	}

	/** short URLs for cache size icons at gc.com site. */
	public static final Map < Integer, String >SIZEIMAGES = new HashMap <Integer, String>();
	static {
		SIZEIMAGES.put((int) CacheSize.CW_SIZE_VIRTUAL, 
				"http://tinyurl.com/dyc87k");
		SIZEIMAGES.put((int) CacheSize.CW_SIZE_MICRO, 
				"http://tinyurl.com/c4roz8");
		SIZEIMAGES.put((int) CacheSize.CW_SIZE_LARGE, 
				"http://tinyurl.com/dnmpdt");
		SIZEIMAGES.put((int) CacheSize.CW_SIZE_NOTCHOSEN, 
				"http://tinyurl.com/db5g4c");
		SIZEIMAGES.put((int) CacheSize.CW_SIZE_SMALL, 
				"http://tinyurl.com/ct2sh5");
		SIZEIMAGES.put((int) CacheSize.CW_SIZE_REGULAR, 
				"http://tinyurl.com/cgo99n");
		SIZEIMAGES.put((int) CacheSize.CW_SIZE_OTHER, 
				"http://tinyurl.com/d2ocmk");
	}

	/** mapping of country names as used by gc.com to ISO abbreviations. */
	public static final Map < String, String > GCCOUNTRY2ISO = new HashMap <String, String>();
	static {
		GCCOUNTRY2ISO.put("Afghanistan", "AF");                        
		GCCOUNTRY2ISO.put("Aland Islands", "AX");                             
		GCCOUNTRY2ISO.put("Albania ", "AL");                                  
		GCCOUNTRY2ISO.put("Algeria", "DZ");                                   
		GCCOUNTRY2ISO.put("Am erican Samoa ", "AS");                           
		GCCOUNTRY2ISO.put("Andorra", "AD");                                   
		GCCOUNTRY2ISO.put("Angola", "AO");                                    
		GCCOUNTRY2ISO.put("Anguilla ", "AI");                                 
		GCCOUNTRY2ISO.put("Antarctica", "AQ");                                
		GCCOUNTRY2ISO.put("Antigua and Barbuda", "AG");                       
		GCCOUNTRY2ISO.put("Argentina", "AR");                                 
		GCCOUNTRY2ISO.put("Armenia", "AM");                                   
		GCCOUNTRY2ISO.put("Aruba", "AW");                                     
		GCCOUNTRY2ISO.put("Australia", "AU");                                 
		GCCOUNTRY2ISO.put("Austria", "AT");                                   
		GCCOUNTRY2ISO.put("Azerbaijan", "AZ");                                
		GCCOUNTRY2ISO.put("Bahamas", "BS");                                   
		GCCOUNTRY2ISO.put("Bahrain", "BH");                                   
		GCCOUNTRY2ISO.put("Bangladesh", "BD");                                
		GCCOUNTRY2ISO.put("Barbados", "BB");                                  
		GCCOUNTRY2ISO.put("Belarus", "BY");                                   
		GCCOUNTRY2ISO.put("Belgium", "BE");                                   
		GCCOUNTRY2ISO.put("Belize", "BZ");                                    
		GCCOUNTRY2ISO.put("Benin", "BJ");                                     
		GCCOUNTRY2ISO.put("Bermuda", "BM");                                   
		GCCOUNTRY2ISO.put("Bhutan", "BT");                                    
		GCCOUNTRY2ISO.put("Bolivia", "BO");                                   
		GCCOUNTRY2ISO.put("Bonaire", "AN");                                   
		GCCOUNTRY2ISO.put("Bosnia and Herzegovina", "BA");                    
		GCCOUNTRY2ISO.put("Botswana", "BW");                                  
		GCCOUNTRY2ISO.put("Bouvet Island", "BV");                             
		GCCOUNTRY2ISO.put("Brazil", "BR");                                    
		GCCOUNTRY2ISO.put("British Indian Ocean Territories ", "IO");         
		GCCOUNTRY2ISO.put("British Virgin Islands", "??");                    
		GCCOUNTRY2ISO.put("Brunei", "BN");                                    
		GCCOUNTRY2ISO.put("Bulgaria", "BG");                                  
		GCCOUNTRY2ISO.put("Burkina Faso ", "BF");                             
		GCCOUNTRY2ISO.put("Burundi", "BI");                                   
		GCCOUNTRY2ISO.put("Burxina", "??");                                   
		GCCOUNTRY2ISO.put("Cambodia", "KH");                                  
		GCCOUNTRY2ISO.put("Cameroon", "CM");                                  
		GCCOUNTRY2ISO.put("Canada", "CA");                                    
		GCCOUNTRY2ISO.put("Cape Verde ", "CV");                               
		GCCOUNTRY2ISO.put("Cayman Islands", "KY");                            
		GCCOUNTRY2ISO.put("Central African Republic", "CF");                  
		GCCOUNTRY2ISO.put("Chad", "TD");                                      
		GCCOUNTRY2ISO.put("Chile", "CL");                                     
		GCCOUNTRY2ISO.put("China", "CN");                                     
		GCCOUNTRY2ISO.put("Christmas Island ", "CX");                         
		GCCOUNTRY2ISO.put("Cocos (Keeling) Islands ", "CC");                  
		GCCOUNTRY2ISO.put("Colombia", "CO");                                  
		GCCOUNTRY2ISO.put("Comoros", "KM");                                   
		GCCOUNTRY2ISO.put("Congo", "CG");                                     
		GCCOUNTRY2ISO.put("Cook Islands", "CK");                              
		GCCOUNTRY2ISO.put("Costa Rica", "CR");                                
		GCCOUNTRY2ISO.put("Croatia", "HR");                                   
		GCCOUNTRY2ISO.put("Cuba", "CU");                                      
		GCCOUNTRY2ISO.put("Curacao", "AN");                                   
		GCCOUNTRY2ISO.put("Cyprus", "CY");                                    
		GCCOUNTRY2ISO.put("Czech Republic", "CZ");                            
		GCCOUNTRY2ISO.put("Democratic Republic of the Congo ", "CD");         
		GCCOUNTRY2ISO.put("Denmark", "DK");                                   
		GCCOUNTRY2ISO.put("Djibouti", "DJ");                                  
		GCCOUNTRY2ISO.put("Dominica", "DM");                                  
		GCCOUNTRY2ISO.put("Dominican Republic", "DO");                        
		GCCOUNTRY2ISO.put("East Timor", "TL");                                
		GCCOUNTRY2ISO.put("Ecuador", "EC");                                   
		GCCOUNTRY2ISO.put("Egypt", "EG");                                     
		GCCOUNTRY2ISO.put("El Salvador", "SV");
		GCCOUNTRY2ISO.put("Equatorial Guinea", "GQ");                         
		GCCOUNTRY2ISO.put("Eritrea ", "ER");                                  
		GCCOUNTRY2ISO.put("Estonia", "EE");                                   
		GCCOUNTRY2ISO.put("Ethiopia", "ET");                                  
		GCCOUNTRY2ISO.put("Falkland Islands", "FK");                          
		GCCOUNTRY2ISO.put("Faroe Islands ", "FO");                            
		GCCOUNTRY2ISO.put("Fiji", "FJ");                                      
		GCCOUNTRY2ISO.put("Finland", "FI");                                   
		GCCOUNTRY2ISO.put("France", "FR");                                    
		GCCOUNTRY2ISO.put("French Guiana", "GF");                             
		GCCOUNTRY2ISO.put("French Polynesia", "PF");                          
		GCCOUNTRY2ISO.put("French Southern Territories", "TF");               
		GCCOUNTRY2ISO.put("Gabon", "GA");                                     
		GCCOUNTRY2ISO.put("Gambia", "GM");                                    
		GCCOUNTRY2ISO.put("Georgia", "GE");                                   
		GCCOUNTRY2ISO.put("Germany", "DE");                                   
		GCCOUNTRY2ISO.put("Ghana", "GH");                                     
		GCCOUNTRY2ISO.put("Gibraltar", "GI");                                 
		GCCOUNTRY2ISO.put("Greece", "GR");                                    
		GCCOUNTRY2ISO.put("Greenland", "GL");                                 
		GCCOUNTRY2ISO.put("Grenada", "GD");                                   
		GCCOUNTRY2ISO.put("Guadeloupe", "GP");                                
		GCCOUNTRY2ISO.put("Guam", "GU");                                      
		GCCOUNTRY2ISO.put("Guatemala", "GT");                                 
		GCCOUNTRY2ISO.put("Guernsey", "GG");                                  
		GCCOUNTRY2ISO.put("Guinea ", "GN");                                   
		GCCOUNTRY2ISO.put("Guinea-Bissau", "GW");
		GCCOUNTRY2ISO.put("Guyana ", "GY");                                   
		GCCOUNTRY2ISO.put("Haiti", "HT");                                     
		GCCOUNTRY2ISO.put("Heard Island And Mcdonald Islands ", "HM");        
		GCCOUNTRY2ISO.put("Honduras", "HN");                                  
		GCCOUNTRY2ISO.put("Hong Kong", "HK");                                 
		GCCOUNTRY2ISO.put("Hungary", "HU");                                   
		GCCOUNTRY2ISO.put("Iceland", "IS");                                   
		GCCOUNTRY2ISO.put("India", "IN");                                     
		GCCOUNTRY2ISO.put("Indonesia", "ID");                                 
		GCCOUNTRY2ISO.put("Iran", "IR");                                      
		GCCOUNTRY2ISO.put("Iraq", "IQ");                                      
		GCCOUNTRY2ISO.put("Ireland", "IE");                                   
		GCCOUNTRY2ISO.put("Isle of Man", "IM");                               
		GCCOUNTRY2ISO.put("Israel", "IL");                                    
		GCCOUNTRY2ISO.put("Italy", "IT");                                     
		GCCOUNTRY2ISO.put("Ivory Coast", "CI");                               
		GCCOUNTRY2ISO.put("Jamaica", "JM");                                   
		GCCOUNTRY2ISO.put("Japan", "JP");                                     
		GCCOUNTRY2ISO.put("Jersey", "JE");                                    
		GCCOUNTRY2ISO.put("Jordan", "JO");                                    
		GCCOUNTRY2ISO.put("Kazakhstan", "KZ");                                
		GCCOUNTRY2ISO.put("Kenya", "KE");                                     
		GCCOUNTRY2ISO.put("Kiribati", "KI");                                  
		GCCOUNTRY2ISO.put("Kuwait", "KW");                                    
		GCCOUNTRY2ISO.put("Kyrgyzstan", "KG");                                
		GCCOUNTRY2ISO.put("Laos", "LA");                                      
		GCCOUNTRY2ISO.put("Latvia", "LV");                                    
		GCCOUNTRY2ISO.put("Lebanon", "LB");                                   
		GCCOUNTRY2ISO.put("Lesotho", "LS");                                   
		GCCOUNTRY2ISO.put("Liberia", "LR");                                   
		GCCOUNTRY2ISO.put("Libya", "LY");                                     
		GCCOUNTRY2ISO.put("Liechtenstein", "LI");                             
		GCCOUNTRY2ISO.put("Lithuania", "LI");                                 
		GCCOUNTRY2ISO.put("Luxembourg", "LU");                                
		GCCOUNTRY2ISO.put("Macau", "MO");                                     
		GCCOUNTRY2ISO.put("Macedonia", "MK");                                 
		GCCOUNTRY2ISO.put("Madagascar", "MG");                                
		GCCOUNTRY2ISO.put("Malawi", "MW");                                    
		GCCOUNTRY2ISO.put("Malaysia", "MY");                                  
		GCCOUNTRY2ISO.put("Maldives", "MV");                                  
		GCCOUNTRY2ISO.put("Mali", "ML");                                      
		GCCOUNTRY2ISO.put("Malta", "MT");                                     
		GCCOUNTRY2ISO.put("Marshall Islands ", "MH");                         
		GCCOUNTRY2ISO.put("Martinique", "MQ");                                
		GCCOUNTRY2ISO.put("Mauritania", "MR");                                
		GCCOUNTRY2ISO.put("Mauritius", "MU");                                 
		GCCOUNTRY2ISO.put("Mayotte ", "YT");                                  
		GCCOUNTRY2ISO.put("Mexico", "MX");                                    
		GCCOUNTRY2ISO.put("Micronesia", "FM");                                
		GCCOUNTRY2ISO.put("Moldovia", "MD");                                  
		GCCOUNTRY2ISO.put("Monaco", "MC");                                    
		GCCOUNTRY2ISO.put("Mongolia", "MN");                                  
		GCCOUNTRY2ISO.put("Montenegro", "ME");                                
		GCCOUNTRY2ISO.put("Montserrat", "MS");                                
		GCCOUNTRY2ISO.put("Morocco", "MA");                                   
		GCCOUNTRY2ISO.put("Mozambique", "MZ");                                
		GCCOUNTRY2ISO.put("Myanmar", "MM");                                   
		GCCOUNTRY2ISO.put("Namibia", "NA");                                   
		GCCOUNTRY2ISO.put("Nauru", "NR");                                     
		GCCOUNTRY2ISO.put("Nepal", "NP");                                     
		GCCOUNTRY2ISO.put("Netherlands", "NL");                               
		GCCOUNTRY2ISO.put("Netherlands Antilles", "AN");                      
		GCCOUNTRY2ISO.put("Nevis and St Kitts", "KN");                        
		GCCOUNTRY2ISO.put("New Caledonia", "NC");                             
		GCCOUNTRY2ISO.put("New Zealand", "NZ");                               
		GCCOUNTRY2ISO.put("Nicaragua", "NI");                                 
		GCCOUNTRY2ISO.put("Niger", "NE");                                     
		GCCOUNTRY2ISO.put("Nigeria", "NG");                                   
		GCCOUNTRY2ISO.put("Niue", "NU");                                      
		GCCOUNTRY2ISO.put("Norfolk Island ", "NF");                           
		GCCOUNTRY2ISO.put("Northern Mariana Islands ", "MP");                 
		GCCOUNTRY2ISO.put("Norway", "NO");                                    
		GCCOUNTRY2ISO.put("Oman", "OM");                                      
		GCCOUNTRY2ISO.put("Pakistan", "PK");                                  
		GCCOUNTRY2ISO.put("Palau", "PW");                                     
		GCCOUNTRY2ISO.put("Panama", "PA");                                    
		GCCOUNTRY2ISO.put("Papua New Guinea", "PG");                          
		GCCOUNTRY2ISO.put("Paraguay ", "PY");                                 
		GCCOUNTRY2ISO.put("People Den Rep Yemen", "YE");                      
		GCCOUNTRY2ISO.put("Peru", "PE");                                      
		GCCOUNTRY2ISO.put("Philippines", "PH");                               
		GCCOUNTRY2ISO.put("Pitcairn Islands", "PN");                          
		GCCOUNTRY2ISO.put("Poland", "PL");                                    
		GCCOUNTRY2ISO.put("Portugal", "PT");                                  
		GCCOUNTRY2ISO.put("Puerto Rico", "PR");                               
		GCCOUNTRY2ISO.put("Qatar", "QA");                                     
		GCCOUNTRY2ISO.put("Reunion", "RE");                                   
		GCCOUNTRY2ISO.put("Romania", "RO");                                   
		GCCOUNTRY2ISO.put("Russia", "RU");                                    
		GCCOUNTRY2ISO.put("Rwanda", "RW");                                    
		GCCOUNTRY2ISO.put("Saint Helena ", "SH");                             
		GCCOUNTRY2ISO.put("Saint Kitts and Nevis", "KN");                     
		GCCOUNTRY2ISO.put("Saint Lucia ", "LC");                              
		GCCOUNTRY2ISO.put("Samoa", "WS");                                     
		GCCOUNTRY2ISO.put("San Marino", "SM");                                
		GCCOUNTRY2ISO.put("Sao Tome and Principe", "");                     
		GCCOUNTRY2ISO.put("Saudi Arabia", "ST");                              
		GCCOUNTRY2ISO.put("Senegal", "SN");                                   
		GCCOUNTRY2ISO.put("Serbia and Montenegro", "RS");                     
		GCCOUNTRY2ISO.put("Seychelles", "SC");                                
		GCCOUNTRY2ISO.put("Sierra Leone ", "SL");                             
		GCCOUNTRY2ISO.put("Singapore", "SG");                                 
		GCCOUNTRY2ISO.put("Slovakia", "SK");                                  
		GCCOUNTRY2ISO.put("Slovenia", "SI");                                  
		GCCOUNTRY2ISO.put("Solomon Islands", "SB");                           
		GCCOUNTRY2ISO.put("Somalia", "SO");                                   
		GCCOUNTRY2ISO.put("South Africa", "ZA");                              
		GCCOUNTRY2ISO.put("South Georgia and Sandwich Islands", "GS");        
		GCCOUNTRY2ISO.put("South Korea", "KR");                               
		GCCOUNTRY2ISO.put("Spain", "ES");
		GCCOUNTRY2ISO.put("Sri Lanka", "LK");
		GCCOUNTRY2ISO.put("St Barthelemy", "BL");
		GCCOUNTRY2ISO.put("St Eustatius", "AN");
		GCCOUNTRY2ISO.put("St Kitts", "KN");
		GCCOUNTRY2ISO.put("St Pierre Miquelon", "PM");
		GCCOUNTRY2ISO.put("St Vince Grenadines", "VC");
		GCCOUNTRY2ISO.put("St. Martin", "MF");
		GCCOUNTRY2ISO.put("Sudan", "SD");
		GCCOUNTRY2ISO.put("Suriname", "SR");
		GCCOUNTRY2ISO.put("Svalbard and Jan Mayen ", "SJ");
		GCCOUNTRY2ISO.put("Swaziland", "SZ");
		GCCOUNTRY2ISO.put("Sweden", "SE");
		GCCOUNTRY2ISO.put("Switzerland", "CH");
		GCCOUNTRY2ISO.put("Syria", "SY");
		GCCOUNTRY2ISO.put("Taiwan", "TW");
		GCCOUNTRY2ISO.put("Tajikistan", "TJ");
		GCCOUNTRY2ISO.put("Tanzania", "TZ");
		GCCOUNTRY2ISO.put("Thailand", "TH");
		GCCOUNTRY2ISO.put("Togo", "TG");
		GCCOUNTRY2ISO.put("Tokelau ", "TK");
		GCCOUNTRY2ISO.put("Tonga", "");
		GCCOUNTRY2ISO.put("Trinidad and Tobago ", "TT");
		GCCOUNTRY2ISO.put("Tunisia", "TN");
		GCCOUNTRY2ISO.put("Turkey", "TR");
		GCCOUNTRY2ISO.put("Turkmenistan", "TM ");
		GCCOUNTRY2ISO.put("Turks and Caicos Islands ", "TC");
		GCCOUNTRY2ISO.put("Tuvalu", "TV");
		GCCOUNTRY2ISO.put("Uganda", "UG");
		GCCOUNTRY2ISO.put("Ukraine", "UA");
		GCCOUNTRY2ISO.put("United Arab Emirates", "AE");
		GCCOUNTRY2ISO.put("United Kingdom", "GB");
		GCCOUNTRY2ISO.put("United States", "US");
		GCCOUNTRY2ISO.put("Uruguay", "UY");
		GCCOUNTRY2ISO.put("US Minor Outlying Islands ", "UM");
		GCCOUNTRY2ISO.put("US Virgin Islands", "VI");
		GCCOUNTRY2ISO.put("Uzbekistan", "UZ");
		GCCOUNTRY2ISO.put("Vanuatu", "VU");
		GCCOUNTRY2ISO.put("Vatican City State", "VA");
		GCCOUNTRY2ISO.put("Venezuela", "VE");
		GCCOUNTRY2ISO.put("Vietnam", "VN");
		GCCOUNTRY2ISO.put("Wallis And Futuna Islands ", "WF");
		GCCOUNTRY2ISO.put("Western Sahara ", "EH");
		GCCOUNTRY2ISO.put("Yemen", "YE");
		GCCOUNTRY2ISO.put("Zaire", "CG");
		GCCOUNTRY2ISO.put("Zambia", "ZM");
		GCCOUNTRY2ISO.put("Zimbabwe", "ZW");
	}

	/** cardinal points. */
	public static final String[] DIRECTIONS = 
		{"n", "ne", "e", "se", "s", "sw", "w", "nw"};

	/** thou shallst not instantiate this object. */
	private Constants() { }

	/** you must not clone this object. 
	 * @throws CloneNotSupportedException if called
	 * @return will never return but throw an exception */
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
}

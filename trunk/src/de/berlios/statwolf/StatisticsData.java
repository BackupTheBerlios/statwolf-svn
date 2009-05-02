package de.berlios.statwolf;

import org.apache.log4j.*;
import java.util.*;
import com.bbn.openmap.*;
import com.bbn.openmap.proj.*;

public class StatisticsData {

	private List<Cache> foundCaches;
	private HashMap<Integer, Integer> cachesByType;
	private HashMap<String, Integer> cachesByContainer;
	private HashMap<Boolean, Integer> cachesArchived;
	private HashMap<Boolean, Integer> cachesOnline;
	private HashMap<Integer, Integer> cachesByDayOfWeek;
	private HashMap<Integer, HashMap<Integer, Integer>> matrixMonthDay;
	private HashMap<Integer, HashMap<Integer, Integer>> matrixYearMonth;
	private HashMap<Float, HashMap<Float, Integer>> matrixTerrDiff;
	private HashMap<String,Integer> cachesByOwner;
	private Integer totalCaches;
	private HashMap<String, Cache> mostXxxCache = new HashMap<String, Cache>();
	private String distUnit;
	private Calendar firstCachingDay;
	private TreeMap<Integer, Integer> distanceFromHome;
	private TreeMap<Integer, Cache> milestones = new TreeMap<Integer, Cache>();
	private LatLonPoint homeCoordinates;
	private TreeMap <Calendar, ArrayList<Cache>> cachesByDate;
	private Boolean excludeLocless;
	private Boolean excludeVirtual;
	private LatLonPoint cacheMedian = new LatLonPoint();
	private Integer cacheingDays;
	private Integer findsLast365Days = 0;
	private HashMap<String,Integer> cachesByDirection;
	private HashMap<Integer,Integer> findsByMonth;
	
	private static Logger logger = Logger.getLogger(HTMLOutput.class);

	public StatisticsData(List<Cache> foundCaches, LatLonPoint homeCoordinates, Properties prefs) {
		distUnit = prefs.getProperty("distunit", "km");
		excludeLocless = Boolean.parseBoolean(prefs.getProperty("excludelocless", "false"));
		excludeVirtual = Boolean.parseBoolean(prefs.getProperty("excludevirtual", "false"));

		updateStatistics(foundCaches, homeCoordinates);
	}

	public void updateStatistics(List<Cache> foundCaches, LatLonPoint homeCoordinates) {
		this.foundCaches = foundCaches;
		this.homeCoordinates = homeCoordinates;

		initVars();
		setDataMatrix();

	}
	
	/*
	 * GET methods
	 */

	public HashMap<Float, Integer> getCachesByTerrain() {
		HashMap<Float, Integer> cbd = new HashMap<Float, Integer>();
		Integer counter;
		Integer temp;
		for (int terr = 0; terr < Constants.TERRDIFF.length; terr++) {
			counter = 0;
			for (int diff = 0; diff < Constants.TERRDIFF.length; diff++) {
				temp = matrixTerrDiff.get(Constants.TERRDIFF[terr]).get(Constants.TERRDIFF[diff]);
				counter = counter + temp;
			}
			cbd.put(Constants.TERRDIFF[terr], counter);
		}
		return cbd;
	}

	public HashMap<Float, Integer> getCachesByDifficulty() {
		HashMap<Float, Integer> cbd = new HashMap<Float, Integer>();
		Integer counter;
		Integer temp;
		for (Float diff : Constants.TERRDIFF) {
			counter = 0;
			for (Float terr : Constants.TERRDIFF) {
				temp = matrixTerrDiff.get(terr).get(diff);
				counter = counter + temp;
			}
			cbd.put(diff, counter);
		}
		return cbd;
	}

	public HashMap<Integer, Integer> getCachesByType() {
		return cachesByType;
	}

	public HashMap<String, Integer> getCachesByContainer() {
		return cachesByContainer;
	}

	public HashMap<Boolean, Integer> getCachesArchived() {
		return cachesArchived;
	}

	public TreeMap<Integer, Cache> getMilestones() {
		return milestones;
	}

	public HashMap<Boolean, Integer> getCachesOnline() {
		return cachesOnline;
	}

	public HashMap<Integer, Integer> getCachesByDayOfWeek() {
		return cachesByDayOfWeek;
	}

	public HashMap<Integer, HashMap<Integer, Integer>> getMatrixMonthDay() {
		return matrixMonthDay;
	}

	public HashMap<Integer, HashMap<Integer, Integer>> getMatrixYearMonth() {
		return matrixYearMonth;
	}

	public HashMap<Float, HashMap<Float, Integer>> getMatrixTerrDiff() {
		return matrixTerrDiff;
	}

	public Integer getTotalCaches() {
		return totalCaches;
	}

	public HashMap<String, Cache> getMostXxxCache() {
		return mostXxxCache;
	}

	public Calendar getFirstCachingDay() {
		return firstCachingDay;
	}

	public TreeMap<Integer, Integer> getDistanceFromHome() {
		return distanceFromHome;
	}
	
	public TreeMap<Calendar, ArrayList<Cache>> getCachesByDate () {
		return cachesByDate;
	}

	public Integer getDaysSinceFirstFind() {
		Calendar firstday = getCleanDate((Calendar) firstCachingDay.clone());
		Calendar today = getCleanDate(Calendar.getInstance());
		Integer diff = 0;
		while (firstday.before(today)) {
			diff++;
			firstday.add(Calendar.DAY_OF_MONTH, 1);
		}
		return diff;
	}
	
	public HashMap<String,Integer> getCachesByOwner() {
		return cachesByOwner;
	}
	
	public LatLonPoint getCacheMedian() {
		return cacheMedian;
	}
	
	public TreeSet<UserNumber> getCachesByOwnerSorted() {
		TreeSet<UserNumber> cachesByOwnerSorted = new TreeSet<UserNumber>();
		for (String key: cachesByOwner.keySet()) {
			cachesByOwnerSorted.add(new UserNumber(key,cachesByOwner.get(key)));
		}
		return cachesByOwnerSorted;
	}
	
	public Calendar getCleanDate(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}
	
	public Integer getFindsLast365Days() {
		return findsLast365Days;
	}
	
	public Integer getCachingDaysLastYear() {
		Integer cachingDays = 0;
		Calendar today = getCleanDate(Calendar.getInstance());
		Calendar lastYear = (Calendar) today.clone();
		lastYear.add(Calendar.DAY_OF_MONTH, -365);
		for (Calendar cacheDay: cachesByDate.keySet()) {
			if (cacheDay.after(lastYear)) {
				cachingDays++;
			}
		}
		return cachingDays;
	}
	
	public Integer getCachingDays() {
		return cacheingDays;
	}
	
	public HashMap<String,Integer> getCachesByDirection() {
		return cachesByDirection;
	}
	
	public HashMap<Integer,Integer> getFindsByMonth() {
		return findsByMonth;
	}
	
	/*
	 * SET methods
	 */

	private void setDataMatrix() {

		matrixYearMonth = new HashMap<Integer, HashMap<Integer, Integer>>();

		totalCaches = foundCaches.size();
		cachesByContainer = new HashMap<String, Integer>();
		cachesByType = new HashMap<Integer, Integer>();
		cachesByType = new HashMap<Integer, Integer>();
		cachesOnline = new HashMap<Boolean, Integer>();
		cachesArchived = new HashMap<Boolean, Integer>();
		cachesByDate = new TreeMap<Calendar, ArrayList<Cache>>(); 
		cachesByOwner = new HashMap<String,Integer>();
		Calendar today = getCleanDate(Calendar.getInstance());
		Calendar lastYear = (Calendar) today.clone();
		//TODO: check for leap years
		lastYear.add(Calendar.DAY_OF_MONTH, -365);

		// TODO: put most of this into separate sub routines
		for (Cache cache: foundCaches ) {

			Integer foundDOW = cache.found.get(Calendar.DAY_OF_WEEK);

			// matrix year month
			if (!matrixYearMonth.containsKey(cache.found.get(Calendar.YEAR))) {
				HashMap<Integer, Integer> temp = new HashMap<Integer, Integer>();
				for (int month = 0; month < 12; month++) {
					temp.put(month, 0);
				}
				matrixYearMonth.put(cache.found.get(Calendar.YEAR), temp);
			}

			{
				HashMap<Integer, Integer> temp = matrixYearMonth.get(cache.found.get(Calendar.YEAR));
				if (!temp.containsKey(cache.found.get(Calendar.MONTH))) {
					temp.put(cache.found.get(Calendar.MONTH), 1);
				} else {
					temp.put(cache.found.get(Calendar.MONTH), temp.get(cache.found.get(Calendar.MONTH)) + 1);
				}
				matrixYearMonth.put(cache.found.get(Calendar.YEAR), temp);
				temp = null;
			}

			// day of week
			cachesByDayOfWeek
					.put(foundDOW, cachesByDayOfWeek.get(foundDOW) + 1);

			// size
			if (cachesByContainer.containsKey(cache.size)) {
				cachesByContainer.put(cache.size, cachesByContainer.get(cache.size) + 1);
			} else {
				cachesByContainer.put(cache.size, 1);
			}

			// type
			if (cachesByType.containsKey(cache.type)) {
				cachesByType.put(cache.type, cachesByType.get(cache.type) + 1);
			} else {
				cachesByType.put(cache.type, 1);
			}

			// online
			if (cachesOnline.containsKey(cache.online)) {
				cachesOnline.put(cache.online, cachesOnline.get(cache.online) + 1);
			} else {
				cachesOnline.put(cache.online, 1);
			}

			// archived
			if (cachesArchived.containsKey(cache.archived)) {
				cachesArchived.put(cache.archived, cachesArchived.get(cache.archived) + 1);
			} else {
				cachesArchived.put(cache.archived, 1);
			}

			// matrixMonthDay
			{
				HashMap<Integer, Integer> temp = new HashMap<Integer, Integer>();
				temp = matrixMonthDay.get(cache.found.get(Calendar.MONTH));
				temp.put(cache.found.get(Calendar.DAY_OF_MONTH), temp.get(cache.found.get(Calendar.DAY_OF_MONTH)) + 1);
				matrixMonthDay.put(cache.found.get(Calendar.MONTH), temp);
			}
			
			findsByMonth.put(cache.found.get(Calendar.MONTH), findsByMonth.get(cache.found.get(Calendar.MONTH))+1);
			
			// matrixTerrDiff
			{
				HashMap<Float, Integer> temp = matrixTerrDiff.get(cache.terrain);
				temp.put(cache.difficulty, temp.get(cache.difficulty) + 1);
				matrixTerrDiff.put(cache.terrain, temp);
			}
			
			// distance from home
			if ( includeCache(cache) ) {
				Float dist;
				if (distUnit.equals("mi")) {
					dist = Length.MILE.fromRadians((homeCoordinates.distance(new LatLonPoint(cache.lat, cache.lon))));
				} else { 
					dist = Length.KM.fromRadians((homeCoordinates.distance(new LatLonPoint(cache.lat, cache.lon))));
				}
				if (dist <= 10) {
					distanceFromHome.put(10, distanceFromHome.get(10) + 1);
				} else if (dist <= 20) {
					distanceFromHome.put(20, distanceFromHome.get(20) + 1);
				} else if (dist <= 30) {
					distanceFromHome.put(30, distanceFromHome.get(30) + 1);
				} else if (dist <= 40) {
					distanceFromHome.put(40, distanceFromHome.get(40) + 1);
				} else if (dist <= 50) {
					distanceFromHome.put(50, distanceFromHome.get(50) + 1);
				} else if (dist <= 100) {
					distanceFromHome.put(100, distanceFromHome.get(100) + 1);
				} else if (dist <= 200) {
					distanceFromHome.put(200, distanceFromHome.get(200) + 1);
				} else if (dist <= 300) {
					distanceFromHome.put(300, distanceFromHome.get(300) + 1);
				} else if (dist <= 400) {
					distanceFromHome.put(400, distanceFromHome.get(400) + 1);
				} else if (dist <= 500) {
					distanceFromHome.put(500, distanceFromHome.get(500) + 1);
				} else if (dist <= 1000) {
					distanceFromHome.put(1000, distanceFromHome.get(1000) + 1);
				} else {
					distanceFromHome.put(9999, distanceFromHome.get(9999) + 1);
				}
			}
			
			// direction
			
			if (includeCache(cache) ) {
				String dir;
				Float az = homeCoordinates.azimuth(new LatLonPoint(cache.lat, cache.lon));
				
				if (az >= -7.0/8.0*Math.PI && az < -5.0/8.0*Math.PI) {
					dir = "sw";
				} else if (az >= -5.0/8.0*Math.PI && az < -3.0/8.0*Math.PI) {
					dir="w";
				} else if (az >= -3.0/8.0*Math.PI && az < -1.0/8.0*Math.PI) {
					dir="nw";
				} else if (az >= -1.0/8.0*Math.PI && az < 1.0/8.0*Math.PI) {
					dir="n";
				} else if (az >= 1.0/8.0*Math.PI && az < 3.0/8.0*Math.PI) {
					dir="ne";
				} else if (az >= 3.0/8.0*Math.PI && az < 5.0/8.0*Math.PI) {
					dir="e";
				} else if (az >= 5.0/8.0*Math.PI && az < 7.0/8.0*Math.PI) {
					dir="se";
				} else {
					dir="s";
				}
				cachesByDirection.put(dir, cachesByDirection.get(dir)+1);
			}

			// cache by date
			{
				Calendar tempdate = getCleanDate((Calendar) cache.found.clone());
				ArrayList<Cache> tempclist;
				if (! cachesByDate.containsKey(tempdate)) {
					tempclist = new ArrayList<Cache>();
				} else {
					tempclist = cachesByDate.get(tempdate);
				}
				tempclist.add(cache);
				cachesByDate.put(tempdate, tempclist);
				if (tempdate.after(lastYear)) {
					findsLast365Days++;
				}
			}
			cacheingDays = cachesByDate.size();
			
			// cache by owner
			if (! cachesByOwner.containsKey(cache.owner)) {
				cachesByOwner.put(cache.owner, 1);
			} else {
				cachesByOwner.put(cache.owner, cachesByOwner.get(cache.owner) + 1);
			}
		}

		List<Cache> excludedCaches = new ArrayList<Cache>(foundCaches);
		
		for (Cache c: foundCaches ) {
			if ( ! includeCache(c) ) {
				excludedCaches.remove(c);
			}
		}
		
		// most northern, eastern, western, southern cache & median
		Collections.sort(excludedCaches, new CompareCacheByLat());
		mostXxxCache.put("south",excludedCaches.get(0));
		mostXxxCache.put("north",excludedCaches.get(excludedCaches.size()-1));
		if (excludedCaches.size() % 2 == 0) {
			Integer index=excludedCaches.size()/2;
			Float lat = excludedCaches.get(index).lat;
			index--;
			lat = lat + excludedCaches.get(index).lat;
			cacheMedian.setLatitude(lat / 2.0F);
		} else {
			Integer index = (int) Math.floor(excludedCaches.size() / 2.0);
			cacheMedian.setLatitude(excludedCaches.get(index).lat);
		}

		Collections.sort(excludedCaches, new CompareCacheByLon());
		mostXxxCache.put("west",excludedCaches.get(0));
		mostXxxCache.put("east",excludedCaches.get(excludedCaches.size()-1));
		if (excludedCaches.size() % 2 == 0) {
			Integer index=excludedCaches.size()/2;
			Float lon = excludedCaches.get(index).lon;
			index--;
			lon = lon + excludedCaches.get(index).lon;
			cacheMedian.setLongitude(lon / 2.0F);
		} else {
			int index = (int) Math.floor(excludedCaches.size() / 2.0);
			cacheMedian.setLongitude(excludedCaches.get(index).lon);
		}

		// milestones
		Collections.sort(foundCaches, new CompareCacheByFoundDate());
		milestones.put(1, foundCaches.get(0));
		for (int i = 99; i < foundCaches.size(); i = i + 100) {
			milestones.put(i + 1, foundCaches.get(i));
		}
		milestones.put(foundCaches.size(), foundCaches.get(foundCaches.size() - 1));

		// first caching day
		firstCachingDay = foundCaches.get(0).found;
		firstCachingDay.set(Calendar.HOUR_OF_DAY, 0);
		firstCachingDay.set(Calendar.MINUTE, 0);
		firstCachingDay.set(Calendar.SECOND, 0);

	}

	// finds by month
	// finds by bearing
	// finds by year cache placed
	// finds to todays date by year
	
	/*
	 * AUXILIARY METHODS
	 */

	private void initVars() {

		matrixTerrDiff = new HashMap<Float, HashMap<Float, Integer>>();
		for (Float i : Constants.TERRDIFF) {
			HashMap<Float, Integer> temp = new HashMap<Float, Integer>();
			for (Float j : Constants.TERRDIFF) {
				temp.put(j, 0);
			}
			matrixTerrDiff.put(i, temp);
		}

		matrixMonthDay = new HashMap<Integer, HashMap<Integer, Integer>>();
		for (int j = 0; j <= 11; j++) {
			HashMap<Integer, Integer> tmpDays = new HashMap<Integer, Integer>();
			for (int i = 1; i <= 31; i++) {
				tmpDays.put(i, 0);
			}

			matrixMonthDay.put(j, tmpDays);
		}

		cachesByDayOfWeek = new HashMap<Integer, Integer>();
		for (Integer i = 1; i < 8 ; i++) {
			cachesByDayOfWeek.put(i, 0);
		}
		
		int[] distances = { 10, 20, 30, 40, 50, 100, 200, 300, 400, 500, 1000, 9999 };
		distanceFromHome = new TreeMap<Integer, Integer>();
		for ( int i: distances ) {
			distanceFromHome.put(i, 0);
		}
		
		cachesByDirection = new HashMap<String,Integer>();
		String[] tmp = {"n","nw","w","sw","s","se","e","ne"};
		for (String d: tmp) {
			cachesByDirection.put(d,0);
		}
		
		findsByMonth = new HashMap<Integer,Integer>();
		for (int i=0; i<12 ; i++) {
			findsByMonth.put(i, 0);
		}
	}
	
	private Boolean includeCache(Cache cache) {
		Boolean ret = true;
		if (cache.type == Constants.VIRTUAL && excludeVirtual) return false;
		if (cache.type == Constants.LOCATIONLESS && excludeLocless) return false;
		return ret;
	}
}

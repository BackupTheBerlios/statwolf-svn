package de.berlios.statwolf;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.bbn.openmap.LatLonPoint;
import com.bbn.openmap.proj.Length;

import de.cachewolf.CacheType;

public final class StatisticsData {

	private transient List<Cache> foundCaches;
	private transient Map<Integer, Integer> cachesByType;
	private transient Map<Integer, Integer> cachesByContainer;
	private transient Map<Integer, Integer> cachesByDayOfWeek;
	private transient Integer[][] matrixMonthDay;
	private transient Map<Integer, Integer[]> matrixYearMonthFound;
	private transient Map<Integer, Integer[]> matrixYearMonthPlaced;
	private transient Map<Integer, HashMap<Integer, Integer>> matrixTerrDiff;
	private transient Map<String,Integer> cachesByOwner;
	private transient Map<String, Cache> mostXxxCache = new HashMap<String, Cache>();
	private transient Map<String,Integer> cachesByDirection;
	private transient Integer totalCaches;
	private transient Integer findsLast365Days = 0;
	private transient Integer daysLastYear;
	private transient Integer daysSinceFirstFind;
	private final transient String distUnit;
	private transient Calendar firstCachingDay;
	private transient TreeMap<Integer, Integer> distanceFromHome;
	private transient TreeMap<Integer, Cache> milestones = new TreeMap<Integer, Cache>();
	private transient TreeMap <Calendar, ArrayList<Cache>> cachesByDateFound;
	private transient LatLonPoint homeCoordinates;
	private final LatLonPoint cacheMedian = new LatLonPoint();
	private transient Boolean excludeLocless;
	private transient Boolean excludeVirtual;
	private transient Double cacheToCacheDistance;
	private transient Map<String, Integer> findsByCountry = new HashMap<String, Integer>();
	private transient Map<String, Integer> findsByState = new HashMap<String, Integer>();
	private transient Integer noStateCounter = 0;
	private transient Integer noCountryCounter = 0;
	private transient Map<String, Integer> doublefc = new HashMap<String, Integer>();
	private transient Integer correctedCacheCount;
	private transient Cache oldestCache;
	private transient Cache newestCache;
	private transient Cache closestCache;
	private transient Cache outmostCache;
	private transient Integer archivedCaches;
	
	private static final Logger LOGGER = Logger.getLogger(StatisticsData.class);

	public StatisticsData(final List<Cache> pFoundCaches, final LatLonPoint homeCoordinates, final Properties prefs) {
		distUnit = prefs.getProperty("distunit", "km");
		excludeLocless = Boolean.parseBoolean(
				prefs.getProperty("excludelocless", "false")
			);
		excludeVirtual = Boolean.parseBoolean(
				prefs.getProperty("excludevirtual", "false")
			);

		updateStatistics(pFoundCaches, homeCoordinates);
	}

	public void updateStatistics(final List<Cache> aFoundCaches, 
			final LatLonPoint aHomeCoordinate) {
		foundCaches = aFoundCaches;
		homeCoordinates = aHomeCoordinate;

		initVars();
		setDataMatrix();
	}
	
	/*
	 * GET methods
	 */

	public Map<Integer, Integer> getCachesByTerrain() {
		final HashMap<Integer, Integer> cbd = new HashMap<Integer, Integer>();
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

	public HashMap<Integer, Integer> getCachesByDifficulty() {
		final HashMap<Integer, Integer> cbd = new HashMap<Integer, Integer>();
		Integer temp;
		for (Integer diff : Constants.TERRDIFF) {
			Integer counter = 0;
			for (Integer terr : Constants.TERRDIFF) {
				temp = matrixTerrDiff.get(terr).get(diff);
				counter += temp;
			}
			cbd.put(diff, counter);
		}
		return cbd;
	}

	public HashMap<Integer, Integer> getCachesByType() {
		return (HashMap<Integer, Integer>) cachesByType;
	}

	public HashMap<Integer, Integer> getCachesByContainer() {
		return (HashMap<Integer, Integer>) cachesByContainer;
	}

	public TreeMap<Integer, Cache> getMilestones() {
		return milestones;
	}

//	public HashMap<Boolean, Integer> getCachesOnline() {
//		return cachesOnline;
//	}
	
	public Integer getCorrectedCacheCount() {
		return correctedCacheCount;
	}

	/** @return caches by day of week found */
	public HashMap<Integer, Integer> getCachesByDayOfWeek() {
		return (HashMap<Integer, Integer>) cachesByDayOfWeek;
	}

	public Integer[][] getMatrixMonthDay() {
		return matrixMonthDay.clone();
	}

	public HashMap<Integer, Integer[]> getMatrixYearMonthFound() {
		return (HashMap<Integer, Integer[]>)matrixYearMonthFound;
	}

	public HashMap<Integer, Integer[]> getMatrixYearMonthPlaced() {
		return (HashMap<Integer, Integer[]>) matrixYearMonthPlaced;
	}

	public HashMap<Integer, HashMap<Integer, Integer>> getMatrixTerrDiff() {
		return (HashMap) matrixTerrDiff;
	}

	public Integer getTotalCaches() {
		return totalCaches;
	}

	public HashMap<String, Cache> getMostXxxCache() {
		return (HashMap) mostXxxCache;
	}

	public Calendar getFirstCachingDay() {
		return firstCachingDay;
	}

	public TreeMap<Integer, Integer> getDistanceFromHome() {
		return distanceFromHome;
	}
	
	public TreeMap<Calendar, ArrayList<Cache>> getCachesByDate () {
		return cachesByDateFound;
	}

	public Integer getDaysSinceFirstFind() {
		return daysSinceFirstFind;
	}
	
	public HashMap<String,Integer> getCachesByOwner() {
		return (HashMap) cachesByOwner;
	}
	
	public LatLonPoint getCacheMedian() {
		return cacheMedian;
	}
	
	public TreeSet<UserNumber> getCachesByOwnerSorted() {
		final TreeSet<UserNumber> cachesByOwnerSorted = new TreeSet<UserNumber>();
		for (String key: cachesByOwner.keySet()) {
			cachesByOwnerSorted.add(new UserNumber(key, cachesByOwner.get(key)));
		}
		return cachesByOwnerSorted;
	}
	
	public Calendar getCleanDate(final Calendar cal) {
		final Calendar tmpcal = (Calendar) cal.clone();
		tmpcal.set(Calendar.HOUR_OF_DAY, 0);
		tmpcal.set(Calendar.MINUTE, 0);
		tmpcal.set(Calendar.SECOND, 0);
		tmpcal.set(Calendar.MILLISECOND, 0);
		return tmpcal;
	}
	
	public Integer getFindsLast365Days() {
		return findsLast365Days;
	}
	
	public Integer getCachingDaysLastYear() {
		Integer cachingDays = 0;
		final Calendar today = getCleanDate(Calendar.getInstance());
		final Calendar lastYear = (Calendar) today.clone();
		lastYear.add(Calendar.DAY_OF_MONTH, -daysLastYear);
		for (Calendar cacheDay : cachesByDateFound.keySet()) {
			if (cacheDay.after(lastYear)) {
				cachingDays++;
			}
		}
		return (cachingDays<daysSinceFirstFind)?cachingDays:daysSinceFirstFind;
	}
	
	public Integer getCachingDays() {
		return cachesByDateFound.size();
	}
	
	public HashMap<String,Integer> getCachesByDirection() {
		return (HashMap) cachesByDirection;
	}
	
	public Integer[] getFindsByMonthFound() {
		Integer[] fpmf = Constants.ZEROMONTHS;
		for (Integer year: matrixYearMonthFound.keySet()) {
			for (Integer month = 0; month < 12; month++) {
				fpmf[month] = fpmf[month] +  matrixYearMonthFound.get(year)[month];
			}
		}
		return fpmf;
	}
	
	public Integer getDaysLastYear() {
		return (daysLastYear<daysSinceFirstFind) ? daysLastYear : daysSinceFirstFind;
	}

	public Double getCacheToCacheDistance() {
		return cacheToCacheDistance;
	}
	
	public Integer[] getFindsByMonthPlaced(){
		Integer[] fpmp = Constants.ZEROMONTHS;
		for (Integer year: matrixYearMonthPlaced.keySet()) {
			for (Integer month = 0; month < 12; month++) {
				fpmp[month] = fpmp[month] +  matrixYearMonthPlaced.get(year)[month];
			}
		}
		return fpmp;
	}
	
	public HashMap<Integer, Integer> getFindsByYearPlaced() {
		final HashMap<Integer, Integer> findsByYearPlaced = new HashMap<Integer, Integer>();
		for (Integer year: matrixYearMonthPlaced.keySet()) {
			Integer cpy = 0;
			for (Integer month = 0; month < 12; month++) {
				cpy = cpy +  matrixYearMonthPlaced.get(year)[month];
			}
			findsByYearPlaced.put(year, cpy);
		}
		return findsByYearPlaced;
	}
	
	public HashMap<Integer, Integer> getFindsByYearFound() {
		final HashMap<Integer, Integer> findsByYearFound = new HashMap<Integer, Integer>();
		for (Integer year: matrixYearMonthFound.keySet()) {
			Integer cpy = 0;
			for (Integer month = 0; month < 12; month++) {
				cpy = cpy +  matrixYearMonthFound.get(year)[month];
			}
			findsByYearFound.put(year, cpy);
		}
		return findsByYearFound;
	}

	public HashMap<Integer,Integer> getCachesByYearToDate() {
		HashMap<Integer,Integer> cachesByYearToDate = new HashMap<Integer,Integer>();
		// TODO: hmm
		return cachesByYearToDate;
	}
	
	public TreeMap<String,Integer> getFindsByCountry() {
		return new TreeMap<String,Integer>(findsByCountry);
	}

	public Cache getOldestCache() { return oldestCache; }
	
	public Cache getNewestCache() { return newestCache; }
	
	public Cache getClosestCache() { return closestCache; }
	
	public Cache getOutmostCache() { return outmostCache; }

	/** @return number of currently archived caches */
	public Integer getArchivedCaches() { return archivedCaches; }

	/** if someone finds what it does, let me know ;) */
	private void setDataMatrix() {
		
		matrixYearMonthFound = new HashMap<Integer, Integer[]>();
		matrixYearMonthPlaced = new HashMap<Integer, Integer[]>();
		Integer[] tempMonths;
		Integer counter = 0;
		archivedCaches = 0;

		totalCaches = foundCaches.size();
		cachesByType = new HashMap<Integer, Integer>();
		cachesByType = new HashMap<Integer, Integer>();
//		cachesOnline = new HashMap<Boolean, Integer>();
		cachesByDateFound = new TreeMap<Calendar, ArrayList<Cache>>(); 
		cachesByOwner = new HashMap<String,Integer>();
		final Calendar today = getCleanDate(Calendar.getInstance());
		final Calendar lastYear = (Calendar) today.clone();
		lastYear.add(Calendar.DAY_OF_MONTH, -daysLastYear);
		Cache lastCacheFound = null;
		cacheToCacheDistance = 0D;
		
		Collections.sort(foundCaches, new CompareCacheByFoundDate());
		
		LOGGER.info("counting");

		// TODO: put most of this into separate sub routines
		for (Cache cache : foundCaches) {
			counter++;
			if (counter % 50 == 0) { System.out.print("."); }

			if (doublefc.containsKey(cache.getId())) {
				LOGGER.warn("duplicate entry for " + cache.getId());
				break;
			} else {
				doublefc.put(cache.getId(), 1);
			}



			// matrix year month found
			updYearMonthFound(cache);
			// matrix year month placed
			updYearMonthPlaced(cache);

			// day of week
			final Integer foundDOW = cache.getFoundDate().get(Calendar.DAY_OF_WEEK);
			cachesByDayOfWeek.put(foundDOW, cachesByDayOfWeek.get(foundDOW) + 1);

			// size
			if (cachesByContainer.containsKey(cache.getSize())) {
				cachesByContainer.put(cache.getSize(), cachesByContainer.get(cache.getSize()) + 1);
			} else {
				cachesByContainer.put(cache.getSize(), 1);
			}

			// type
			if (cachesByType.containsKey(cache.getType())) {
				cachesByType.put(cache.getType(), cachesByType.get(cache.getType()) + 1);
			} else {
				cachesByType.put(cache.getType(), 1);
			}

			// archived
			if (cache.isArchived()) { archivedCaches++; }

			// matrixMonthDay
			matrixMonthDay[cache.getFoundDate().get(Calendar.MONTH)][cache.getFoundDate().get(Calendar.DAY_OF_MONTH)]++;
			
			// matrixTerrDiff
			{
				final HashMap<Integer, Integer> temp = matrixTerrDiff.get(cache.getTerrain());
				temp.put(cache.getDifficulty(), temp.get(cache.getDifficulty()) + 1);
				matrixTerrDiff.put(cache.getTerrain(), temp);
			}
			
			// distance from home
			updDistanceHome(cache);
			
			// direction
			updCardinalPoints(cache);
			
			// cache to cache distance
			lastCacheFound = updCacheToCacheDist(lastCacheFound, cache);

			// cache by date found
			{
				final Calendar tempdate = getCleanDate((Calendar) cache.getFoundDate().clone());
				ArrayList<Cache> tempclist;
				if (cachesByDateFound.containsKey(tempdate)) {
					tempclist = cachesByDateFound.get(tempdate);
				} else {
					tempclist = new ArrayList<Cache>();	
				}
				tempclist.add(cache);
				cachesByDateFound.put(tempdate, tempclist);
				if (tempdate.after(lastYear)) {
					findsLast365Days++;
				}
			}
			
			// cache by owner
			if (cachesByOwner.containsKey(cache.getOwner())) {
				cachesByOwner.put(cache.getOwner(), cachesByOwner.get(cache.getOwner()) + 1);
			} else {
				cachesByOwner.put(cache.getOwner(), 1);
			}
			
			// cache by country
			if (includeCache(cache)) {
				if (cache.getDetails().isValid() && !"".equals(cache.getDetails().getCountry())) {
					if (cache.getDetails().getCountry() == null) {
						LOGGER.debug("NULL country in "+cache.getId());
						noCountryCounter++;
					} else if (findsByCountry.containsKey(cache.getDetails().getCountry())) {
						findsByCountry.put(cache.getDetails().getCountry(), findsByCountry.get(cache.getDetails().getCountry())+1);
					} else {
						findsByCountry.put(cache.getDetails().getCountry(),1);
					}
				} else {
					LOGGER.debug("missing country information for ".concat(cache.getId()));
					noCountryCounter++;
				}
			}
			// cache by state
			
			// oldest cache
			if (includeCache(cache)) {
				if (null == oldestCache) {
					oldestCache = cache;
				} else {
					if (cache.getHidden().before(oldestCache.getHidden())) {
						oldestCache = cache;
					}
				}
			}
			
			// newest cache
			if (includeCache(cache)) {
				if (null == newestCache) {
					newestCache = cache;
				} else {
					if (cache.getHidden().after(newestCache.getHidden())) {
						newestCache = cache;
					}
				}
				
			}
			
			// closest cache
			if (includeCache(cache)) {
				if (null == closestCache) {
					closestCache = cache;
				} else {
					float oldDist, newDist;
					oldDist = Length.KM.fromRadians(homeCoordinates.distance(new LatLonPoint(closestCache.getLat(), closestCache.getLon())));
					newDist = Length.KM.fromRadians(homeCoordinates.distance(new LatLonPoint(cache.getLat(), cache.getLon())));
					if (newDist < oldDist) {
						closestCache = cache;
					}
				}
			}
			
			// outmost cache
			if (includeCache(cache)) {
				if (null == outmostCache) {
					outmostCache = cache;
				} else {
					float oldDist, newDist;
					oldDist = Length.KM.fromRadians(homeCoordinates.distance(new LatLonPoint(outmostCache.getLat(), outmostCache.getLon())));
					newDist = Length.KM.fromRadians(homeCoordinates.distance(new LatLonPoint(cache.getLat(), cache.getLon())));
					if (newDist > oldDist) {
						outmostCache = cache;
					}
				}
			}
		}
		
		if ( noCountryCounter > 0) {
			LOGGER.warn(noCountryCounter.toString().concat(" caches have no country information associated to them"));
		}

		List<Cache> excludedCaches = new ArrayList<Cache>(foundCaches);
		
		for (Cache c : foundCaches ) {
			if (! includeCache(c)) {
				excludedCaches.remove(c);
			}
		}
		
		// most northern, eastern, western, southern cache & median
		Collections.sort(excludedCaches, new CompareCacheByLat());
		mostXxxCache.put("south",excludedCaches.get(0));
		mostXxxCache.put("north",excludedCaches.get(excludedCaches.size() - 1));
		if (excludedCaches.size() % 2 == 0) {
			Integer index=excludedCaches.size() / 2;
			Float lat = excludedCaches.get(index).getLat();
			index--;
			lat = lat + excludedCaches.get(index).getLat();
			cacheMedian.setLatitude(lat / 2.0F);
		} else {
			Integer index = (int) Math.floor(excludedCaches.size() / 2.0);
			cacheMedian.setLatitude(excludedCaches.get(index).getLat());
		}

		Collections.sort(excludedCaches, new CompareCacheByLon());
		mostXxxCache.put("west", excludedCaches.get(0));
		mostXxxCache.put("east", excludedCaches.get(excludedCaches.size() - 1));
		if (excludedCaches.size() % 2 == 0) {
			Integer index = excludedCaches.size() / 2;
			Float lon = excludedCaches.get(index).getLon();
			index--;
			lon = lon + excludedCaches.get(index).getLon();
			cacheMedian.setLongitude(lon / 2.0F);
		} else {
			int index = (int) Math.floor(excludedCaches.size() / 2.0);
			cacheMedian.setLongitude(excludedCaches.get(index).getLon());
		}
		
		correctedCacheCount = excludedCaches.size();

		// milestones
		Collections.sort(foundCaches, new CompareCacheByFoundDate());
		milestones.put(1, foundCaches.get(0));
		for (int i = 99; i < foundCaches.size(); i = i + 100) {
			milestones.put(i + 1, foundCaches.get(i));
		}
		milestones.put(foundCaches.size(), foundCaches.get(foundCaches.size() - 1));

		// first caching day
		firstCachingDay = getCleanDate(foundCaches.get(0).getFoundDate());
	
		daysSinceFirstFind = daysBetween(firstCachingDay, Calendar.getInstance()) + 1;

		System.out.println(); // NOPMD by greis on 16.08.09 23:32
	}

	// finds by year cache placed
	// finds to todays date by year
	
	/*
	 * AUXILIARY METHODS
	 */

	/** set various variables to sane defaults. */
	private void initVars() {
		
		setDaysLastYear();
		
		cachesByContainer = new HashMap<Integer, Integer>();
		for (Integer cont : Constants.CONTAINERS) {
			cachesByContainer.put(cont, 0);
		}

		matrixTerrDiff = new HashMap<Integer, HashMap<Integer, Integer>>();
		for (Integer i : Constants.TERRDIFF) {
			HashMap<Integer, Integer> temp = new HashMap<Integer, Integer>();
			for (Integer j : Constants.TERRDIFF) {
				temp.put(j, 0);
			}
			matrixTerrDiff.put(i, temp);
		}

		matrixMonthDay = new Integer[12][32];
		for (int month = 0; month <= 11; month++) {
			matrixMonthDay[month]=Constants.ZERODAYS.clone();
		}

		cachesByDayOfWeek = new HashMap<Integer, Integer>();
		for (Integer i = 1; i < 8 ; i++) {
			cachesByDayOfWeek.put(i, 0);
		}
		
		final int[] distances = { 10, 20, 30, 40, 50, 100, 200, 300, 400, 500, 1000, 9999 };
		distanceFromHome = new TreeMap<Integer, Integer>();
		for (int i : distances) {
			distanceFromHome.put(i, 0);
		}
		
		cachesByDirection = new HashMap<String, Integer>();
		final String[] tmp = {"n", "nw", "w", "sw", "s", "se", "e", "ne"};
		for (String d : tmp) {
			cachesByDirection.put(d, 0);
		}
	}
	
	/**
	 * check if cache should be included in calculations.
	 * 
	 * @param cache
	 * @return false if caches is virtual or locless and virtual resp. locless
	 *         caches should be excluded true otherwise
	 */
	private Boolean includeCache(final Cache cache) {
		Boolean ret = true;
		
		if (cache.getType() == CacheType.CW_TYPE_VIRTUAL && excludeVirtual) {
			ret = false;
		} else if (cache.getType() == CacheType.CW_TYPE_LOCATIONLESS && excludeLocless) {
			ret = false;
		}
		
		return ret;
	}
	
	/**
	 * number of days in the last 12 months.
	 * 
	 * @return 365 for "normal" year, 366 if time span crosses February 29th in a
	 *         leap year
	 */
	private void setDaysLastYear() {
		Calendar today = Calendar.getInstance();
		Calendar lastYear = (Calendar) today.clone();
		lastYear.add(Calendar.YEAR, -1);
		daysLastYear = 0;
		while (lastYear.before(today)) {
			daysLastYear++;
			lastYear.add(Calendar.DAY_OF_MONTH, 1);
		}
	}

	/**
	 * calculate the absolute number of days between two given calendar objects.
	 * if <code>cal1</code> is after <code>cal2</code> they will be swapped
	 * 
	 * @param cal1
	 *            first date
	 * @param cal2
	 *            second date
	 * @return absolute number of days between <code>cal1</code> and
	 *         <code>cal2</code>
	 */
	public Integer daysBetween(final Calendar cal1, final Calendar cal2) {
		//Integer delta;

		if (cal1 == null || cal2 == null) {
			return null;
		}
		
		Calendar cal1clone = getCleanDate((Calendar) cal1.clone());
		Calendar cal2clone = getCleanDate((Calendar) cal2.clone());

		if (cal1clone.compareTo(cal2clone) == 0) {
			return 0;
		}

		if (cal1clone.compareTo(cal2clone) > 0) {
			Calendar temp = cal1clone;
			cal1clone = cal2clone;
			cal2clone = temp;
		}
		
		Integer delta = 0;
		while (cal1clone.compareTo(cal2clone) < 0) {
			delta++;
			cal1clone.add(Calendar.DAY_OF_MONTH, 1);
		}

		return delta;
	}

	/**
	 * determine the cardinal direction of a given cache from the
	 * <code>homeCoordinates</code>.
	 * 
	 * @param aCache
	 *            cache to check
	 */
	private void updCardinalPoints(final Cache aCache) {
		if (includeCache(aCache)) {
			String dir;
			final Float azimuth = homeCoordinates.azimuth(
					new LatLonPoint(aCache.getLat(), aCache.getLon())
				);
			final Double piAchtel = Math.PI / 8.0;
			
			if (azimuth < -7.0 * piAchtel) {
				dir = "s";
			} else if (azimuth < -5.0 * piAchtel) {
				dir = "sw";
			} else if (azimuth < -3.0 * piAchtel) {
				dir = "w";
			} else if (azimuth < -1.0 * piAchtel) {
				dir = "nw";
			} else if (azimuth < 1.0 * piAchtel) {
				dir = "n";
			} else if (azimuth < 3.0 * piAchtel) {
				dir = "ne";
			} else if (azimuth < 5.0 * piAchtel) {
				dir = "e";
			} else if (azimuth < 7.0 * piAchtel) {
				dir = "se";
			} else {
				dir = "s";
			}

			cachesByDirection.put(dir, cachesByDirection.get(dir) + 1);
		}
	}
	
	/**
	 * update distance from home statistics.<br>
	 * distance will be calculated as miles or km.
	 * 
	 * @param aCache
	 *            cache to check against home coordinates
	 */
	private void updDistanceHome(final Cache aCache) {
		if (includeCache(aCache)) {
			Float dist;
			if ("mi".equals(distUnit)) {
				dist = Length.MILE.fromRadians(homeCoordinates.distance(
						new LatLonPoint(aCache.getLat(), aCache.getLon()))
					);
			} else { 
				dist = Length.KM.fromRadians(homeCoordinates.distance(
						new LatLonPoint(aCache.getLat(), aCache.getLon()))
					);
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
	}

	/**
	 * 
	 * @param lastCache
	 * @param thisCache
	 * @return
	 */
	private Cache updCacheToCacheDist(final Cache lastCache, final Cache thisCache) {
		Cache ret;
		if (includeCache(thisCache)) {
			if (lastCache != null) {
				final LatLonPoint thisPos = new LatLonPoint(
						thisCache.getLat(), thisCache.getLon()
					);
				final LatLonPoint lastPos = new LatLonPoint(
						lastCache.getLat(), lastCache.getLon()
					);
				cacheToCacheDistance = cacheToCacheDistance 
					+ ("mi".equals(distUnit)
						? Length.MILE.fromRadians(lastPos.distance(thisPos))
						: Length.KM.fromRadians(lastPos.distance(thisPos))
					);
			}
			ret = thisCache;
		} else {
			ret = lastCache;
		}
		return ret;
	}
	
	private void updYearMonthFound(final Cache aCache) {
		Integer[] tempMonths;
		final Integer year = aCache.getFoundDate().get(Calendar.YEAR);
		final Integer month = aCache.getFoundDate().get(Calendar.MONTH);
		if (!matrixYearMonthFound.containsKey(year)) {
			matrixYearMonthFound.put(year, Constants.ZEROMONTHS.clone());
		}

		tempMonths = matrixYearMonthFound.get(year);
		tempMonths[month]++;
		matrixYearMonthFound.put(year, tempMonths);
	}

	private void updYearMonthPlaced(final Cache aCache) {
		Integer[] tempMonths;
		final Integer year = aCache.getHidden().get(Calendar.YEAR);
		final Integer month = aCache.getHidden().get(Calendar.MONTH);
		if (!matrixYearMonthPlaced.containsKey(year)) {
			matrixYearMonthPlaced.put(year, Constants.ZEROMONTHS.clone());
		}
	
		tempMonths = matrixYearMonthPlaced.get(year);
		tempMonths[month]++;
		matrixYearMonthPlaced.put(year, tempMonths);
	}

	
}

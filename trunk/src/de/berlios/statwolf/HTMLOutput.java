package de.berlios.statwolf;

import java.util.*;
import org.apache.log4j.*;

import de.cachewolf.CacheSize;
import de.cachewolf.CacheType;

import java.io.*;
import java.net.URLEncoder;
import java.text.*;

public class HTMLOutput {

	private StatisticsData stats;
	private ResourceBundle messages;
	private String distUnit;
	private String username;
	private ResourceBundle html;
	private static Logger logger = Logger.getLogger(HTMLOutput.class);
	private Boolean excludeVirtual = false;
	private Boolean excludeLocless = false;
	private Boolean excludeSomething = false;

	public HTMLOutput(StatisticsData stats) {
		
		String htmlSchema;
		String locale;
		
		distUnit = StatWolf.prefs.getProperty("distunit", "km");
		username = StatWolf.prefs.getProperty("username");
		if (username == null) {
			logger.error("username not set, please check preferences");
			System.exit(1);
		}
		try {
			excludeVirtual = Boolean.parseBoolean(StatWolf.prefs.getProperty("excludevirtual", "false"));
			excludeLocless = Boolean.parseBoolean(StatWolf.prefs.getProperty("excludelocless", "false"));
			excludeSomething = excludeVirtual || excludeLocless;
		} catch (Exception ex) {
			logger.error("error when parsing exsclude* properties", ex);
		}
		locale = StatWolf.prefs.getProperty("locale", "en");
		htmlSchema = "html_".concat(StatWolf.prefs.getProperty("htmlschema", "default"));
		
		this.stats = stats;
		html = ResourceBundle.getBundle(htmlSchema);
		try {
			Properties html = new Properties();
			html.load(this.getClass().getClassLoader().getResourceAsStream(htmlSchema.concat(".properties")));
		} catch (Exception ex) {
			logger.fatal("unable to load html schema");
			logger.debug(ex);
			System.exit(1);
		}
		
		Locale.setDefault(new Locale(locale));
		messages = ResourceBundle.getBundle("messages");
	}

	public String generateHTML() {
		StringBuffer out = new StringBuffer();
		
		out.append(htmlHeader());
		out.append(htmlStats());
		out.append(htmlFooter());
		
		String outdir = StatWolf.prefs.getProperty("outputdir", System.getProperty("java.io.tmpdir"));
		
		if ( !(outdir.endsWith("/") || outdir.endsWith("\\")) ) {
		   outdir = outdir + System.getProperty("file.separator");
		}

		String outFileName = outdir.concat("StatWolf.html");

		try {
			BufferedWriter of = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFileName),"UTF8"));
			of.write(out.toString());
			of.close();
			return outFileName;
		} catch (IOException ex) {
			logger.fatal("Error saving HTML output");
			logger.debug(ex);
			return null;
		}

	}

	private String matrixTerrainDifficulty() {

		StringBuffer ret = new StringBuffer();
		Integer combinations = 0;

		ret.append("<div style=\"float:left;width:100%;\">\n");
		ret.append(generateHeading(messages.getString("msg.tdm")));

		ret.append("<table style=\"table-layout,fixed;width:98%;\">\n");
		ret.append(String.format("<thead style=\"%s\">\n", html.getString("matrix.head1")));
		ret.append(String.format("<tr><td></td><td></td><td colspan='9'>%s</td></tr>\n", messages.getString("msg.diff")));
		ret.append("</thead>");
		//FIXME: the second THEAD violates DTD - however most browsers render it correct
		ret.append(String.format("<thead style=\"%s\">\n", html.getString("matrix.head2")));
		ret.append(String.format("<tr><td style=\"%s\"></td><td></td>", html.getString("matrix.head1")));
		for (Integer i : Constants.TERRDIFF) {
			ret.append(MessageFormat.format("<td>{0,number,0.0}</td>", i/10F));
		}
		ret.append("</tr>\n");
		ret.append("</thead>\n");
		
		HashMap<Integer, HashMap<Integer, Integer>> mtd = new HashMap<Integer, HashMap<Integer, Integer>>();
		mtd = stats.getMatrixTerrDiff();
		ret.append(String.format("<tbody style=\"%s\">\n", html.getString("matrix.body")));
		for (Integer terr : Constants.TERRDIFF) {
			ret.append("<tr>");
			if (terr == 10) {
				ret.append(String.format("<td rowspan='9' style=\"%s\">%s</td>",
						html.getString("matrix.head1"),
						messages.getString("msg.terr"
							)
						)
					);
			}
			ret.append(MessageFormat.format(
					"<td style=\"{0}\">{1,number,0.0}</td>", 
					html.getString("matrix.head2"),
					terr/10F));
			for (Integer diff : Constants.TERRDIFF) {
				ret.append(MessageFormat.format("<td>{0}</td>", 
						mtd.get(terr).get(diff) == 0 ? "&nbsp;" : mtd.get(terr).get(diff)));
				combinations = combinations + (mtd.get(terr).get(diff) == 0 ? 0 : 1);
			}
			ret.append("</tr>\n");
		}

		ret.append("</tbody>\n</table>\n");
		ret.append(MessageFormat.format(messages.getString("msg.combination"),
				combinations, 81));
		ret.append("</div>\n");
		return ret.toString();
	}

	private String matrixMonthDay() {
		
		Integer[][] mmd = stats.getMatrixMonthDay();

		StringBuffer ret = new StringBuffer();
		Integer combinations = 0;

		ret.append("<div style=\"float:left;width:100%;\">\n");
		ret.append(generateHeading(messages.getString("msg.fpd")));

		ret.append("<table style=\"width:98%;table-layout:fixed;\">\n");
		ret.append(String.format("<thead style=\"%s\">\n", html.getString("matrix.head1")));
		ret.append(String.format("<tr><td></td><td></td><td colspan=\"12\">%s</td></tr>",messages.getString("msg.month")));
		ret.append("</thead>\n");
		//FIXME: the second THEAD violates the DTD - however most browsers render it correct
		ret.append(String.format("<thead style=\"%s\">\n", html.getString("matrix.head2")));
		ret.append(String.format("<tr><td style=\"%s\"></td><td></td>",html.getString("matrix.head1")));
		for (int mon = 0 ; mon < 12; mon++) {
			ret.append(String.format("<td>%s</td>",messages.getString("mon.short."	+ mon)));
		}

		ret.append("</tr>\n");
		ret.append("</thead>\n");
		ret.append(String.format("<tbody style=\"%s\">\n", html.getString("matrix.body")));
		for (int day = 1 ; day <= 31 ; day ++) {
			ret.append("<tr>");
			if (day == 1) {
				ret.append(String.format("<td rowspan=\"31\" style=\"%s\">%s</td>",
						html.getString("matrix.head1"),
						messages.getString("msg.day")
					)
				);
			}
			ret.append(String.format("<td style=\"%s\">%s</td>",
					html.getString("matrix.head2"),
					day));

			for (int month = 0; month < 12; month++) {
				ret.append(MessageFormat.format("<td>{0}</td>",
						mmd[month][day] == 0 ? "" : mmd[month][day]
						)
					);
				combinations = combinations	+ (mmd[month][day] == 0 ? 0 : 1);
			}
			ret.append("</tr>\n");
		}
		ret.append("</tbody>\n</table>\n");
		ret.append(MessageFormat.format(messages.getString("msg.combination"), combinations, 366));
		ret.append("</div>\n");
		return ret.toString();
	}

	private String matrixYearMonth(HashMap<Integer, Integer[]> mym, String headline) {
		Set<Integer> years = new TreeSet<Integer>(mym.keySet());

		StringBuffer ret = new StringBuffer();

		ret.append("<div style=\"float:left;width:100%;\">\n");
		ret.append(generateHeading(headline));

		ret.append("<table style=\"width:98%;table-layout:fixed\">\n");
		ret.append(String.format("<thead style=\"%s\">\n", html.getString("matrix.head1")));
		ret.append("<tr><td></td><td></td><td colspan='12' align=\"center\">");
		ret.append(messages.getString("msg.month"));
		ret.append("</td></tr>\n");
		ret.append("</thead>\n");
		//FIXME: the second THEAD violates the DTD - however most browsers render it correct
		ret.append(String.format("<thead style=\"%s\">\n", html.getString("matrix.head2")));
		ret.append(String.format("<tr><td style=\"%s\"></td><td></td>", html.getString("matrix.head1")));
		for (int month = 0; month < 12; month++) {
			ret.append(String.format("<td>%s</td>",
					messages.getString("mon.short."+ month)));
		}
		ret.append("</tr>\n");
		ret.append("</thead>\n");
		
		ret.append(String.format("<tbody style=\"%s\">\n", html.getString("matrix.body")));
		Boolean firstYear = true;
		for (Integer year : years) {
			ret.append("<tr>");
			if (firstYear) {
				ret.append(String.format("<td rowspan=\"%d\" style=\"%s\">%s</td>", 
						years.size(),
						html.getString("matrix.head1"),
						messages.getString("msg.year")
					)
				);
				firstYear = false;
			}
			ret.append(String.format("<td style=\"%s\">%s</td>", 
					html.getString("matrix.head2"),
					year.toString()
					)
				);

			for (int month = 0; month < 12; month++) {
				ret.append(MessageFormat.format("<td>{0}</td>",
						mym.get(year)[month] == 0 ? "&nbsp;" : mym.get(year)[month]
						)
					);
			}

			ret.append("</tr>\n");
		}
		ret.append("</tbody>\n</table>\n</div>\n");
		return ret.toString();
	}

	private String findsByType() {
		HashMap<Integer,Integer> fbt = stats.getCachesByType();
		TreeSet<Integer> mapKeys = new TreeSet<Integer>(fbt.keySet());
		HashMap <Integer, Float> percent = new HashMap <Integer, Float>(); 
		Float maxPercent = 0.0F;
		
		for (Integer type : mapKeys) {
			if ( fbt.get(type) != null ) {
				Float x = calcCachePercent(fbt.get(type));
				percent.put(type, x);
				if ( x > maxPercent) {
					maxPercent = x;
				}
			} else {
				percent.put(type, 0.0F);
			}
		}
		
		StringBuffer ret = new StringBuffer();
		ret.append("<div style=\"float:left; width:50%;\">");
		ret.append(generateHeading(messages.getString("msg.fbtype")));
		ret.append("<table width=\"98%\" style=\"table-layout:fixed\">\n");
		ret.append(String.format("<thead style=\"%s\">\n", html.getString("table.head.default")));
		ret.append("<tr>" +
				"<td style=\"width:35%;\"></td>" +
				"<td style=\"width:13%;\">#</td>" +
				"<td style=\"width:9%;\">%</td>" +
				"<td style=\"\"></td>" +
				"</tr>\n");
		ret.append("</thead>\n");
		ret.append(String.format("<tbody style=\"%s\">\n",html.getString("table.body.default")));
		for (Integer type : mapKeys) {
			ret.append(MessageFormat.format(
					"<tr>" +
					"<td><img src=\"{0}\" alt=\"\"/> {1}</td><td style=\"{6}\">{2}</td>" +
					"<td style=\"{6}\">{3,number,#,##0.0}</td>" +
					"<td><img src=\"{4}\" width=\"{5}\" height=\"15\" alt=\"{3,number,#,##0.0}%\"/></td>" +
					"</tr>\n", 
					Constants.TYPEIMAGES.get(type),
					messages.getString("cachetype."+type),
					fbt.get(type),
					percent.get(type),
					html.getString("bar.horizontal"),
					calculateBarLength(percent.get(type), maxPercent), 
					html.getString("cell.number")
				)
			);
		}
		ret.append("</tbody>\n");
		ret.append("</table>\n");
		ret.append("</div>\n");
		return ret.toString();
	}

	private String findsByDT(HashMap<Integer,Integer> mftd, String heading) {
		StringBuffer ret = new StringBuffer();
		HashMap<Integer, Float> percent = new HashMap<Integer, Float>();
		Float maxPercent = 0.0F;
		
		ret.append("<div style=\"float:left; width:50%;\">\n");
		ret.append(generateHeading(heading));
		ret.append("<table width=\"98%\" style=\"table-layout:fixed\">\n");
		ret.append(String.format("<thead style=\"%s\">\n", html.getString("table.head.default")));
		ret.append("<tr>" +
				"<td style=\"width:35%;\"></td>" +
				"<td style=\"width:13%;\">#</td>" +
				"<td style=\"width:9%;\">%</td>" +
				"<td style=\"\"></td>" +
				"</tr>\n");
		ret.append("</thead>\n");
		ret.append(String.format("<tbody style=\"%s\">\n",html.getString("table.body.default")));
		for (Integer i : Constants.TERRDIFF) {
			if ( mftd.get(i) != null ) {
				Float x = calcCachePercent(mftd.get(i));
				percent.put(i, x);
				if ( x > maxPercent) {
					maxPercent = x;
				}
			} else {
				percent.put(i, 0.0F);
			}
		}
		for (Integer i : Constants.TERRDIFF) {
			ret.append(MessageFormat.format(
					"<tr>" +
					"<td>{0,number,#,##0.0}</td>" +
					"<td style=\"{5}\">{1}</td>" +
					"<td style=\"{5}\">{2,number,#,##0.0}</td>" +
					"<td><img src=\"{3}\" height=\"15\" width=\"{4}\" alt=\"{2,number,#,##0.0}%\"/></td>" +
					"</tr>\n",
					i/10F,
					mftd.get(i),
					percent.get(i),
					html.getString("bar.horizontal"),
					calculateBarLength(percent.get(i), maxPercent), 
					html.getString("cell.number")
				)
			);
		}
		ret.append("</tbody>\n");
		ret.append("</table>\n");
		ret.append("</div>\n");
		
		return ret.toString();
	}

	private Integer calculateBarLength(Float actual, Float max) {
		return (int) Math.round(actual/max*Constants.MAXHORIZONTALBARLENGTH);
	}
	
	private String findsByContainer() {
		StringBuffer ret = new StringBuffer();
		HashMap <Integer,Integer> fbc = stats.getCachesByContainer();
		HashMap<Integer,Float> percent = new HashMap<Integer,Float>();
		Float maxPercent = 0.0F;
		
		for (Integer cont : Constants.CONTAINERS) {
			if ( fbc.get(cont) != null ) {
				Float x = calcCachePercent(fbc.get(cont));
				percent.put(cont, x);
				if ( x > maxPercent) {
					maxPercent = x;
				}
			} else {
				percent.put(cont, 0.0F);
			}
		}
		
		ret.append("<div style=\"float:left; width:50%;\">\n");
		ret.append(generateHeading(messages.getString("msg.fbcontainer")));
		ret.append("<table width=\"98%\" style=\"table-layout:fixed\">\n");
		ret.append(String.format("<thead style=\"%s\">\n", html.getString("table.head.default")));
		ret.append("<tr>" +
				"<td style=\"width:35%;\"></td>" +
				"<td style=\"width:13%;\">#</td>" +
				"<td style=\"width:9%;\">%</td>" +
				"<td style=\"\"></td>" +
				"</tr>\n");
		ret.append("</thead>\n");
		ret.append(String.format("<tbody style=\"%s\">\n",html.getString("table.body.default")));
		
		for (Integer cont : Constants.CONTAINERS) {
			ret.append(MessageFormat.format(
					"<tr>" +
					"<td><img src=\"{0}\" alt=\"\"/> {1}</td>" +
					"<td style=\"{6}\">{2}</td>" +
					"<td style=\"{6}\">{3,number,#,##0.0}</td>" +
					"<td><img src=\"{4}\" height=\"15\" width=\"{5}\" alt=\"{3,number,#,##0.0}%\"/></td>" +
					"</tr>\n", 
					Constants.SIZEIMAGES.get(cont), 
					CacheSize.cw2ExportString(cont.byteValue()),
					fbc.get(cont) == null?"":fbc.get(cont), 
					fbc.get(cont) == null?"":percent.get(cont),
					html.getString("bar.horizontal"),
					calculateBarLength(percent.get(cont), maxPercent), 
					html.getString("cell.number")
				)
			);			
		}
		ret.append("</tbody>\n");
		ret.append("</table>\n");
		ret.append("</div>\n");
		return ret.toString();
	}
	
	private String findsByDayOfWeek() {
		StringBuffer ret = new StringBuffer();
		HashMap <Integer,Integer> fbc = stats.getCachesByDayOfWeek();
		HashMap<Integer,Float> percent = new HashMap<Integer,Float>();
		Float maxPercent = 0.0F;
		
		for (int dow=1; dow < 8; dow++) {
			if ( fbc.get(dow) != null ) {
				Float x = calcCachePercent(fbc.get(dow));
				percent.put(dow, x);
				if ( x > maxPercent) {
					maxPercent = x;
				}
			} else {
				percent.put(dow, 0.0F);
			}
		}
		
		ret.append("<div style=\"float:left; width:50%;\">\n");
		ret.append(generateHeading(messages.getString("msg.fbdayofweek")));
		ret.append("<table width=\"98%\" style=\"table-layout:fixed\">\n");
		ret.append(String.format("<thead style=\"%s\">\n", html.getString("table.head.default")));
		ret.append("<tr>" +
				"<td style=\"width:35%;\"></td>" +
				"<td style=\"width:13%;\">#</td>" +
				"<td style=\"width:9%;\">%</td>" +
				"<td style=\"\"></td>" +
				"</tr>\n");
		ret.append("</thead>\n");
		ret.append(String.format("<tbody style=\"%s\">\n",html.getString("table.body.default")));
		for (int dow=1; dow < 8; dow++) {
			ret.append(MessageFormat.format(
					"<tr>" +
					"<td>{0}</td>" +
					"<td style=\"{5}\">{1}</td>" +
					"<td style=\"{5}\">{2,number,#,##0.0}</td>" +
					"<td><img src=\"{3}\" height=\"15\" width=\"{4}\" alt=\"{2,number,#,##0.0}%\"/></td>" +
					"</tr>\n", 
					messages.getString("dow.long."+dow),
					fbc.get(dow) == null?0:fbc.get(dow), 
					percent.get(dow),
					html.getString("bar.horizontal"),
					calculateBarLength(percent.get(dow), maxPercent), 
					html.getString("cell.number")
				)
			);			
		}
		ret.append("</tbody>\n");
		ret.append("</table>\n");
		ret.append("</div>\n");
		return ret.toString();
	}
	
	private String formatLatLon(Float pos, String ll) {
		StringBuffer ret = new StringBuffer();
		String direction;
		Float absPos;
		Integer degree;
		Double minutes;
		
		absPos = Math.abs(pos);
		degree = absPos.intValue();
		minutes = (absPos - degree.doubleValue()) * 60.0;
		
		if (ll == "lat") { 
			if (pos < 0) { 
				direction = messages.getString("orientation.s"); 
			} else { 
				direction = messages.getString("orientation.n"); 
			}
			ret.append(
				MessageFormat.format(
					"{0} {1,number,00}\u00B0 {2,number,00.000}", 
					direction,
					degree,
					minutes
				)
			);
		} else if (ll == "lon") { 
			if (pos < 0) { 
				direction = messages.getString("orientation.w"); 
			} else { 
				direction = messages.getString("orientation.e"); 
			}
			ret.append(
					MessageFormat.format(
						"{0} {1,number,000}\u00B0 {2,number,00.000}", 
						direction,
						degree,
						minutes
					)
				);
		}
		return ret.toString();
	}
	
	private String findsByDistanceFromHome() {
		TreeMap<Integer,Integer> dfh = stats.getDistanceFromHome();
		StringBuffer ret = new StringBuffer();
		HashMap<Integer,Float> percent = new HashMap<Integer,Float>();
		Float maxPercent = 0.0F;
		Integer[] dist = {10,20,30,40,50,100,200,300,400,500,1000,9999};
		for (Integer i: dist) {
			if ( dfh.get(i) != null ) {
				Float x = calcCachePercent(dfh.get(i));
				percent.put(i, x);
				if ( x > maxPercent) {
					maxPercent = x;
				}
			} else {
				percent.put(i, 0.0F);
			}
		}
		ret.append("<div style=\"float:left; width:50%;\">\n");
		ret.append(generateHeading(MessageFormat.format(messages.getString("msg.fbdistance"), distUnit)));
		ret.append("<table style=\"table-layout:fixed; width:98%\">");
		ret.append(String.format("<thead style=\"%s\">\n", html.getString("table.head.default")));
		ret.append("<tr>"+
				"<td style=\"width:35%;\"></td>" +
				"<td style=\"width:13%;\">#</td>" +
				"<td style=\"width:9%;\">%</td>" +
				"<td style=\"\"></td>" +
				"</tr>\n");
		ret.append("</thead>\n");
		ret.append(String.format("<tbody style=\"%s\">\n",html.getString("table.body.default")));
		ret.append(formatDistanceFromHome("[ 0, 10 ]",dfh.get(10),percent.get(10),maxPercent));
		ret.append(formatDistanceFromHome("( 10, 20 ]",dfh.get(20),percent.get(20),maxPercent));
		ret.append(formatDistanceFromHome("( 20, 30 ]",dfh.get(30),percent.get(30),maxPercent));
		ret.append(formatDistanceFromHome("( 30, 40 ]",dfh.get(40),percent.get(40),maxPercent));
		ret.append(formatDistanceFromHome("( 40, 50 ]",dfh.get(50),percent.get(50),maxPercent));
		ret.append(formatDistanceFromHome("( 50, 100 ]",dfh.get(100),percent.get(100),maxPercent));
		ret.append(formatDistanceFromHome("( 100, 200 ]",dfh.get(200),percent.get(200),maxPercent));
		ret.append(formatDistanceFromHome("( 200, 300 ]",dfh.get(300),percent.get(300),maxPercent));
		ret.append(formatDistanceFromHome("( 300, 400 ]",dfh.get(400),percent.get(400),maxPercent));
		ret.append(formatDistanceFromHome("( 400, 500 ]",dfh.get(500),percent.get(500),maxPercent));
		ret.append(formatDistanceFromHome("( 500, 1000 ]",dfh.get(1000),percent.get(1000),maxPercent));
		ret.append(formatDistanceFromHome("( 1000, \u221E )",dfh.get(9999),percent.get(9999),maxPercent));
		ret.append("</tbody>\n");
		ret.append("</table>\n");
		ret.append("</div>\n");
		return ret.toString();
	}
	
	private String formatDistanceFromHome(String label, Integer count, Float percent, Float maxPercent) {
		return MessageFormat.format(
			"<tr><td style=\"height:21px;\">{0}</td><td style=\"{5}\">{1}</td><td style=\"{5}\">{2,number,#,##0.0}</td><td><img src=\"{3}\" height=\"15\" width=\"{4}\" alt=\"{2,number,#,##0.0}%\"/></td></tr>\n",
			label,
			count,
			percent,
			html.getString("bar.horizontal"), 
			calculateBarLength(percent, maxPercent), 
			html.getString("cell.number")
		);
	}
	
	private Float calcCachePercent(Integer count) {
		return Math.round(count.floatValue() / stats.getTotalCaches().floatValue() * 1000.0F) / 10.0F;
	}
	
	/**
	 * generate list of milestone caches. displays first cahce found and the very % 100 cache
	 * @return
	 */
	private String milestones() {
		TreeMap<Integer,Cache> milestones = stats.getMilestones();
		Calendar lastMilestone = null;
		StringBuffer ret = new StringBuffer();
		ret.append("<div style=\"float:left; width:100%;\">\n");
		ret.append(generateHeading(messages.getString("msg.milestones")));
		ret.append("<table style=\"width:98%;\">\n");
		ret.append(String.format("<thead style=\"%s\">\n", html.getString("table.head.default")));
		ret.append("<tr><td>#</td><td>Date</td><td>\u0394 Days</td><td>Waypoint</td><td>Cache</td></tr>\n");
		ret.append("</thead>\n");
		ret.append(String.format("<tbody style=\"%s\">\n",html.getString("table.body.default")));
		for (Map.Entry<Integer,Cache> entry : milestones.entrySet()) {
	        Cache value = entry.getValue();
	        Integer key = entry.getKey();
	        ret.append(MessageFormat.format("<tr>" +
	        		"<td style=\"{6}\">{0}</td>" +
	        		"<td>{1,date,medium}</td>" +
	        		"<td style=\"{6}\">{2}</td>" +
	        		"<td>{3}</td>" +
	        		"<td><img src=\"{4}\" alt=\"\"/> {5}</td>" +
	        		"</tr>\n", 
	        		key,
	        		value.getFoundDate().getTime(),
	        		stats.daysBetween(lastMilestone,value.getFoundDate()),
	        		cacheLink(value.getId()),
	        		Constants.TYPEIMAGES.get(value.getType()),
	        		escapeXML(value.getName()),
	        		html.getString("cell.number")
	        	)
	        );
	        lastMilestone = (Calendar) value.getFoundDate().clone();
		}
		ret.append("</tbody>\n");
		ret.append("</table>\n");
		ret.append(crystalball());
		ret.append("</div>\n");
		return ret.toString();
	}
	
	/**
	 * calculate numerous values for the datatomb
	 * @return
	 */
	private String datatomb () {
		StringBuffer ret = new StringBuffer();
		Integer totalDays = stats.getDaysSinceFirstFind();
		String strTemp;
		
		HashMap<String, Cache> mostXxxxCache = stats.getMostXxxCache();
		ret.append("<div style=\"float:left;width:100%;\">\n");
		ret.append(generateHeading(messages.getString("msg.datatomb")));
		ret.append("<table style=\"table-layout:fixed;width:98%\">\n");
		ret.append(String.format("<tbody style=\"%s\">\n",html.getString("table.body.default")));
		
		strTemp = String.format("<tr><td style=\"width:40%%\">%s</td><td>%s</td></tr>\n", 
				messages.getString("msg.total"), 
				messages.getString("msg.total.data")
			);
		ret.append(MessageFormat.format(strTemp,
				stats.getTotalCaches(),
				stats.getCachingDays(),
				totalDays
			)
		);
		strTemp = String.format("<tr><td>%s</td><td>%s</td></tr>\n", 
				messages.getString("msg.totalavg"), 
				messages.getString("msg.totalavg.data")
			);
		ret.append(MessageFormat.format(strTemp,
				stats.getTotalCaches().floatValue() / stats.getCachingDays().floatValue(),
				stats.getTotalCaches().floatValue() / totalDays.floatValue(),
				stats.getTotalCaches().floatValue() / totalDays.floatValue() * 7.0F
			)
		);
		strTemp = String.format("<tr><td>%s</td><td>%s</td></tr>\n", 
				messages.getString("msg.365"), 
				messages.getString("msg.total.data")
			);
		ret.append(MessageFormat.format(strTemp,
				stats.getFindsLast365Days(),
				stats.getCachingDaysLastYear(),
				stats.getDaysLastYear()
			)
		);
		strTemp = String.format("<tr><td>%s</td><td>%s</td></tr>\n", 
				messages.getString("msg.365avg"), 
				messages.getString("msg.totalavg.data")
			);
		ret.append(MessageFormat.format(strTemp,
				stats.getFindsLast365Days().floatValue() / stats.getCachingDaysLastYear().floatValue(),
				stats.getFindsLast365Days().floatValue() / stats.getDaysLastYear().floatValue(),
				stats.getFindsLast365Days().floatValue() / stats.getDaysLastYear().floatValue() * 7.0F
			)
		);
		
		String[] dirs = { "north", "south", "west", "east"};
		for (String dir:  dirs) {
			String outline=String.format("<tr><td style=\"\">%s%s</td><td>%s</td></tr>\n", 
					messages.getString("msg.cache.".concat(dir)),
					excludeSomething?"<font style=\"size:9px\">*</font>":"",
					messages.getString("msg.cache.most")
				);
			ret.append(MessageFormat.format(outline,
					(dir == "north" || dir == "south")?formatLatLon(mostXxxxCache.get(dir).getLat(), "lat"):formatLatLon(mostXxxxCache.get(dir).getLon(), "lon"),
					cacheLink(mostXxxxCache.get(dir).getId()),
					mostXxxxCache.get(dir).getName()
				)
			);
		}
		ret.append(String.format("<tr><td>%s%s</td><td><a href=\"http://maps.google.de/maps?q=%s,%s\">%s %s</a></td></tr>\n",
				messages.getString("msg.cachemedian"),
				excludeSomething?"<font style=\"size:9px\">*</font>":"",
				stats.getCacheMedian().getLatitude(),
				stats.getCacheMedian().getLongitude(),
				formatLatLon(stats.getCacheMedian().getLatitude(), "lat"),
				formatLatLon(stats.getCacheMedian().getLongitude(), "lon"))
			);
		
		strTemp = String.format("<tr><td>%s</td><td>%s</td></tr>\n",
				messages.getString("msg.cachetocachedistance"),
				"\u2211 {1,number,#,##0.0} {2} \u2205 {3,number,#,##0.0} {2}");
		ret.append(MessageFormat.format(strTemp,
				excludeSomething?"<font style=\"size:9px\">*</font>":"",
				stats.getCacheToCacheDistance(),
				distUnit,
				excludeSomething?(stats.getCacheToCacheDistance()/stats.getCorrectedCacheCount()):(stats.getCacheToCacheDistance()/stats.getTotalCaches())
				)
			);
		
		strTemp = String.format("<tr><td>%s%s</td><td><img src=\"%s\"> %s %s</td></tr>\n",
				messages.getString("msg.oldestcache"),
				excludeSomething?"*":"",
				Constants.TYPEIMAGES.get(stats.getOldestCache().getType()),
				cacheLink(stats.getOldestCache().getId()),
				escapeXML(stats.getOldestCache().getName())
				);
		ret.append(strTemp);
		
		strTemp = String.format("<tr><td>%s%s</td><td><img src=\"%s\"> %s %s</td></tr>\n",
				messages.getString("msg.newestcache"),
				excludeSomething?"*":"",
				Constants.TYPEIMAGES.get(stats.getNewestCache().getType()),
				cacheLink(stats.getNewestCache().getId()),
				escapeXML(stats.getNewestCache().getName())
				);
		ret.append(strTemp);		

		strTemp = String.format("<tr><td>%s%s</td><td><img src=\"%s\"> %s %s</td></tr>\n",
				messages.getString("msg.closestcache"),
				excludeSomething?"*":"",
				Constants.TYPEIMAGES.get(stats.getClosestCache().getType()),
				cacheLink(stats.getClosestCache().getId()),
				escapeXML(stats.getClosestCache().getName())
				);
		ret.append(strTemp);
		
		strTemp = String.format("<tr><td>%s%s</td><td><img src=\"%s\"> %s %s</td></tr>\n",
				messages.getString("msg.outmostcache"),
				excludeSomething?"*":"",
				Constants.TYPEIMAGES.get(stats.getOutmostCache().getType()),
				cacheLink(stats.getOutmostCache().getId()),
				escapeXML(stats.getOutmostCache().getName())
				);
		ret.append(strTemp);
		
		ret.append("</tbody>\n");
		ret.append("</table>\n");
		ret.append("</div>\n");
		return ret.toString();

	}
	
	/**
	 * generate a section heading
	 * @param heading
	 * @return
	 */
	private String generateHeading(String heading) {
		return String.format("<p style=\"%s\">%s</p>\n",
				html.getString("p.heading"),
				heading
			);
	}
	
	/**
	 * generate list of "top" owners who placed the found caches. user can change length of list via preferences
	 * @return
	 */
	private String findsByOwner() {
		StringBuffer ret = new StringBuffer();
		TreeSet<UserNumber> cachesByOwnerSorted = stats.getCachesByOwnerSorted();
		Integer numberOfOwners = (int) Math.floor(
				(Double.parseDouble(StatWolf.prefs.getProperty("ownernumber", "20"))+1.0)/2.0
				);
		
		ret.append("<div style=\"float:left;width:100%;\">\n");
		ret.append(generateHeading(messages.getString("msg.findsbyowner")));

		for (int j = 0 ; j < 2 ; j++) {
			ret.append("<div style=\"float:left;width:50%;\">\n");
			ret.append("<table style=\"table-layout:fixed;width:98%\">\n");
			ret.append(String.format("<thead style=\"%s\">\n", html.getString("table.head.default")));
			ret.append("<tr><td width=\"70%\"></td><td>#</td><td>%</td></tr>\n");
			ret.append("</thead>\n");
			ret.append(String.format("<tbody style=\"%s\">\n",html.getString("table.body.default")));
			for (int i = 0; i < numberOfOwners; i++) {
				if (cachesByOwnerSorted.size() > 0) {
					UserNumber owner = cachesByOwnerSorted.last();
					cachesByOwnerSorted.remove(owner);
					ret.append(MessageFormat.format(
							"<tr><td>{0}</td><td style=\"{3}\">{1,number,#,##0}</td><td style=\"{3}\">{2,number,#0.00}</td></tr>\n",
							ownerLink(owner.user),
							owner.number,
							owner.number.floatValue() / stats.getTotalCaches().floatValue()*100.0F,
							html.getString("cell.number")
						)
					);
				} else {
					ret.append("<tr><td>&nbsp;</td><td></td><td></td></tr>\n");
				}
			}
			ret.append("</tbody>");
			ret.append("</table>\n");
			ret.append("</div>\n");
		}

		String summary = String.format("<p style=\"%s\">%s</p>\n",
				html.getString("p.combinations"),
				messages.getString("msg.findsbyownermore")
			);
		ret.append(MessageFormat.format(summary, 
				cachesByOwnerSorted.size(),
				StatWolf.prefs.getProperty("username")
				)
			);
		ret.append("</div>\n");
		return ret.toString();
	}
	
	/**
	 * generate a compass rose using google chart API displaying cache numbers by direction relative to home coordinates
	 * @return
	 */
	private String findsByDirection() {
		HashMap<String,Integer> cbd = stats.getCachesByDirection();
		Integer maxCount = 0;
		StringBuffer chartData = new StringBuffer("&amp;chd=t:");
		StringBuffer chartHead = new StringBuffer("&amp;chxl=0:");

		for (String dir: Constants.DIRECTIONS) {
			if (cbd.get(dir) > maxCount) {
				maxCount = cbd.get(dir);
			}
			chartHead.append(String.format("|%s", messages.getString("orientation."+dir)));
			chartData.append(String.format("%s,", cbd.get(dir)));
		}
		chartData.append(cbd.get("n"));
		
		StringBuffer ret = new StringBuffer();
		String charturl= Constants.CHARTBASE
			.concat("cht=r")
			.concat("&amp;chs=250x250") // TODO: compute from outer div size
			.concat("&amp;chco=FF0000") // TODO: get color from profile
			.concat("&amp;chls=2.0,4.0,0.0")
			.concat("&amp;chxt=x")
			.concat("&amp;chf=bg,s,00000000")
			.concat("&amp;chxr=0,0.0,360.0")
			.concat(chartHead.toString()) 
			.concat(chartData.toString())
			.concat(String.format("&amp;chds=0,%s", maxCount)); 
		ret.append("<div style=\"float:left;width:50%;\">\n");
		ret.append(generateHeading(messages.getString("msg.findsbydirection")));
		ret.append(String.format("<p align=\"center\"><img src=\"%s\" alt=\"\"/></p>", charturl));
		ret.append("</div>\n");
		return ret.toString();
	}
	
	/**
	 * generate statistics for months the cache was found in
	 * @return
	 */
	private String findsByMonth() {
		Integer[] fbm = stats.getFindsByMonthFound();
		StringBuffer ret = new StringBuffer();
		Integer maxCount = 0;
		for (int month = 0; month < 12; month++) {
			if ( maxCount < fbm[month]) {
				maxCount = fbm[month];
			}
		}
		ret.append("<div style=\"float:left;width:50%;\">\n");
		ret.append(generateHeading(messages.getString("msg.findspermonth")));
		ret.append("<table width=\"98%\" style=\"table-layout:fixed\">\n");
		ret.append(String.format("<thead style=\"%s\">\n", html.getString("table.head.default")));
		ret.append("<tr>" +
				"<td style=\"width:35%;\"></td>" +
				"<td style=\"width:13%;\">#</td>" +
				"<td style=\"width:9%;\">%</td>" +
				"<td style=\"\"></td>" +
				"</tr>\n");
		ret.append("</thead>\n");
		ret.append(String.format("<tbody style=\"%s\">\n",html.getString("table.body.default")));
		for (int month = 0; month < 12; month++) {
			ret.append(MessageFormat.format("<tr><td>{0}</td><td style=\"{4}\">{1,number,#,##0}</td><td style=\"{4}\">{2,number,#,##0.0}</td><td>{3}</td></tr>\n",
					messages.getString("mon.long."+month),
					fbm[month],
					calcCachePercent(fbm[month]),
					createHorizontalBar(fbm[month],maxCount),
					html.getString("cell.number")
				)
			);
		}
		ret.append("</tbody>\n");
		ret.append("</table>\n");
		ret.append("</div>\n");
		return ret.toString();
	}
	
	/**
	 * generate link to a cache page for milestone statistic
	 * @param id waypoint
	 * @return url pointing to waypoint info
	 */
	private String cacheLink(String id) {
		return MessageFormat.format("<a href=\"http://coord.info/{0}\">{0}</a>", id);
	}

	/**
	 * generate link to an owner profile for owner statistics
	 * @param id
	 * @return
	 */
	private String ownerLink(String id) {
		String ret = "";
		try {
			ret =String.format("<a href=\"http://www.geocaching.com/profile/?u=%s\">%s</a>", 
					URLEncoder.encode(id, "UTF-8"),
					escapeXML(id));
		} catch (Exception ignore) { }
		return ret;
	}

	/**
	 * generate a horizontal image bar
	 * @param count
	 * @param maxCount
	 * @return
	 */
	private String createHorizontalBar(Integer count, Integer maxCount) {
		Integer width = (int) Math.floor(count.floatValue() / maxCount.floatValue() * Constants.MAXHORIZONTALBARLENGTH);
		return MessageFormat.format("<img src=\"{0}\" height=\"15\" width=\"{1}\" alt=\"{2,number,#,##0.0}%\"/>",
				html.getString("bar.horizontal"),
				width.toString(),
				calcCachePercent(count)
			);
	}

	/**
	 * generate forecast for next 00 and 000 milestone based on last years caching averages
	 * @return
	 */
	private String crystalball() {
		StringBuffer ret = new StringBuffer();
		Integer currentFinds = stats.getTotalCaches();
		Integer next00Milestone = (currentFinds + 100) / 100 * 100;
		Integer next000Milestone= (currentFinds + 1000) / 1000 * 1000;
		Integer delta00Finds = next00Milestone - currentFinds;
		Integer delta000Finds = next000Milestone - currentFinds;
		Float cpd = stats.getFindsLast365Days().floatValue() / stats.getDaysLastYear().floatValue();
		Integer days00Finds = Math.round(delta00Finds/cpd);
		Integer days000Finds = Math.round(delta000Finds/cpd);
		Calendar date00Finds = Calendar.getInstance();
		Calendar date000Finds = Calendar.getInstance();
		date00Finds.add(Calendar.DAY_OF_MONTH, days00Finds);
		date000Finds.add(Calendar.DAY_OF_MONTH, days000Finds);
		ret.append("<p>");
		ret.append(MessageFormat.format(messages.getString("msg.crystalball.1"), next00Milestone,date00Finds.getTime()));
		if ( next00Milestone != next000Milestone) {
			ret.append(MessageFormat.format(messages.getString("msg.crystalball.2"), next000Milestone,date000Finds.getTime() ));
		}
		ret.append("</p>\n");
		return ret.toString();
	}
	
	/**
	 * generate raw numbers of finds by country
	 * @return
	 */
	private String findsByCountry() {
		TreeMap<String,Integer> fbc =  stats.getFindsByCountry();
		StringBuffer ret = new StringBuffer();
		ret.append("<div style=\"float:left;width:50%;\">\n");
		ret.append(generateHeading(messages.getString("msg.findspercountry")+(excludeSomething?"<font style=\"size:9px\">*</font>":"")));
		ret.append("<table width=\"98%\" style=\"table-layout:fixed\">\n");
		ret.append(String.format("<thead style=\"%s\">\n", html.getString("table.head.default")));
		ret.append("<tr><td width=\"70%\"></td><td>#</td><td>%</td></tr>\n");
		ret.append("</thead>\n");
		ret.append(String.format("<tbody style=\"%s\">\n",html.getString("table.body.default")));
		for (String country: fbc.keySet()) {
			ret.append(MessageFormat.format("<tr><td>{0}</td><td style=\"{2}\">{1,number}</td><td style=\"{2}\">{3,number,0.0}</td></tr>\n",
					country,
					fbc.get(country),
					html.getString("cell.number"),
					fbc.get(country).floatValue() / stats.getTotalCaches().floatValue() * 100F
				)
			);
		}
		ret.append("</tbody>\n");
		ret.append("</table>\n");
		ret.append("</div>\n");
		return ret.toString();
	}
	
	/**
	 * generate a map using google chart api
	 * @param area valid parameters are world, europe, middle_east, asia, africa, south_america
	 * @return
	 */
	private String googleMap(String area) {
		StringBuffer ret = new StringBuffer();
		String headline = messages.getString("msg.areamaps")+messages.getString("area."+area);
		String url="http://chart.apis.google.com/chart?cht=t&amp;chs=378x189&amp;chf=bg,s,EAF7FE&amp;chtm=".concat(area);
		String countryCodes="&amp;chld=";
		String countryValues="&amp;chd=t:";
		TreeMap<String,Integer> fbc =  stats.getFindsByCountry();
		Integer valueCounter = 0;
		
		ret.append("<div style=\"float:left;width:50%;\">\n");
		ret.append(generateHeading(headline+(excludeSomething?"<font style=\"size:9px\">*</font>":"")));
		
		url = url.concat("&amp;chco=FFFFFF,DEB887,DEB887");
		
		for (String country: fbc.keySet()) {
			if (Constants.GCCOUNTRY2ISO.get(country) == null) {
				logger.error("unmapped country "+country);
				continue;
			}
			countryCodes = countryCodes + Constants.GCCOUNTRY2ISO.get(country);
			valueCounter++;
		}
		
		for (int i = 0 ; i < valueCounter-1; i++) {
			countryValues = countryValues.concat("1,");
		}
		
		if (valueCounter > 0) {
			countryValues = countryValues.concat("1");
		}
		
		ret.append(String.format("<img src=\"%s%s%s\" alt=\"\"/>\n", url,countryCodes,countryValues));
		ret.append("</div>\n");
		
		return ret.toString();
	}
	
	/**
	 * generate time line graph showing total progress and annual caching activities 
	 * @return
	 */
	private String timeLine() {
		StringBuffer ret = new StringBuffer();
		HashMap<Integer, Integer[]> mym = stats.getMatrixYearMonthFound();
		
//		String url = "http://chart.apis.google.com/chart?cht=lxy&chxt=r,x,y";
		String url = "http://chart.apis.google.com/chart?cht=lc";
		Calendar firstCachingDay = stats.getFirstCachingDay();
		Calendar today = Calendar.getInstance();
		
		Integer numberOfValues = 0;
		Integer totalCaches = 0;
		Integer bestYear = 0;		
		Integer totalValues[];
		StringBuffer xLabel = new StringBuffer();
		
		xLabel.append("&chxl=0:|");
		for (Integer year = firstCachingDay.get(Calendar.YEAR);year <= today.get(Calendar.YEAR); year++) {
			xLabel.append(year.toString());
			for (Integer month = 0; month <= 11; month++) {
				if ((year == firstCachingDay.get(Calendar.YEAR)) && (month < firstCachingDay.get(Calendar.MONTH))) 
					continue;
				if ((year == today.get(Calendar.YEAR)) && (month > today.get(Calendar.MONTH)))
					continue;
				if (month < 11) xLabel.append("|");
				numberOfValues++;
			}
		}
		
		logger.info(numberOfValues);
		
		totalValues = new Integer[numberOfValues];
				
		Integer valueCounter = 0;
		for (Integer year = firstCachingDay.get(Calendar.YEAR);year <= today.get(Calendar.YEAR); year++) {
			Integer cachesInYear = 0;
			for (Integer month = 0; month <= 11; month++) {
				if (year == firstCachingDay.get(Calendar.YEAR) && month < firstCachingDay.get(Calendar.MONTH)) {
					continue;
				}
				if (year == today.get(Calendar.YEAR) && month > today.get(Calendar.MONTH)) {
					continue;
				}
				cachesInYear = cachesInYear + mym.get(year)[month];
				totalCaches = totalCaches + mym.get(year)[month];
				logger.info("month "+month+" year "+year+" this year "+cachesInYear+" total "+totalCaches);
				totalValues[valueCounter] = totalCaches;
				valueCounter++;
			}
			if (cachesInYear > bestYear) bestYear = cachesInYear;
		}
		
		ret.append("<div style=\"float:left;width:100%;\">\n");
		ret.append(generateHeading(messages.getString("msg.timeline")));
		ret.append(String.format("<img src=\"%s\" alt=\"\">", 
				url
				.concat("&chs=740x300") // size
				.concat(String.format("&chxr=1,0,%d", totalCaches)) // max values for y axis 
				.concat("&chf=bg,s,00000000") // background
				.concat(xLabel.toString()) // x axis labels
				.concat("&chd=e:".concat(ChartDataEncoder.scale(totalValues,totalCaches,true)))
				.concat("&chls=2,1,0") // line styles
				.concat("&chm=B,DEB88740,0,0,0") // fill area color
				.concat("&chco=DEB887") // color of main line
				.concat("&chxt=x,r") // 3 axis
				.concat("") // 
			));
		ret.append("</div>\n");
		
		return ret.toString();
	}
	
	/**
	 * generate HTML version of statistics according to user preferences
	 * @return
	 */
	public String htmlStats() {
		StringBuffer ret = new StringBuffer();
		Integer outlineCounter = 1;
		
		ret.append(statsHeader());
		
		while (null != StatWolf.prefs.getProperty("output.".concat(outlineCounter.toString()))) {
			String[] outline = StatWolf.prefs.getProperty("output.".concat(outlineCounter.toString())).split(",");
			for (String outtype: outline) {
				if (outtype.equals("monthyearfound")) {
					ret.append(matrixYearMonth(stats.getMatrixYearMonthFound(),messages.getString("msg.fpm")));
				} else if (outtype.equals("milestones")) {
					ret.append(milestones());
				} else if (outtype.equals("terraindifficulty")) {
					ret.append(matrixTerrainDifficulty());
				} else if (outtype.equals("cachetype")) {
					ret.append(findsByType());
				} else if (outtype.equals("distancefromhome")) {
					ret.append(findsByDistanceFromHome());
				} else if (outtype.equals("daymonthfound")) {
					ret.append(matrixMonthDay());
				} else if (outtype.equals("difficulty")) {
					ret.append(findsByDT(stats.getCachesByDifficulty(), messages.getString("msg.fbd")));
				} else if (outtype.equals("terrain")) {
					ret.append(findsByDT(stats.getCachesByTerrain(), messages.getString("msg.fbt")));
				} else if (outtype.equals("dayofweek")) {
					ret.append(findsByDayOfWeek());
				} else if (outtype.equals("size")) {
					ret.append(findsByContainer());
				} else if (outtype.equals("monthyearplaced")) {
					ret.append(matrixYearMonth(stats.getMatrixYearMonthPlaced(),messages.getString("msg.fpmplaced")));
				} else if (outtype.equals("compassrose")) {
					ret.append(findsByDirection());
				} else if (outtype.equals("findsbymonthfound")) {
					ret.append(findsByMonth());
				} else if (outtype.equals("datatomb")) {
					ret.append(datatomb());
				} else if (outtype.equals("owner")) {
					ret.append(findsByOwner());
				} else if (outtype.equals("mapworld")) {
					ret.append(googleMap("world"));
				} else if (outtype.equals("mapeurope")) {
					ret.append(googleMap("europe"));
				} else if (outtype.equals("mapmiddleeast")) {
					ret.append(googleMap("middle_east"));
				} else if (outtype.equals("mapasia")) {
					ret.append(googleMap("asia"));
				} else if (outtype.equals("mapafrica")) {
					ret.append(googleMap("africa"));
				} else if (outtype.equals("mapsouthamerica")) {
					ret.append(googleMap("south_america"));
				} else {
					logger.warn("unknown output directive "+outtype+". check preferences.properties");
				}
			}
			ret.append("<div style=\"float:left; width:100%;\"></div>\n");
			outlineCounter++;
		}
		
		ret.append(statsFooter());
		
		return ret.toString();
	}
	
	/**
	 * generate header section of statistics and set basic size parameters
	 * @return
	 */
	private String statsHeader() {
		StringBuffer ret = new StringBuffer();
		String temp=String.format("<div align=\"center\" style=\"%s\">\n", html.getString("outerdiv"));
		ret.append(String.format(temp, html.getString("totalwidth")));
		ret.append("<span style='");
		ret.append("font-family: Tahoma, Arial, sans-serif; font-size: 16px; font-weight: bold;");
		ret.append("'>");
		ret.append(MessageFormat.format(messages.getString("head.summary"),username, stats.getTotalCaches()));
		ret.append("</span><br />\n");
		ret.append("<br />");
		ret.append(MessageFormat.format(messages.getString("head.update"),new Date(System.currentTimeMillis())));
		ret.append("<br /><br />\n");
		return ret.toString();
	}
	
	/**
	 * generate finally section of statistics and close all open tags
	 * @return
	 */
	private String statsFooter() {
		StringBuffer ret = new StringBuffer();
		String temp;
		ret.append("<div style=\"float:left;width:100%;\">\n");
		ret.append(generateHeading(messages.getString("msg.finally")));
		temp = String.format(
					"<p style=\"%s\">%s</p>\n", 
					html.getString("p.finally"),
					messages.getString("msg.footer.1")
				);
		ret.append(MessageFormat.format(temp, "<a href=\"http://statwolf.berlios.de/\">StatWolf</a>", Version.VERSION));
		temp = String.format(
				"<p style=\"%s\">%s</p>\n", 
				html.getString("p.finally"),
				messages.getString("msg.footer.2")
			);
		ret.append(MessageFormat.format(temp, "<a href=\"http://www.cachewolf.de/\">CacheWolf</a>"));
		temp = String.format(
				"<p style=\"%s\">%s</p>\n", 
				html.getString("p.finally"),
				messages.getString("msg.footer.3")
			);
		ret.append(MessageFormat.format(temp, "<a href=\"http://gsak.net/board/index.php?showtopic=4623\">FindStatGen</a>"));
		ret.append("<p/>\n");
		if (excludeSomething) {
			StringBuffer buf = new StringBuffer();
			buf.append("<p style=\"\"><font style=\"size:9px\">*</font> excludes ");
			if (excludeVirtual && excludeLocless) {
				buf.append("Locationless and Virtual");
			} else if (excludeVirtual) {
				buf.append("Virtual");
			} else if (excludeLocless) {
				buf.append("Locationless");
			}
			buf.append(" caches</p><p/>\n");
			ret.append(buf);
		}
		ret.append("</div>\n");
		ret.append("</div>\n");
		return ret.toString();
	}
	
	/**
	 * generate html header for stand alone display and set start marker 
	 * @return
	 */
	private String htmlHeader() {
		StringBuffer ret = new StringBuffer();
		ret.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		ret.append("<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>\n");
		ret.append("<html xmlns='http://www.w3.org/1999/xhtml'>\n");
		ret.append("<head>\n");
		ret.append("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />\n");
		ret.append("<meta http-equiv=\"Content-Style-Type\" content=\"text/css\" />");
		ret.append("<base href=\"http://www.geocaching.com/\" />\n");
		ret.append("<title>");
		ret.append(MessageFormat.format(messages.getString("page.title"), username));
		ret.append("</title>");
		ret.append("</head>\n");
		ret.append("<body>\n");
		ret.append("<!-- ******************** select below to include in gc.com profile ******************** -->\n");
		return ret.toString();
	}
	
	/**
	 * set end marker for stats and close HTML tags
	 * @return
	 */
	private String htmlFooter() {
		StringBuffer ret = new StringBuffer();
		ret.append("<!--  ******************** select above to include in gc.com profile ********************  -->\n");
		ret.append("</body>\n</html>\n");
		return ret.toString();
	}
	
	/**
	 * encode enough of the string to make it go through the XML validator
	 * @param str string to encode
	 * @return string with &, >, < replaced by their entity encoding
	 */
	private String escapeXML(String str) {
		return str.replace(">", "&gt;").replace("<", "&lt;").replace("&", "&amp;");
	}
}

package de.berlios.statwolf;

import java.util.*;
import org.apache.log4j.*;
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
	private Properties prefs;

	public HTMLOutput(StatisticsData stats, Properties prefs) {
		
		String htmlSchema;
		String locale;
		
		this.prefs = prefs;
		
		distUnit = prefs.getProperty("distunit", "km");
		username = prefs.getProperty("username", "UnkownCacher");
		locale = prefs.getProperty("locale", "en");
		htmlSchema = "html_".concat(prefs.getProperty("htmlschema", "default"));
		
		this.stats = stats;
		html = ResourceBundle.getBundle(htmlSchema);
		try {
			//TODO: switch from resource to properties
			Properties html = new Properties();
			html.load(this.getClass().getClassLoader().getResourceAsStream(htmlSchema.concat(".properties")));
		} catch (Exception ex) {
			logger.fatal(ex.getLocalizedMessage());
			logger.debug(ex.getStackTrace());
			System.exit(1);
		}
		
		Locale.setDefault(new Locale(locale));
		messages = ResourceBundle.getBundle("messages");
	}

	public String generateHTML() {
		
		StringBuffer out = new StringBuffer();
		out.append(full_header());
		out.append(stat_header());
		out.append(headline());
		out.append("<div style=\"float:left; width:100%;\"></div>\n");
		out.append(matrixYearMonth());
		out.append("<div style=\"float:left; width:100%;\"></div>\n");
		out.append(milestones());
		out.append(matrixTerrainDifficulty());
		out.append("<div style=\"float:left; width:100%;\"></div>\n");
		out.append(findsByType());
		out.append(findsByDistanceFromHome());
		out.append("<div style=\"float:left; width:100%;\"></div>\n");
		out.append(matrixMonthDay());
		out.append("<div style=\"float:left; width:100%;\"></div>\n");
		out.append(findsByDT(stats.getCachesByDifficulty(), messages.getString("msg.fbd")));
		out.append(findsByDT(stats.getCachesByTerrain(), messages.getString("msg.fbt")));
		out.append("<div style=\"float:left; width:100%;\"></div>\n");
		out.append(findsByDayOfWeek());
		out.append(findsByContainer());
		out.append("<div style=\"float:left; width:100%;\"></div>\n");
		out.append(findsByDirection());
		out.append(findsByMonth());
		// month (50%)
		out.append("<div style=\"float:left; width:100%;\"></div>\n");
		// year cache placed (50%)
		// find till today per year (50%)
		out.append("<div style=\"float:left; width:100%;\"></div>\n");
		// finds placed by month / year
		out.append("<div style=\"float:left; width:100%;\"></div>\n");
		out.append(datatomb());
		out.append("<div style=\"float:left; width:100%;\"></div>\n");
		out.append(findsByOwner());
		
		out.append(stat_footer());
		out.append(footer());
		
		String tempdir = System.getProperty("java.io.tmpdir");
		if ( !(tempdir.endsWith("/") || tempdir.endsWith("\\")) ) {
		   tempdir = tempdir + System.getProperty("file.separator");
		}
		
		String OutFileName = tempdir.concat("cw-statistik.html");

		try {
			BufferedWriter of = new BufferedWriter(new FileWriter(
					OutFileName));
			of.write(out.toString());
			of.close();
			return OutFileName;
		} catch (IOException e) {
			return null;
		}

	}

	private String stat_header() {
		StringBuffer ret = new StringBuffer();
		String temp=String.format("<div align=\"center\" style=\"%s\">\n", html.getString("outerdiv"));
		ret.append(String.format(temp, html.getString("totalwidth")));
		return ret.toString();
	}

	private String full_header() {
		StringBuffer ret = new StringBuffer();
		ret.append("<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>\n");
		ret.append("<html xmlns='http://www.w3.org/1999/xhtml'>\n");
		ret.append("<head>\n");
		ret.append("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />\n");
		ret.append("<base href=\"http://www.geocaching.com/\" />\n");
		ret.append("<title>");
		ret.append(MessageFormat.format(messages.getString("page.title"), username));
		ret.append("</title>");
		ret.append("</head>\n");
		ret.append("<body>\n");
		ret.append("<!-- *********************************************************************** -->\n");
		return ret.toString();
	}

	private String stat_footer() {
		String temp;
		StringBuffer ret = new StringBuffer();
		ret.append("<div style=\"float:left; width:100%;\">\n");
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
		ret.append("<p/></div>\n");
		ret.append("</div>\n");
		ret.append("<!-- *********************************************************************** -->\n");
		return ret.toString();
	}

	private String footer() {
		return "</body>\n</html>\n";
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
		ret.append(String.format("<thead style=\"%s\">\n", html.getString("matrix.head2")));
		ret.append(String.format("<tr><td style=\"%s\"></td><td></td>", html.getString("matrix.head1")));
		for (Float i : Constants.TERRDIFF) {
			ret.append(MessageFormat.format("<td>{0,number,0.0}</td>", i));
		}
		ret.append("</tr>\n");
		ret.append("</thead>\n");
		
		HashMap<Float, HashMap<Float, Integer>> mtd = new HashMap<Float, HashMap<Float, Integer>>();
		mtd = stats.getMatrixTerrDiff();
		ret.append(String.format("<tbody style=\"%s\">\n", html.getString("matrix.body")));
		for (Float terr : Constants.TERRDIFF) {
			ret.append("<tr>");
			if (terr == 1.0F) {
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
					terr));
			for (Float diff : Constants.TERRDIFF) {
				ret.append(MessageFormat.format("<td>{0}</td>", 
						mtd.get(terr).get(diff) == 0 ? "&nbsp;" : mtd.get(terr).get(diff)));
				combinations = combinations + (mtd.get(terr).get(diff) == 0 ? 0 : 1);
			}
			ret.append("</tr>\n");
		}

		ret.append("</tbody>\n</table>\n</div>\n");
		ret.append(MessageFormat.format(messages.getString("msg.combination"),
				combinations, 81));
		return ret.toString();
	}

	private String matrixMonthDay() {
		
		HashMap<Integer, HashMap<Integer, Integer>> mmd = new HashMap<Integer, HashMap<Integer, Integer>>();
		mmd = stats.getMatrixMonthDay();

		StringBuffer ret = new StringBuffer();
		Integer combinations = 0;

		ret.append("<div style=\"float:left;width:100%;\">");
		ret.append(generateHeading(messages.getString("msg.fpd")));

		ret.append("<table style=\"width:98%;table-layout:fixed;\">\n");
		ret.append(String.format("<thead style=\"%s\">\n", html.getString("matrix.head1")));
		ret.append(String.format("<tr><td></td><td></td><td colspan=\"12\">%s</td></tr>",messages.getString("msg.month")));
		ret.append("</thead>\n");
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
						mmd.get(month).get(day) == 0 ? "" : mmd.get(month).get(day)
						)
					);
				combinations = combinations	+ (mmd.get(month).get(day) == 0 ? 0 : 1);
			}
			ret.append("</tr>\n");
		}
		ret.append("</body>\n</table>\n");
		ret.append(MessageFormat.format(messages.getString("msg.combination"), combinations, 366));
		ret.append("</div>\n");
		return ret.toString();
	}

	private String matrixYearMonth() {
		HashMap<Integer, HashMap<Integer, Integer>> mym = new HashMap<Integer, HashMap<Integer, Integer>>();
		mym = stats.getMatrixYearMonth();
		Set<Integer> years = new TreeSet<Integer>(mym.keySet());

		StringBuffer ret = new StringBuffer();

		ret.append("<div id='mym'>");
		ret.append(generateHeading(messages.getString("msg.fpm")));

		ret.append("<table style=\"width:98%;table-layout:fixed\">\n");
		ret.append(String.format("<thead style=\"%s\">\n", html.getString("matrix.head1")));
		ret.append("<tr><td></td><td></td><td colspan='12' align=\"center\">");
		ret.append(messages.getString("msg.month"));
		ret.append("</td></tr>\n");
		ret.append("</thead>\n");
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
						mym.get(year).get(month) == 0 ? "&nbsp;" : mym.get(year).get(month)
						)
					);
			}

			ret.append("</tr>\n");
		}
		ret.append("</tbody>\n</table>\n</div>\n");
		return ret.toString();
	}

	private String headline() {
		StringBuffer ret = new StringBuffer();
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

	private String findsByDT(HashMap<Float,Integer> mftd, String heading) {
		StringBuffer ret = new StringBuffer();
		HashMap<Float, Float> percent = new HashMap<Float, Float>();
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
		for (Float i : Constants.TERRDIFF) {
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
		for (Float i : Constants.TERRDIFF) {
			ret.append(MessageFormat.format(
					"<tr>" +
					"<td>{0,number,#,##0.0}</td>" +
					"<td style=\"{5}\">{1}</td>" +
					"<td style=\"{5}\">{2,number,#,##0.0}</td>" +
					"<td><img src=\"{3}\" height=\"15\" width=\"{4}\" alt=\"{2,number,#,##0.0}%\"/></td>" +
					"</tr>\n",
					i,
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
		HashMap <String,Integer> fbc = stats.getCachesByContainer();
		HashMap<String,Float> percent = new HashMap<String,Float>();
		Float maxPercent = 0.0F;
		
		for (String cont : Constants.CONTAINERS) {
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
		
		for (String cont : Constants.CONTAINERS) {
			ret.append(MessageFormat.format(
					"<tr>" +
					"<td><img src=\"{0}\" alt=\"\"/> {1}</td>" +
					"<td style=\"{6}\">{2}</td>" +
					"<td style=\"{6}\">{3,number,#,##0.0}</td>" +
					"<td><img src=\"{4}\" height=\"15\" width=\"{5}\" alt=\"{3,number,#,##0.0}%\"/></td>" +
					"</tr>\n", 
					Constants.SIZEIMAGES.get(cont), 
					cont,
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
	        		value.found.getTime(),
	        		daysBetween(lastMilestone,value.found),
	        		cacheLink(value.id),
	        		Constants.TYPEIMAGES.get(value.type),
	        		value.name,
	        		html.getString("cell.number")
	        	)
	        );
	        lastMilestone = value.found;
		}
		ret.append("</tbody>\n");
		ret.append("</table>\n");
		ret.append(crystalball());
		ret.append("</div>\n");
		return ret.toString();
	}
	
	private Integer daysBetween(Calendar ob1, Calendar ob2) {
		Integer delta = 0;
		
		if ( ob1 == null || ob2 == null) {
			return null;
		}
		ob1.set(Calendar.HOUR_OF_DAY, 0);
		ob1.set(Calendar.MINUTE, 0);
		ob1.set(Calendar.SECOND, 0);
		ob1.set(Calendar.MILLISECOND, 0);
		ob2.set(Calendar.HOUR_OF_DAY, 0);
		ob2.set(Calendar.MINUTE, 0);
		ob2.set(Calendar.SECOND, 0);
		ob2.set(Calendar.MILLISECOND, 0);
		
		if (ob1.compareTo(ob2) == 0) {
			return 0;
		}
		
		if (ob1.compareTo(ob2) > 0) {
			Calendar temp = ob1;
			ob1 = ob2;
			ob2 = temp;
		}
		
		while (ob1.compareTo(ob2) < 0) {
			delta++;
			ob1.add(Calendar.DAY_OF_MONTH, 1);
		}
		
		return delta;
	}
	
	private String datatomb () {
		StringBuffer ret = new StringBuffer();
		Integer totalDays = daysBetween(stats.getFirstCachingDay(), Calendar.getInstance());
		String strTemp;
		
		String rawline = "<tr><td style=\"\">%s</td><td>%s</td></tr>\n";
		
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
			String outline=String.format(rawline, messages.getString("msg.cache.".concat(dir)),messages.getString("msg.cache.most"));
			ret.append(MessageFormat.format(outline,
					(dir == "north" || dir == "south")?formatLatLon(mostXxxxCache.get(dir).lat, "lat"):formatLatLon(mostXxxxCache.get(dir).lon, "lon"),
					cacheLink(mostXxxxCache.get(dir).id),
					mostXxxxCache.get(dir).name
				)
			);
		}
		ret.append(String.format("<tr><td>%s</td><td><a href=\"http://maps.google.de/maps?q=%s,%s\">%s %s</a></td></tr>\n",
				messages.getString("msg.cachemedian"),
				stats.getCacheMedian().getLatitude(),
				stats.getCacheMedian().getLongitude(),
				formatLatLon(stats.getCacheMedian().getLatitude(), "lat"),
				formatLatLon(stats.getCacheMedian().getLongitude(), "lon"))
			);

		ret.append("</tbody>\n");
		ret.append("</table>\n");
		ret.append("</div>\n");
		return ret.toString();

	}
	
	private String generateHeading(String heading) {
		return String.format("<p style=\"%s\">%s</p>\n",
				html.getString("p.heading"),
				heading
			);
	}
	
	private String findsByOwner() {
		StringBuffer ret = new StringBuffer();
		TreeSet<UserNumber> cachesByOwnerSorted = stats.getCachesByOwnerSorted();
		Integer numberOfOwners = (int) Math.floor(
				(Double.parseDouble(prefs.getProperty("ownernumber", "20"))+1.0)/2.0
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
				prefs.getProperty("username")
				)
			);
		ret.append("</div>\n");
		return ret.toString();
	}
	
	private String findsByDirection() {
		HashMap<String,Integer> cbd = stats.getCachesByDirection();
		Integer maxCount = 0;
		StringBuffer chartData = new StringBuffer("&chd=t:");
		StringBuffer chartHead = new StringBuffer("&chxl=0:");

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
			.concat("&chs=250x250") // TODO: compute from outer div size
			.concat("&chco=FF0000") // TODO: get color from profile
			.concat("&chls=2.0,4.0,0.0")
			.concat("&chxt=x")
			.concat("&chf=bg,s,00000000")
			.concat("&chxr=0,0.0,360.0")
			.concat(chartHead.toString()) 
			.concat(chartData.toString())
			.concat(String.format("&chds=0,%s", maxCount)); 
		ret.append("<div style=\"float:left;width:50%;\">\n");
		ret.append(generateHeading(messages.getString("msg.findsbydirection")));
		ret.append(String.format("<p align=\"center\"><img src=\"%s\" alt=\"\"/></p>", charturl));
		ret.append("</div>\n");
		return ret.toString();
	}
	
	private String findsByMonth() {
		HashMap <Integer,Integer> fbm = stats.getFindsByMonth();
		StringBuffer ret = new StringBuffer();
		Integer maxCount = 0;
		for (int i = 0; i < 12; i++) {
			if ( maxCount < fbm.get(i)) {
				maxCount = fbm.get(i);
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
		for (int i = 0; i < 12; i++) {
			ret.append(MessageFormat.format("<tr><td>{0}</td><td style=\"{4}\">{1,number,#,##0}</td><td style=\"{4}\">{2,number,#,##0.0}</td><td>{3}</td></tr>\n",
					messages.getString("mon.long."+i),
					fbm.get(i),
					calcCachePercent(fbm.get(i)),
					createHorizontalBar(fbm.get(i),maxCount),
					html.getString("cell.number")
				)
			);
		}
		ret.append("</tbody>\n");
		ret.append("</table>\n");
		ret.append("</div>\n");
		return ret.toString();
	}
	
	private String cacheLink(String id) {
		return MessageFormat.format("<a href=\"http://coord.info/{0}\">{0}</a>", id);
	}
	
	private String ownerLink(String id) {
		String ret = "";
		try {
			ret =String.format("<a href=\"http://www.geocaching.com/profile/?u=%s\">%s</a>", 
					URLEncoder.encode(id, "UTF-8"),
					id);
		} catch (Exception ignore) { }
		return ret;
	}
	
	private String createHorizontalBar(Integer count, Integer maxCount) {
		Integer width = (int) Math.floor(count.floatValue() / maxCount.floatValue() * Constants.MAXHORIZONTALBARLENGTH);
		return MessageFormat.format("<img src=\"{0}\" height=\"15\" width=\"{1}\" alt=\"{2,number,#,##0.0}%\"/>",
				html.getString("bar.horizontal"),
				width.toString(),
				calcCachePercent(count)
			);
	}
	
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
}

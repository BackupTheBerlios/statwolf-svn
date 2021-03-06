package de.berlios.statwolf;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import de.cachewolf.CacheSize;

public class HTMLOutput {

	private final transient StatisticsData stats;
	
	private final transient ResourceBundle messages;
	
	private final transient String distUnit;
	
	private final transient String username;
	
	private final transient ResourceBundle html;
	/** reference to default log4j logger. */
	private static final Logger LOGGER = Logger.getLogger(HTMLOutput.class);
	/** exclude virtual caches as specified in preferences. */
	private transient Boolean excludeVirtual;
	/** exclude locationless caches as specified in preferences. */
	private transient Boolean excludeLocless;
	/** exclude virtual or locationless caches (calculated). */ 
	private transient Boolean excludeSomething;
	/** structuring div of width 100% */
	private static final String DIVDELIM = "<div style=\"float:left;width:100%;\"/>\n";
	/** declaration for DIV of width 100%. */
	private static final String DIV100 = "<div style=\"float:left;width:100%;\">\n";
	/** declaration for DIV of width 50%. */
	private static final String DIV50 = "<div style=\"float:left;width:50%;\">\n";
	
	private final Properties prefs;

	public HTMLOutput(final StatisticsData paramStats, final Properties prefs) {
		
		this.prefs = prefs;
		String htmlSchema;
		String locale;
		
		distUnit = prefs.getProperty("distunit", "km");
		username = prefs.getProperty("username");
		if (username == null) {
			LOGGER.error("username not set, please check preferences");
			System.exit(1);
		}
		try {
			excludeVirtual = Boolean.parseBoolean(prefs.getProperty("excludevirtual", "false"));
			excludeLocless = Boolean.parseBoolean(prefs.getProperty("excludelocless", "false"));
			excludeSomething = excludeVirtual || excludeLocless;
		} catch (Exception ex) {
			LOGGER.error("error when parsing exsclude* properties", ex);
		}
		locale = prefs.getProperty("locale", "en");
		htmlSchema = "html_".concat(prefs.getProperty("htmlschema", "default"));
		
		stats = paramStats;
		html = ResourceBundle.getBundle(htmlSchema);
		try {
			final Properties html = new Properties();
			html.load(this.getClass().getClassLoader().getResourceAsStream(htmlSchema.concat(".properties")));
		} catch (Exception ex) {
			LOGGER.fatal("unable to load html schema");
			LOGGER.debug(ex);
			System.exit(1);
		}
		
		Locale.setDefault(new Locale(locale));
		messages = ResourceBundle.getBundle("messages");
	}

	/**
	 * write he generated output to the systems directory for temporary 
	 * files as StatWolf.html .
	 * @return the name of the written file on success, null on failure
	 */
	public final String generateHTML() {
		final StringBuffer output = new StringBuffer();
		String ret = null;
		
		output.append(htmlHeader());
		output.append(htmlStats());
		output.append(htmlFooter());
		
		String outdir = prefs.getProperty("outputdir", System.getProperty("java.io.tmpdir"));
		
		if (!(outdir.endsWith("/") || outdir.endsWith("\\"))) {
		   outdir = outdir.concat(System.getProperty("file.separator"));
		}

		final String outFileName = outdir.concat("StatWolf.html");

		try {
			final BufferedWriter outfile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFileName), "UTF8"));
			outfile.write(output.toString());
			outfile.close();
			ret = outFileName;
		} catch (IOException ex) {
			LOGGER.fatal("Error saving HTML output");
			LOGGER.debug(ex);
		}
		return ret;
	}

	private String matrixTerrainDifficulty() {

		final StringBuffer ret = new StringBuffer(256);
		Integer combinations = 0;

		ret.append(DIV100);
		ret.append(generateHeading(messages.getString("msg.tdm")));

		ret.append("<table style=\"table-layout,fixed;width:98%;\">\n");
		ret.append(String.format("<thead style=\"%s\">\n", html.getString("matrix.head1")));
		ret.append(String.format("<tr><td></td><td></td><td colspan='9'>%s</td></tr>\n", messages.getString("msg.diff")));
		ret.append("</thead>");
		//FIXME: the second THEAD violates DTD - however most browsers render it correct
		ret.append(String.format("<thead style=\"%s\">\n", html.getString("matrix.head2")));
		ret.append(String.format("<tr><td style=\"%s\"></td><td></td>", html.getString("matrix.head1")));
		for (Integer i : Constants.TERRDIFF) {
			ret.append(MessageFormat.format("<td>{0,number,0.0}</td>", i / 10F));
		}
		ret.append("</tr>\n");
		ret.append("</thead>\n");
		
		HashMap < Integer, HashMap < Integer, Integer >>mtd = new HashMap < Integer, HashMap < Integer, Integer >>();
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
					terr / 10F));
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
		
		final Integer[][] mmd = stats.getMatrixMonthDay();

		final StringBuffer ret = new StringBuffer(256);
		Integer combinations = 0;

		ret.append(DIV100);
		ret.append(generateHeading(messages.getString("msg.fpd")));

		ret.append("<table style=\"width:98%;table-layout:fixed;\">\n");
		ret.append(String.format("<thead style=\"%s\">\n", html.getString("matrix.head1")));
		ret.append(String.format("<tr><td></td><td></td><td colspan=\"12\">%s</td></tr>", messages.getString("msg.month")));
		ret.append("</thead>\n");
		//FIXME: the second THEAD violates the DTD - however most browsers render it correct
		ret.append(String.format("<thead style=\"%s\">\n", html.getString("matrix.head2")));
		ret.append(String.format("<tr><td style=\"%s\"></td><td></td>", html.getString("matrix.head1")));
		for (int mon = 0; mon < 12; mon++) {
			ret.append(String.format("<td>%s</td>", messages.getString("mon.short."	+ mon)));
		}

		ret.append("</tr>\n");
		ret.append("</thead>\n");
		ret.append(String.format("<tbody style=\"%s\">\n", html.getString("matrix.body")));
		for (int day = 1; day <= 31; day++) {
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

	private String matrixYearMonth(final Map<Integer, Integer[]> mym, final String headline) {
		final Set<Integer> years = new TreeSet<Integer>(mym.keySet());

		final StringBuffer ret = new StringBuffer(256);

		ret.append(DIV100);
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
					messages.getString("mon.short." + month)));
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
		final HashMap<Integer, Integer> fbt = stats.getCachesByType();
		final TreeSet<Integer> mapKeys = new TreeSet<Integer>(fbt.keySet());
		final HashMap <Integer, Float> percent = new HashMap <Integer, Float>(); 
		Float maxPercent = 0.0F;
		
		for (Integer type : mapKeys) {
			if (fbt.get(type) == null) {
				percent.put(type, 0.0F);
			} else {
				final Float tmp = calcCachePercent(fbt.get(type));
				percent.put(type, tmp);
				if (tmp > maxPercent) {
					maxPercent = tmp;
				}
			}
		}
		
		final StringBuffer ret = new StringBuffer(512);
		ret.append(DIV50);
		ret.append(generateHeading(messages.getString("msg.fbtype")));
		ret.append("<table width=\"98%\" style=\"table-layout:fixed\">\n");
		ret.append(String.format("<thead style=\"%s\">\n", html.getString("table.head.default")));
		ret.append("<tr>"
				+ "<td style=\"width:35%;\"></td>"
				+ "<td style=\"width:13%;\">#</td>"
				+ "<td style=\"width:9%;\">%</td>"
				+ "<td style=\"\"></td>"
				+ "</tr>\n");
		ret.append("</thead>\n");
		ret.append(String.format("<tbody style=\"%s\">\n", html.getString("table.body.default")));
		for (Integer type : mapKeys) {
			ret.append(MessageFormat.format(
					"<tr>"
					+ "<td><img src=\"{0}\" alt=\"\"/> {1}</td><td style=\"{6}\">{2}</td>"
					+ "<td style=\"{6}\">{3,number,#,##0.0}</td>"
					+ "<td><img src=\"{4}\" width=\"{5}\" height=\"15\" alt=\"{3,number,#,##0.0}%\"/></td>"
					+ "</tr>\n", 
					Constants.TYPEIMAGES.get(type),
					messages.getString("cachetype." + type),
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

	private String findsByDT(final Map<Integer,Integer> mftd, final String heading) {
		final StringBuffer ret = new StringBuffer(512);
		final HashMap<Integer, Float> percent = new HashMap<Integer, Float>();
		Float maxPercent = 0.0F;
		
		ret.append(DIV50);
		ret.append(generateHeading(heading));
		ret.append("<table width=\"98%\" style=\"table-layout:fixed\">\n");
		ret.append(String.format("<thead style=\"%s\">\n", html.getString("table.head.default")));
		ret.append("<tr>" 
				+ "<td style=\"width:35%;\"></td>" 
				+ "<td style=\"width:13%;\">#</td>" 
				+ "<td style=\"width:9%;\">%</td>" 
				+ "<td style=\"\"></td>" 
				+ "</tr>\n");
		ret.append("</thead>\n");
		ret.append(String.format("<tbody style=\"%s\">\n", html.getString("table.body.default")));
		for (Integer i : Constants.TERRDIFF) {
			if (mftd.get(i) == null) {
				percent.put(i, 0.0F);
			} else {
				final Float tmp = calcCachePercent(mftd.get(i));
				percent.put(i, tmp);
				if (tmp > maxPercent) {
					maxPercent = tmp;
				}
			}
		}
		for (Integer i : Constants.TERRDIFF) {
			ret.append(MessageFormat.format(
					"<tr>" 
					+ "<td>{0,number,#,##0.0}</td>" 
					+ "<td style=\"{5}\">{1}</td>" 
					+ "<td style=\"{5}\">{2,number,#,##0.0}</td>" 
					+ "<td><img src=\"{3}\" height=\"15\" width=\"{4}\" alt=\"{2,number,#,##0.0}%\"/></td>" 
					+ "</tr>\n",
					i / 10F,
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

	private Integer calculateBarLength(final Float actual, final Float max) {
		return (int) Math.round(actual / max * Constants.MAXHORBARLENGTH);
	}
	
	private String findsByContainer() {
		final StringBuffer ret = new StringBuffer(512);
		final HashMap <Integer, Integer> fbc = stats.getCachesByContainer();
		final HashMap<Integer, Float> percent = new HashMap<Integer, Float>();
		Float maxPercent = 0.0F;
		
		for (Integer cont : Constants.CONTAINERS) {
			if (fbc.get(cont) == null) {
				percent.put(cont, 0.0F);
			} else {
				final Float tmp = calcCachePercent(fbc.get(cont));
				percent.put(cont, tmp);
				if (tmp > maxPercent) {
					maxPercent = tmp;
				}				
			}
		}
		
		ret.append(DIV50);
		ret.append(generateHeading(messages.getString("msg.fbcontainer")));
		ret.append("<table width=\"98%\" style=\"table-layout:fixed\">\n");
		ret.append(String.format("<thead style=\"%s\">\n", html.getString("table.head.default")));
		ret.append("<tr>" 
				+ "<td style=\"width:35%;\"></td>" 
				+ "<td style=\"width:13%;\">#</td>" 
				+ "<td style=\"width:9%;\">%</td>" 
				+"<td style=\"\"></td>" 
				+ "</tr>\n");
		ret.append("</thead>\n");
		ret.append(String.format("<tbody style=\"%s\">\n", html.getString("table.body.default")));
		
		for (Integer cont : Constants.CONTAINERS) {
			ret.append(MessageFormat.format(
					"<tr>" 
					+ "<td><img src=\"{0}\" alt=\"\"/> {1}</td>" 
					+ "<td style=\"{6}\">{2}</td>" 
					+ "<td style=\"{6}\">{3,number,#,##0.0}</td>" 
					+ "<td><img src=\"{4}\" height=\"15\" width=\"{5}\" alt=\"{3,number,#,##0.0}%\"/></td>" 
					+ "</tr>\n", 
					Constants.SIZEIMAGES.get(cont), 
					CacheSize.cw2ExportString(cont.byteValue()),
					fbc.get(cont) == null ? "" : fbc.get(cont), 
					fbc.get(cont) == null ? "" : percent.get(cont),
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
		final StringBuffer ret = new StringBuffer(512);
		final HashMap <Integer, Integer> fbc = stats.getCachesByDayOfWeek();
		final HashMap<Integer, Float> percent = new HashMap<Integer, Float>();
		Float maxPercent = 0.0F;
		
		for (int dow = 1; dow < 8; dow++) {
			if (fbc.get(dow) == null) {
				percent.put(dow, 0.0F);
			} else {
				final Float tmp = calcCachePercent(fbc.get(dow));
				percent.put(dow, tmp);
				if (tmp > maxPercent) {
					maxPercent = tmp;
				}
			}
		}
		
		ret.append(DIV50);
		ret.append(generateHeading(messages.getString("msg.fbdayofweek")));
		ret.append("<table width=\"98%\" style=\"table-layout:fixed\">\n");
		ret.append(String.format("<thead style=\"%s\">\n", html.getString("table.head.default")));
		ret.append("<tr>" 
				+ "<td style=\"width:35%;\"></td>" 
				+ "<td style=\"width:13%;\">#</td>" 
				+ "<td style=\"width:9%;\">%</td>" 
				+ "<td style=\"\"></td>" 
				+ "</tr>\n");
		ret.append("</thead>\n");
		ret.append(String.format("<tbody style=\"%s\">\n", html.getString("table.body.default")));
		for (int dow = 1; dow < 8; dow++) {
			ret.append(MessageFormat.format(
					"<tr>" 
					+ "<td>{0}</td>" 
					+ "<td style=\"{5}\">{1}</td>" 
					+ "<td style=\"{5}\">{2,number,#,##0.0}</td>" 
					+ "<td><img src=\"{3}\" height=\"15\" width=\"{4}\" alt=\"{2,number,#,##0.0}%\"/></td>" 
					+ "</tr>\n", 
					messages.getString("dow.long." + dow),
					fbc.get(dow) == null ? 0 : fbc.get(dow), 
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
	
	private String formatLatLon(final Float pos, final String latlon) {
		final StringBuffer ret = new StringBuffer();
		String direction;
		Float absPos;
		Integer degree;
		Double minutes;
		
		absPos = Math.abs(pos);
		degree = absPos.intValue();
		minutes = (absPos - degree.doubleValue()) * 60.0;
		
		if ("lat".equals(latlon)) { 
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
		} else if ("lon".equals(latlon)) { 
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
		final TreeMap<Integer, Integer> dfh = stats.getDistanceFromHome();
		final StringBuffer ret = new StringBuffer(256);
		final HashMap<Integer, Float> percent = new HashMap<Integer, Float>();
		Float maxPercent = 0.0F;
		final Integer[] dist = {10, 20, 30, 40, 50, 100, 200, 300, 400, 500, 1000, 9999};
		for (Integer i : dist) {
			if (dfh.get(i) == null) {
				percent.put(i, 0.0F);
			} else {
				final Float tmp = calcCachePercent(dfh.get(i));
				percent.put(i, tmp);
				if (tmp > maxPercent) {
					maxPercent = tmp;
				}				
			}
		}
		ret.append(DIV50);
		ret.append(generateHeading(MessageFormat.format(messages.getString("msg.fbdistance"), distUnit)));
		ret.append("<table style=\"table-layout:fixed; width:98%\">");
		ret.append(String.format("<thead style=\"%s\">\n", html.getString("table.head.default")));
		ret.append("<tr>"
				+ "<td style=\"width:35%;\"></td>" 
				+ "<td style=\"width:13%;\">#</td>" 
				+ "<td style=\"width:9%;\">%</td>" 
				+ "<td style=\"\"></td>" 
				+ "</tr>\n");
		ret.append("</thead>\n");
		ret.append(String.format("<tbody style=\"%s\">\n", html.getString("table.body.default")));
		ret.append(formatDistanceFromHome("[ 0, 10 ]",  dfh.get(10), percent.get(10), maxPercent));
		ret.append(formatDistanceFromHome("( 10, 20 ]", dfh.get(20), percent.get(20), maxPercent));
		ret.append(formatDistanceFromHome("( 20, 30 ]", dfh.get(30), percent.get(30), maxPercent));
		ret.append(formatDistanceFromHome("( 30, 40 ]", dfh.get(40), percent.get(40), maxPercent));
		ret.append(formatDistanceFromHome("( 40, 50 ]", dfh.get(50), percent.get(50), maxPercent));
		ret.append(formatDistanceFromHome("( 50, 100 ]", dfh.get(100), percent.get(100), maxPercent));
		ret.append(formatDistanceFromHome("( 100, 200 ]", dfh.get(200), percent.get(200), maxPercent));
		ret.append(formatDistanceFromHome("( 200, 300 ]", dfh.get(300), percent.get(300), maxPercent));
		ret.append(formatDistanceFromHome("( 300, 400 ]", dfh.get(400), percent.get(400), maxPercent));
		ret.append(formatDistanceFromHome("( 400, 500 ]", dfh.get(500), percent.get(500), maxPercent));
		ret.append(formatDistanceFromHome("( 500, 1000 ]", dfh.get(1000), percent.get(1000), maxPercent));
		ret.append(formatDistanceFromHome("( 1000, \u221E )", dfh.get(9999), percent.get(9999), maxPercent));
		ret.append("</tbody>\n");
		ret.append("</table>\n");
		ret.append("</div>\n");
		return ret.toString();
	}
	
	private String formatDistanceFromHome(final String label, final Integer count, final Float percent, final Float maxPercent) {
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
	
	private Float calcCachePercent(final Integer count) {
		return Math.round(count.floatValue() / stats.getTotalCaches().floatValue() * 1000.0F) / 10.0F;
	}
	
	/**
	 * generate list of milestone caches. displays first cahce found and the very % 100 cache
	 * @return
	 */
	private String milestones() {
		final TreeMap<Integer, Cache> milestones = stats.getMilestones();
		Calendar lastMilestone = null;
		final StringBuffer ret = new StringBuffer(512);
		ret.append(DIV100);
		ret.append(generateHeading(messages.getString("msg.milestones")));
		ret.append("<table style=\"width:98%;\">\n");
		ret.append(String.format("<thead style=\"%s\">\n", html.getString("table.head.default")));
		ret.append("<tr><td>#</td><td>Date</td><td>\u0394 Days</td><td>Waypoint</td><td>Cache</td></tr>\n");
		ret.append("</thead>\n");
		ret.append(String.format("<tbody style=\"%s\">\n", html.getString("table.body.default")));
		for (Map.Entry<Integer, Cache> entry : milestones.entrySet()) {
	        final Cache value = entry.getValue();
	        final Integer key = entry.getKey();
	        ret.append(MessageFormat.format("<tr>" 
	        		+ "<td style=\"{6}\">{0}</td>" 
	        		+ "<td>{1,date,medium}</td>" 
	        		+ "<td style=\"{6}\">{2}</td>" 
	        		+ "<td>{3}</td>" 
	        		+ "<td><img src=\"{4}\" alt=\"\"/> {5}</td>" 
	        		+ "</tr>\n", 
	        		key,
	        		value.getFoundDate().getTime(),
	        		stats.daysBetween(lastMilestone, value.getFoundDate()),
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
	 * calculate numerous values for the datatomb.
	 * @return
	 */
	private String datatomb() {
		final StringBuffer ret = new StringBuffer(256);
		final Integer totalDays = stats.getDaysSinceFirstFind();
		String strTemp;
		
		final HashMap<String, Cache> mostXxxxCache = stats.getMostXxxCache();
		ret.append(DIV100);
		ret.append(generateHeading(messages.getString("msg.datatomb")));
		ret.append("<table style=\"table-layout:fixed;width:98%\">\n");
		ret.append(String.format("<tbody style=\"%s\">\n", html.getString("table.body.default")));
		
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
		
		final String[] dirs = { "north", "south", "west", "east"};
		for (String dir :  dirs) {
			final String outline = String.format("<tr><td style=\"\">%s%s</td><td>%s</td></tr>\n", 
					messages.getString("msg.cache.".concat(dir)),
					excludeSomething?"<font style=\"size:9px\">*</font>":"",
					messages.getString("msg.cache.most")
				);
			ret.append(MessageFormat.format(outline,
					("north".equals(dir) || "south".equals(dir)) ? formatLatLon(mostXxxxCache.get(dir).getLat(), "lat") : formatLatLon(mostXxxxCache.get(dir).getLon(), "lon"),
					cacheLink(mostXxxxCache.get(dir).getId()),
					mostXxxxCache.get(dir).getName()
				)
			);
		}
		ret.append(String.format("<tr><td>%s%s</td><td><a href=\"http://maps.google.de/maps?q=%s,%s\">%s %s</a></td></tr>\n",
				messages.getString("msg.cachemedian"),
				excludeSomething ? "<font style=\"size:9px\">*</font>" : "",
				stats.getCacheMedian().getLatitude(),
				stats.getCacheMedian().getLongitude(),
				formatLatLon(stats.getCacheMedian().getLatitude(), "lat"),
				formatLatLon(stats.getCacheMedian().getLongitude(), "lon"))
			);
		
		strTemp = String.format("<tr><td>%s</td><td>%s</td></tr>\n",
				messages.getString("msg.cachetocachedistance"),
				"\u2211 {1,number,#,##0.0} {2} \u2205 {3,number,#,##0.0} {2}");
		ret.append(MessageFormat.format(strTemp,
				excludeSomething ? "<font style=\"size:9px\">*</font>" : "",
				stats.getCacheToCacheDistance(),
				distUnit,
				excludeSomething ? (stats.getCacheToCacheDistance() / stats.getCorrectedCacheCount()) : (stats.getCacheToCacheDistance() / stats.getTotalCaches())
				)
			);
		
		strTemp = String.format("<tr><td>%s%s</td><td><img src=\"%s\"/> %s %s</td></tr>\n",
				messages.getString("msg.oldestcache"),
				excludeSomething ? "*" : "",
				Constants.TYPEIMAGES.get(stats.getOldestCache().getType()),
				cacheLink(stats.getOldestCache().getId()),
				escapeXML(stats.getOldestCache().getName())
				);
		ret.append(strTemp);
		
		strTemp = String.format("<tr><td>%s%s</td><td><img src=\"%s\"/> %s %s</td></tr>\n",
				messages.getString("msg.newestcache"),
				excludeSomething ? "*" : "",
				Constants.TYPEIMAGES.get(stats.getNewestCache().getType()),
				cacheLink(stats.getNewestCache().getId()),
				escapeXML(stats.getNewestCache().getName())
				);
		ret.append(strTemp);		

		strTemp = String.format("<tr><td>%s%s</td><td><img src=\"%s\"/> %s %s</td></tr>\n",
				messages.getString("msg.closestcache"),
				excludeSomething ? "*" : "",
				Constants.TYPEIMAGES.get(stats.getClosestCache().getType()),
				cacheLink(stats.getClosestCache().getId()),
				escapeXML(stats.getClosestCache().getName())
				);
		ret.append(strTemp);
		
		strTemp = String.format("<tr><td>%s%s</td><td><img src=\"%s\"/> %s %s</td></tr>\n",
				messages.getString("msg.outmostcache"),
				excludeSomething ? "*" : "",
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
	 * generate a section heading.
	 * @param headingj
	 * @return
	 */
	private String generateHeading(final String heading) {
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
		final StringBuffer ret = new StringBuffer(512);
		final TreeSet<UserNumber> cachesByOwnerSorted = stats.getCachesByOwnerSorted();
		final Integer numberOfOwners = (int) Math.floor(
				(Double.parseDouble(prefs.getProperty("ownernumber", "20")) + 1.0) / 2.0
				);
		
		ret.append(DIV100);
		ret.append(generateHeading(messages.getString("msg.findsbyowner")));

		for (int j = 0; j < 2; j++) {
			ret.append(DIV50);
			ret.append("<table style=\"table-layout:fixed;width:98%\">\n");
			ret.append(String.format("<thead style=\"%s\">\n", html.getString("table.head.default")));
			ret.append("<tr><td width=\"70%\"></td><td>#</td><td>%</td></tr>\n");
			ret.append("</thead>\n");
			ret.append(String.format("<tbody style=\"%s\">\n", html.getString("table.body.default")));
			for (int i = 0; i < numberOfOwners; i++) {
				if (cachesByOwnerSorted.isEmpty()) {
					ret.append("<tr><td>&nbsp;</td><td></td><td></td></tr>\n");
				} else {
					final UserNumber owner = cachesByOwnerSorted.last();
					cachesByOwnerSorted.remove(owner);
					ret.append(MessageFormat.format(
							"<tr><td>{0}</td><td style=\"{3}\">{1,number,#,##0}</td><td style=\"{3}\">{2,number,#0.00}</td></tr>\n",
							ownerLink(owner.getUser()),
							owner.getNumber(),
							owner.getNumber().floatValue() / stats.getTotalCaches().floatValue() * 100.0F,
							html.getString("cell.number")
						)
					);
				}
			}
			ret.append("</tbody>");
			ret.append("</table>\n");
			ret.append("</div>\n");
		}

		final String summary = String.format("<p style=\"%s\">%s</p>\n",
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
	
	/**
	 * generate a compass rose using google chart API displaying cache numbers by direction relative to home coordinates-
	 * @return
	 */
	private String findsByDirection() {
		final HashMap<String, Integer> cbd = stats.getCachesByDirection();
		Integer maxCount = 0;
		final StringBuffer chartData = new StringBuffer("&amp;chd=t:");
		final StringBuffer chartHead = new StringBuffer("&amp;chxl=0:");

		for (String dir : Constants.CARDINALS) {
			if (cbd.get(dir) > maxCount) {
				maxCount = cbd.get(dir);
			}
			chartHead.append(String.format("|%s", messages.getString("orientation." + dir)));
			chartData.append(String.format("%s,", cbd.get(dir)));
		}
		chartData.append(cbd.get("n"));
		
		final StringBuffer ret = new StringBuffer(64);
		final String charturl = Constants.CHARTBASE
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
		ret.append(DIV50);
		ret.append(generateHeading(messages.getString("msg.findsbydirection")));
		ret.append(String.format("<p align=\"center\"><img src=\"%s\" alt=\"\"/></p>", charturl));
		ret.append("</div>\n");
		return ret.toString();
	}
	
	/**
	 * generate statistics for months the cache was found in.
	 * @return
	 */
	private String findsByMonth() {
		final Integer[] fbm = stats.getFindsByMonthFound();
		final StringBuffer ret = new StringBuffer(256);
		Integer maxCount = 0;
		for (int month = 0; month < 12; month++) {
			if (maxCount < fbm[month]) {
				maxCount = fbm[month];
			}
		}
		ret.append(DIV50);
		ret.append(generateHeading(messages.getString("msg.findspermonth")));
		ret.append("<table width=\"98%\" style=\"table-layout:fixed\">\n");
		ret.append(String.format("<thead style=\"%s\">\n", html.getString("table.head.default")));
		ret.append("<tr>" 
				+ "<td style=\"width:35%;\"></td>" 
				+ "<td style=\"width:13%;\">#</td>" 
				+ "<td style=\"width:9%;\">%</td>" 
				+ "<td style=\"\"></td>" 
				+ "</tr>\n");
		ret.append("</thead>\n");
		ret.append(String.format("<tbody style=\"%s\">\n", html.getString("table.body.default")));
		for (int month = 0; month < 12; month++) {
			ret.append(MessageFormat.format("<tr><td>{0}</td><td style=\"{4}\">{1,number,#,##0}</td><td style=\"{4}\">{2,number,#,##0.0}</td><td>{3}</td></tr>\n",
					messages.getString("mon.long." + month),
					fbm[month],
					calcCachePercent(fbm[month]),
					createHorizontalBar(fbm[month], maxCount),
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
	 * generate link to a cache page for milestone statistic.
	 * @param cacheid waypoint
	 * @return url pointing to waypoint info
	 */
	private String cacheLink(final String cacheid) {
		return MessageFormat.format("<a href=\"http://coord.info/{0}\">{0}</a>", cacheid);
	}

	/**
	 * generate link to an owner profile for owner statistics.
	 * @param ownerid
	 * @return
	 */
	private String ownerLink(final String ownerid) {
		String ret = "";
		try {
			ret = String.format("<a href=\"http://www.geocaching.com/profile/?u=%s\">%s</a>", 
					URLEncoder.encode(ownerid, "UTF-8"),
					escapeXML(ownerid));
		} catch (Exception ignore) { 
			LOGGER.debug("Unsupported encoding", ignore);
		}
		return ret;
	}

	/**
	 * generate a horizontal image bar.
	 * @param count
	 * @param maxCount
	 * @return
	 */
	private String createHorizontalBar(final Integer count, final Integer maxCount) {
		final Integer width = (int) Math.floor(count.floatValue() / maxCount.floatValue() * Constants.MAXHORBARLENGTH);
		return MessageFormat.format("<img src=\"{0}\" height=\"15\" width=\"{1}\" alt=\"{2,number,#,##0.0}%\"/>",
				html.getString("bar.horizontal"),
				width.toString(),
				calcCachePercent(count)
			);
	}

	/**
	 * generate forecast for next 00 and 000 milestone based on last years caching averages.
	 * @return
	 */
	private String crystalball() {
		final StringBuffer ret = new StringBuffer();
		final Integer currentFinds = stats.getTotalCaches();
		final Integer next00Milestone = (currentFinds + 100) / 100 * 100;
		final Integer next000Milestone = (currentFinds + 1000) / 1000 * 1000;
		final Integer delta00Finds = next00Milestone - currentFinds;
		final Integer delta000Finds = next000Milestone - currentFinds;
		final Float cpd = stats.getFindsLast365Days().floatValue() / stats.getDaysLastYear().floatValue();
		final Integer days00Finds = Math.round(delta00Finds / cpd);
		final Integer days000Finds = Math.round(delta000Finds / cpd);
		final Calendar date00Finds = Calendar.getInstance();
		final Calendar date000Finds = Calendar.getInstance();
		date00Finds.add(Calendar.DAY_OF_MONTH, days00Finds);
		date000Finds.add(Calendar.DAY_OF_MONTH, days000Finds);
		ret.append("<p>");
		ret.append(MessageFormat.format(messages.getString("msg.crystalball.1"), next00Milestone, date00Finds.getTime()));
		if (!next00Milestone.equals(next000Milestone)) {
			ret.append(MessageFormat.format(messages.getString("msg.crystalball.2"), next000Milestone, date000Finds.getTime()));
		}
		ret.append("</p>\n");
		return ret.toString();
	}
	
	/**
	 * generate raw numbers of finds by country.
	 * @return
	 */
	private String findsByCountry() {
		final TreeMap<String, Integer> fbc =  stats.getFindsByCountry();
		final StringBuffer ret = new StringBuffer(256);
		ret.append(DIV50);
		ret.append(generateHeading(messages.getString("msg.findspercountry") + (excludeSomething ? "<font style=\"size:9px\">*</font>" : "")));
		ret.append("<table width=\"98%\" style=\"table-layout:fixed\">\n");
		ret.append(String.format("<thead style=\"%s\">\n", html.getString("table.head.default")));
		ret.append("<tr><td width=\"70%\"></td><td>#</td><td>%</td></tr>\n");
		ret.append("</thead>\n");
		ret.append(String.format("<tbody style=\"%s\">\n", html.getString("table.body.default")));
		for (String country : fbc.keySet()) {
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
	 * generate a map using google chart api.
	 * @param area valid parameters are world, europe, middle_east, asia, africa, south_america
	 * @return
	 */
	private String googleMap(final String area) {
		final StringBuffer ret = new StringBuffer(128);
		final String headline = messages.getString("msg.areamaps") + messages.getString("area." + area);
		String url = "http://chart.apis.google.com/chart?cht=t&amp;chs=378x189&amp;chf=bg,s,EAF7FE&amp;chtm=".concat(area);
		String countryCodes = "&amp;chld=";
		String countryValues = "&amp;chd=t:";
		final TreeMap<String, Integer> fbc = stats.getFindsByCountry();
		Integer valueCounter = 0;
		
		ret.append(DIV50);
		ret.append(generateHeading(headline + (excludeSomething ? "<font style=\"size:9px\">*</font>" : "")));
		
		url = url.concat("&amp;chco=FFFFFF,DEB887,DEB887");
		
		for (String country : fbc.keySet()) {
			if (Constants.GCCOUNTRY2ISO.get(country) == null) {
				LOGGER.error("unmapped country " + country);
				continue;
			}
			countryCodes = countryCodes.concat(Constants.GCCOUNTRY2ISO.get(country));
			valueCounter++;
		}
		
		for (int i = 0; i < valueCounter - 1; i++) {
			countryValues = countryValues.concat("1,");
		}
		
		if (valueCounter > 0) {
			countryValues = countryValues.concat("1");
		}
		
		ret.append(String.format("<img src=\"%s%s%s\" alt=\"\"/>\n", url, countryCodes, countryValues));
		ret.append("</div>\n");
		
		return ret.toString();
	}
	
	/**
	 * generate time line graph showing total progress and annual caching activities. 
	 * @return
	 */
	private String timeLine() {
		final StringBuffer ret = new StringBuffer(256);
		final HashMap<Integer, Integer[]> mym = stats.getMatrixYearMonthFound();
		
//		String url = "http://chart.apis.google.com/chart?cht=lxy&chxt=r,x,y";
		final String url = "http://chart.apis.google.com/chart?cht=lc";
		final Calendar firstCachingDay = stats.getFirstCachingDay();
		final Calendar today = Calendar.getInstance();
		
		Integer numberOfValues = 0;
		Integer totalCaches = 0;
		Integer bestYear = 0;		
		Integer totalValues[];
		final StringBuffer xLabel = new StringBuffer();
		
		xLabel.append("&chxl=0:|");
		for (Integer year = firstCachingDay.get(Calendar.YEAR); year <= today.get(Calendar.YEAR); year++) {
			xLabel.append(year.toString());
			for (Integer month = 0; month <= 11; month++) {
				if ((year == firstCachingDay.get(Calendar.YEAR)) && (month < firstCachingDay.get(Calendar.MONTH))) {
					continue;
				}
				if ((year == today.get(Calendar.YEAR)) && (month > today.get(Calendar.MONTH))) {
					continue;
				}
				if (month < 11) { xLabel.append('|'); }
				numberOfValues++;
			}
		}
		
		LOGGER.info(numberOfValues);
		
		totalValues = new Integer[numberOfValues];
				
		Integer valueCounter = 0;
		for (Integer year = firstCachingDay.get(Calendar.YEAR); year <= today.get(Calendar.YEAR); year++) {
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
				LOGGER.info("month " + month + " year " + year + " this year " + cachesInYear + " total " + totalCaches);
				totalValues[valueCounter] = totalCaches;
				valueCounter++;
			}
			if (cachesInYear > bestYear) { bestYear = cachesInYear; }
		}
		
		ret.append(DIV100);
		ret.append(generateHeading(messages.getString("msg.timeline")));
		ret.append(String.format("<img src=\"%s\" alt=\"\">", 
				url
				.concat("&chs=740x300") // size
				.concat(String.format("&chxr=1,0,%d", totalCaches)) // max values for y axis 
				.concat("&chf=bg,s,00000000") // background
				.concat(xLabel.toString()) // x axis labels
				.concat("&chd=e:".concat(ChartDataEncoder.scale(totalValues, totalCaches, true)))
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
	 * generate HTML version of statistics according to user preferences.
	 * @return
	 */
	public final String htmlStats() {
		final StringBuffer ret = new StringBuffer(1024);
		Integer outlineCounter = 1;
		
		ret.append(statsHeader());
		
		while (null != prefs.getProperty("output.".concat(outlineCounter.toString()))) {
			final String[] outline = prefs.getProperty("output.".concat(outlineCounter.toString())).split(",");
			for (String outtype : outline) {
				if ("monthyearfound".equals(outtype)) {
					ret.append(matrixYearMonth(stats.getMatrixYearMonthFound(), messages.getString("msg.fpm")));
				} else if ("milestones".equals(outtype)) {
					ret.append(milestones());
				} else if ("terraindifficulty".equals(outtype)) {
					ret.append(matrixTerrainDifficulty());
				} else if ("cachetype".equals(outtype)) {
					ret.append(findsByType());
				} else if ("distancefromhome".equals(outtype)) {
					ret.append(findsByDistanceFromHome());
				} else if ("daymonthfound".equals(outtype)) {
					ret.append(matrixMonthDay());
				} else if ("difficulty".equals(outtype)) {
					ret.append(findsByDT(stats.getCachesByDifficulty(), messages.getString("msg.fbd")));
				} else if ("terrain".equals(outtype)) {
					ret.append(findsByDT(stats.getCachesByTerrain(), messages.getString("msg.fbt")));
				} else if ("dayofweek".equals(outtype)) {
					ret.append(findsByDayOfWeek());
				} else if ("size".equals(outtype)) {
					ret.append(findsByContainer());
				} else if ("monthyearplaced".equals(outtype)) {
					ret.append(matrixYearMonth(stats.getMatrixYearMonthPlaced(), messages.getString("msg.fpmplaced")));
				} else if ("compassrose".equals(outtype)) {
					ret.append(findsByDirection());
				} else if ("findsbymonthfound".equals(outtype)) {
					ret.append(findsByMonth());
				} else if ("datatomb".equals(outtype)) {
					ret.append(datatomb());
				} else if ("owner".equals(outtype)) {
					ret.append(findsByOwner());
				} else if ("mapworld".equals(outtype)) {
					ret.append(googleMap("world"));
				} else if ("mapeurope".equals(outtype)) {
					ret.append(googleMap("europe"));
				} else if ("mapmiddleeast".equals(outtype)) {
					ret.append(googleMap("middle_east"));
				} else if ("mapasia".equals(outtype)) {
					ret.append(googleMap("asia"));
				} else if ("mapafrica".equals(outtype)) {
					ret.append(googleMap("africa"));
				} else if ("mapsouthamerica".equals(outtype)) {
					ret.append(googleMap("south_america"));
				} else {
					LOGGER.warn("unknown output directive " + outtype + ". check preferences.properties");
				}
			}
			ret.append(DIVDELIM);
			outlineCounter++;
		}
		
		ret.append(statsFooter());
		
		return ret.toString();
	}
	
	/**
	 * generate header section of statistics and set basic size parameters.
	 * @return
	 */
	private String statsHeader() {
		final StringBuffer ret = new StringBuffer(128);
		final String temp = String.format("<div align=\"center\" style=\"%s\">\n", html.getString("outerdiv"));
		ret.append(String.format(temp, html.getString("totalwidth")));
		ret.append("<span style='");
		ret.append("font-family: Tahoma, Arial, sans-serif; font-size: 16px; font-weight: bold;");
		ret.append("'>");
		ret.append(MessageFormat.format(messages.getString("head.summary"), username, stats.getTotalCaches()));
		ret.append("</span><br />\n");
		ret.append("<br />");
		ret.append(MessageFormat.format(messages.getString("head.update"), new Date(System.currentTimeMillis())));
		ret.append("<br /><br />\n");
		return ret.toString();
	}
	
	/**
	 * generate finally section of statistics and close all open tags.
	 * @return
	 */
	private String statsFooter() {
		final StringBuffer ret = new StringBuffer(128);
		String temp;
		ret.append(DIV100);
		ret.append(generateHeading(messages.getString("msg.finally")));
		temp = String.format(
					"<p style=\"%s\">%s</p>\n", 
					html.getString("p.finally"),
					messages.getString("msg.footer.1")
				);
		ret.append(MessageFormat.format(temp, "<a href=\"http://statwolf.berlios.de/\">StatWolf</a>", Version.SWVERSION));
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
			final StringBuffer buf = new StringBuffer(32);
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
	 * generate html header for stand alone display and set start marker.
	 * @return
	 */
	private String htmlHeader() {
		final StringBuffer ret = new StringBuffer(768);
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
	 * set end marker for stats and close HTML tags.
	 * @return
	 */
	private String htmlFooter() {
		final StringBuffer ret = new StringBuffer(128);
		ret.append("<!--  ******************** select above to include in gc.com profile ********************  -->\n");
		ret.append("</body>\n</html>\n");
		return ret.toString();
	}
	
	/**
	 * encode enough of the string to make it go through the XML validator.
	 * @param str string to encode
	 * @return string with &, >, < replaced by their entity encoding
	 */
	private String escapeXML(final String str) {
		return str.replace(">", "&gt;").replace("<", "&lt;").replace("&", "&amp;");
	}
}

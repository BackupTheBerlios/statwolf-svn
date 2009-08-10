package de.berlios.statwolf;

import java.io.FileInputStream;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class StatWolf {
	
	/** user preferences. */
	public static Properties prefs;
	
	/** main method of StatWolf. */
	public static void main(final String[] args) {
		String indexdir;

		// is there an other way to get log4j.properties from the jar file?
		ResourceBundle rb = ResourceBundle.getBundle("log4j");
		Properties log4jProps = new Properties();
		for (Enumeration<String> keys = rb.getKeys(); keys.hasMoreElements();) {
			final String key = (String) keys.nextElement();
            final String value = rb.getString(key);
            
            log4jProps.put(key, value);
		}
		PropertyConfigurator.configure(log4jProps);

		Logger logger = Logger.getLogger(StatWolf.class);
		
		prefs = new Properties();
		String preffile = System.getProperty("preferences");
		
		if (preffile == null) {
			preffile = "preferences.properties";
		}

		try {
			prefs.load(new FileInputStream(preffile));
		} catch (Exception ex) {
			logger.fatal("unable to load preferences ".concat(ex.getMessage()));
			logger.debug("reason: ", ex);
			System.exit(1);
		}
		
		String locale = prefs.getProperty("locale", "en");
		Locale.setDefault(new Locale(locale));
		
		String loglevel = prefs.getProperty("loglevel", "info");
		if (loglevel.toLowerCase().equals("debug")) {
			log4jProps.put("log4j.logger.de.berlios.statwolf", "DEBUG");
			log4jProps.put("log4j.rootLogger", "DEBUG,A2");
			PropertyConfigurator.configure(log4jProps);
		}
		
		ResourceBundle messages = ResourceBundle.getBundle("messages");
		
		if (args.length > 0) {
			indexdir = args[0];
			logger.debug("command line indexdir: ".concat(indexdir));
		} else {
			indexdir = prefs.getProperty("indexdir");
			if (indexdir == null) {
				logger.error("indexdir not set. please check preferences");
				System.exit(1);
			}
		}
		
		if (!(indexdir.endsWith(System.getProperty("file.separator")))) {
			indexdir = indexdir.concat(System.getProperty("file.separator"));
		}
		
		// catch everything we may have forgotten
		IndexParser indexParser = null;
		StatisticsData statisticsData = null;
		HTMLOutput htmlOutput = null;
		String statfile = null;
		
		try {
			indexParser = new IndexParser(indexdir);
		} catch (Exception ex) {
			logger.error("unexpected parsing error");
			logger.debug(ex, ex);
			System.exit(1);
		}	
		try {
			statisticsData = new StatisticsData(indexParser.getFoundCaches(), indexParser.getHomeCoordinates());
		} catch (Exception ex) {
			logger.error("unexpected statistics error");
			logger.debug(ex, ex);
			System.exit(1);
		}	

		try {
			htmlOutput = new  HTMLOutput(statisticsData);
			statfile = htmlOutput.generateHTML();
		} catch (Exception ex) {
			logger.error("unexpected html generation error");
			logger.debug(ex, ex);
			System.exit(1);
		}	
		if (statfile != null) {
			logger.info(MessageFormat.format(messages.getString("log.finished"), statfile));
		} else {
			logger.fatal("unabel to save output");
		}
	}
	
	/** get preferences object. */
	public final Properties getPrefs() { return prefs; };
}

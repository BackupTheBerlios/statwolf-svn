package de.berlios.statwolf;

// OK

import java.io.FileInputStream;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

//import de.berlios.statwolf.gui.MainForm;

/** main class for StatWolf. Configuration files will be read and */
public final class StatWolf {
	
	/** user preferences. */
	private final transient Properties prefs;
	/** log4j logger. not declared static since this class will create it */
	private final transient Logger logger; // NOPMD by greis on 17.08.09 21:49
	/** localized messages. */
	private final transient ResourceBundle messages;
	/** directory for index.xml file. */
	private transient String indexdir;
	
	/**
	 * thou shallst not instantiate this object.
	 * 
	 * @param prefProperty
	 *            filename of preferences if specified on command line. may be
	 *            null
	 * @param profiledir
	 *            directory for CacheWolf profile if specified on command line.
	 *            may be null
	 */
	private StatWolf(final String prefProperty, final String profiledir) {

		// is there an other way to get log4j.properties from the jar file?
		final ResourceBundle log4jBundle = ResourceBundle.getBundle("log4j");
		final Properties log4jProps = new Properties();
		for (final Enumeration<String> keys = log4jBundle.getKeys(); keys.hasMoreElements();) {
			final String key = (String) keys.nextElement();
            final String value = log4jBundle.getString(key);
            
            log4jProps.put(key, value);
		}
		PropertyConfigurator.configure(log4jProps);

		logger = Logger.getLogger(StatWolf.class);
		
		prefs = new Properties();
		String preffile = prefProperty;
		
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
		
		final String locale = prefs.getProperty("locale", "en");
		Locale.setDefault(new Locale(locale));
		
		final String loglevel = prefs.getProperty("loglevel", "info");
		if (loglevel.equalsIgnoreCase("debug")) {
			log4jProps.put("log4j.logger.de.berlios.statwolf", "DEBUG");
			log4jProps.put("log4j.rootLogger", "DEBUG,A2");
			PropertyConfigurator.configure(log4jProps);
		}
		
		messages = ResourceBundle.getBundle("messages");
		
		if (profiledir == null) {
			indexdir = prefs.getProperty("indexdir");
			if (indexdir == null) {
				logger.error("indexdir not set. please check preferences");
				System.exit(1);
			}
		} else {
			indexdir = profiledir;
		}
		
		if (!(indexdir.endsWith(System.getProperty("file.separator")))) {
			indexdir = indexdir.concat(System.getProperty("file.separator"));
		}
	}
	
	/**
	 * main method of StatWolf.
	 * 
	 * @param args
	 *            command line arguments
	 */
	public static void main(final String[] args) {
		if ("true".equals(System.getProperty("gui", "false"))) {
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					createAndShowGUI();
				}
			});			
		} else {
			final String preffile = System.getProperty("preferences");
			StatWolf statwolf;
			if (args.length > 0) {
				statwolf = new StatWolf(preffile, args[0]);
			} else {
				statwolf = new StatWolf(preffile, null);
			}
			statwolf.generateStatistics();
		}
	}
	
	private static void createAndShowGUI() {
//		final MainForm frame = new MainForm();
//		frame.setVisible(true);
	}
	
	/** @return preferences object. */
	public Properties getPrefs() { return prefs; };
	
	/**
	 * do not allow cloning of this object.
	 * 
	 * @throws CloneNotSupportedException
	 *             at every call
	 * @return will not return anything, but throw a
	 *         <code>CloneNotSupportedException</code>
	 */
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
	
	/**
	 * generate the actual statistics. will be done by parsing the index, 
	 * calculating statistics data, generating the HTML output and writing 
	 * it to a file
	 */
	private void generateStatistics() {
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
			statisticsData = new StatisticsData(
					indexParser.getFoundCaches(), 
					indexParser.getHomeCoordinates(), 
					prefs);
		} catch (Exception ex) {
			logger.error("unexpected statistics error");
			logger.debug(ex, ex);
			System.exit(1);
		}	

		try {
			htmlOutput = new  HTMLOutput(statisticsData, prefs);
			statfile = htmlOutput.generateHTML();
		} catch (Exception ex) {
			logger.error("unexpected html generation error");
			logger.debug(ex, ex);
			System.exit(1);
		}

		if (statfile == null) {
			logger.fatal("unabel to save output");
		} else {
			logger.info(MessageFormat.format(
					messages.getString("log.finished"), statfile));			
		}
	}
}

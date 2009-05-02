package de.berlios.statwolf;

import org.apache.log4j.*;
import java.util.*;
import java.io.*;

public class StatWolf {
	
	public static void main(String[] args) {
		
		PropertyConfigurator.configure("log4j.properties");
		Logger logger = Logger.getLogger(StatWolf.class);
		
		Properties prefs = new Properties();
		String preffile = System.getProperty("preferences");

		if ( preffile == null ) {
			preffile = "preferences.properties";
		}

		try {
			prefs.load(new FileInputStream(preffile));
		} catch (Exception ex) {
			logger.fatal(ex.getLocalizedMessage());
			logger.debug(ex.getStackTrace());
			System.exit(1);
		}

		IndexParser indexParser = new IndexParser(prefs);

		StatisticsData statisticsData = new StatisticsData(indexParser.getFoundCaches(), indexParser.getHomeCoordinates(), prefs);

		HTMLOutput htmlOutput = new  HTMLOutput(statisticsData, prefs);

		String statfile = htmlOutput.generateHTML();

		if (statfile != null ) {
			logger.info("Finished. Statistics are in ".concat(statfile));
		} else {
			logger.fatal("Sorry, some error occured. Please check the logs!");
		}
	}
}

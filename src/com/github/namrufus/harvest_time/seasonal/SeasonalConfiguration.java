package com.github.namrufus.harvest_time.seasonal;

import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;

public class SeasonalConfiguration {
	
	private int daysInSeasonalYear;
	
	public SeasonalConfiguration(ConfigurationSection config, Logger LOG) {
		daysInSeasonalYear = config.getInt("days_in_seasonal_year");
	}
	
	// ================================================================================================================
	
	public int getDaysInSeasonalYear() { return daysInSeasonalYear; }
	
	// ----------------------------------------------------------------------------------------------------------------
	
	public void dump(Logger LOG) {
		LOG.info("SeasonalConfiguration:");
		LOG.info("  daysInSeasonalYear: " + daysInSeasonalYear);
	}
}

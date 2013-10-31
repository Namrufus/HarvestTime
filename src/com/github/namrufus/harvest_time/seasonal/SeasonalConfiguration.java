package com.github.namrufus.harvest_time.seasonal;

import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;

public class SeasonalConfiguration {
	public enum RainfallType {
		MILD, DROUGHT, MONSOON;
	}
	
	private int daysInSeasonalYear;
	
	boolean rainfallControlEnabled;
	double mildFrequency, mildRainfallChance;
	double droughtFrequency, droughtRainfallChance;
	double monsoonFrequency, monsoonRainfallChance;
	
	public SeasonalConfiguration(ConfigurationSection config, Logger LOG) {
		// seasonal_growth config
		daysInSeasonalYear = config.getInt("days_in_seasonal_year");
		
		// seasonal rainfall control config
		ConfigurationSection rainfallConfig = config.getConfigurationSection("rainfall_control");
		rainfallControlEnabled = rainfallConfig.getBoolean("enabled");
		
		mildFrequency = rainfallConfig.getDouble("MILD.frequency");
		mildRainfallChance = rainfallConfig.getDouble("MILD.rainfall_chance");
		
		droughtFrequency = rainfallConfig.getDouble("DROUGHT.frequency");
		droughtRainfallChance = rainfallConfig.getDouble("DROUGHT.rainfall_chance");
		
		monsoonFrequency = rainfallConfig.getDouble("MONSOON.frequency");
		monsoonRainfallChance = rainfallConfig.getDouble("MONSOON.rainfall_chance");
	}
	
	// ================================================================================================================
	public int getDaysInSeasonalYear() { return daysInSeasonalYear; }
	
	public RainfallType sampleNewRainfallType() {
		double r = Math.random();
		
		// normalize
		r *= mildFrequency + droughtFrequency + monsoonFrequency;
		
		if (r < mildFrequency)
			return RainfallType.MILD;
		else if (r < droughtFrequency)
			return RainfallType.DROUGHT;
		else /* r < monsoonFrequency */
			return RainfallType.MONSOON;
	}
	
	public boolean sampleIsRaining(RainfallType rainfallType) {
		double r = Math.random();
		
		if (rainfallType == RainfallType.MILD)
			return r < mildRainfallChance;
		else if (rainfallType == RainfallType.DROUGHT)
			return r < droughtRainfallChance;
		else /* monsoon */
			return r < monsoonRainfallChance;
	}
	
	// ----------------------------------------------------------------------------------------------------------------
	
	public void dump(Logger LOG) {
		LOG.info("SeasonalConfiguration:");
		LOG.info("  daysInSeasonalYear: " + daysInSeasonalYear);
		
		LOG.info("  rainfallControlEnabled: " + rainfallControlEnabled);
		
		LOG.info("  mildFrequency: " + mildFrequency);
		LOG.info("  mildRainfallChance: " + mildRainfallChance);
		LOG.info("  droughtFrequency: " + droughtFrequency);
		LOG.info("  droughtRainfallChance: " + droughtRainfallChance);
		LOG.info("  monsoonFrequency: " + monsoonFrequency);
		LOG.info("  monsoonRainfallChance: " + monsoonRainfallChance);
	}
}

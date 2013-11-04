package com.github.namrufus.harvest_time.seasonal;

import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;

public class SeasonalConfiguration {
	public enum RainfallType {
		MILD, DROUGHT, MONSOON;
	}
	
	private int daysInSeasonalYear;
	
	private boolean rainfallControlEnabled;
	private String seed;
	private double mildFrequency, mildRainfallChance;
	private double droughtFrequency, droughtRainfallChance;
	private double monsoonFrequency, monsoonRainfallChance;
	
	public SeasonalConfiguration(ConfigurationSection config, Logger LOG) {
		// seasonal_growth config
		daysInSeasonalYear = config.getInt("days_in_seasonal_year");
		
		// seasonal rainfall control config
		ConfigurationSection rainfallConfig = config.getConfigurationSection("rainfall_control");
		rainfallControlEnabled = rainfallConfig.getBoolean("enabled");
		
		seed = rainfallConfig.getString("seed");
		
		mildFrequency = rainfallConfig.getDouble("MILD.frequency");
		mildRainfallChance = rainfallConfig.getDouble("MILD.rainfall_chance");
		
		droughtFrequency = rainfallConfig.getDouble("DROUGHT.frequency");
		droughtRainfallChance = rainfallConfig.getDouble("DROUGHT.rainfall_chance");
		
		monsoonFrequency = rainfallConfig.getDouble("MONSOON.frequency");
		monsoonRainfallChance = rainfallConfig.getDouble("MONSOON.rainfall_chance");
	}
	
	// ================================================================================================================
	public int getDaysInSeasonalYear() { return daysInSeasonalYear; }
	
	public boolean isRainfallControlEnabled() { return rainfallControlEnabled; }
	
	public String getRainfallSeed() { return seed; }
	
	// given a unformly random number from the range [0, 1] return a distribution
	// of rainfall states 
	public RainfallType sampleNewRainfallType(double r) {	
		// normalize
		r *= mildFrequency + droughtFrequency + monsoonFrequency;
		
		if (r < mildFrequency)
			return RainfallType.MILD;
		else if (r < droughtFrequency + mildFrequency)
			return RainfallType.DROUGHT;
		else /* r < monsoonFrequency */
			return RainfallType.MONSOON;
	}
	
	public boolean sampleIsRaining(RainfallType rainfallType, double r) {	
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
		
		LOG.info("  seed: " + seed);
		
		LOG.info("  mildFrequency: " + mildFrequency);
		LOG.info("  mildRainfallChance: " + mildRainfallChance);
		LOG.info("  droughtFrequency: " + droughtFrequency);
		LOG.info("  droughtRainfallChance: " + droughtRainfallChance);
		LOG.info("  monsoonFrequency: " + monsoonFrequency);
		LOG.info("  monsoonRainfallChance: " + monsoonRainfallChance);
	}
}

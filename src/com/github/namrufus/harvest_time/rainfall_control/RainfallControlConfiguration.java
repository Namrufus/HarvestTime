package com.github.namrufus.harvest_time.rainfall_control;

import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;

// this class is responsible for loading in configuration for the rainfall control module

public class RainfallControlConfiguration {
	private boolean enabled;
	
	private String seed;
	private double mildFrequency, mildRainfallChance;
	private double droughtFrequency, droughtRainfallChance;
	private double monsoonFrequency, monsoonRainfallChance;
	
	public RainfallControlConfiguration(ConfigurationSection config, Logger LOG) {
		enabled = config.getBoolean("enabled");
		
		seed = config.getString("seed");
		
		mildFrequency = config.getDouble("MILD.frequency");
		mildRainfallChance = config.getDouble("MILD.rainfall_chance");
		
		droughtFrequency = config.getDouble("DROUGHT.frequency");
		droughtRainfallChance = config.getDouble("DROUGHT.rainfall_chance");
		
		monsoonFrequency = config.getDouble("MONSOON.frequency");
		monsoonRainfallChance = config.getDouble("MONSOON.rainfall_chance");
	}
	
	// ================================================================================================================
	
	public boolean isEnabled() { return enabled; }

	public String getSeed() { return seed; }
	
	// given a uniformly random number from the range [0, 1] return a distribution
	// of rainfall states 
	public RainfallClimateState sampleRainfallClimateState(double r) {	
		// normalize
		r *= mildFrequency + droughtFrequency + monsoonFrequency;
		
		if (r < mildFrequency)
			return RainfallClimateState.MILD;
		else if (r < droughtFrequency + mildFrequency)
			return RainfallClimateState.DROUGHT;
		else /* r < monsoonFrequency */
			return RainfallClimateState.MONSOON;
	}
	
	public boolean sampleIsRaining(RainfallClimateState rainfallType, double r) {	
		if (rainfallType == RainfallClimateState.MILD)
			return r < mildRainfallChance;
		else if (rainfallType == RainfallClimateState.DROUGHT)
			return r < droughtRainfallChance;
		else /* monsoon */
			return r < monsoonRainfallChance;
	}
	
	// ----------------------------------------------------------------------------------------------------------------
	
	public void dump(Logger log) {
		log.info("RainfallControlConfiguration:");
		
		log.info("  enabled: " + enabled);
		
		log.info("  seed: " + seed);
		
		log.info("  mildFrequency: " + mildFrequency);
		log.info("  mildRainfallChance: " +  mildRainfallChance);
		
		log.info("  droughtFrequency: " + droughtFrequency);
		log.info("  droughtRainfallChance: " + droughtRainfallChance);
		
		log.info("  monsoonFrequency: " + monsoonFrequency);
		log.info("  monsoonRainfallChance: " + monsoonRainfallChance);
	}
}

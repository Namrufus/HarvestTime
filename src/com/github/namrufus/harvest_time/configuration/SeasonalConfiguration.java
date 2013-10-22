package com.github.namrufus.harvest_time.configuration;

import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public class SeasonalConfiguration {
	public enum RainfallType {
		MILD, DROUGHT, MONSOON;
	}
	
	private int daysInSeasonalYear;
	private Material cropGrowthCheckMaterial;
	
	boolean rainfallControlEnabled;
	double mildFrequency, mildRainfallChance;
	double droughtFrequency, droughtRainfallChance;
	double monsoonFrequency, monsoonRainfallChance;
	
	public SeasonalConfiguration(ConfigurationSection config, Logger LOG) {
		// seasonal_growth config
		daysInSeasonalYear = config.getInt("days_in_seasonal_year");
		
		String cropGrowthCheckMaterialName = config.getString("crop_growth_check_material");
		try {
			cropGrowthCheckMaterial = Material.valueOf(cropGrowthCheckMaterialName);
		} catch (IllegalArgumentException e) {
			LOG.warning("Seasonal Growth Configuration: unkown material: "+cropGrowthCheckMaterialName+" using WATCH");
			cropGrowthCheckMaterial = Material.WATCH;
		}
		
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
	public Material getCrogGrowthCheckMaterial() { return cropGrowthCheckMaterial; }
	
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
		LOG.info("  cropGrowthCheckMaterial: " + cropGrowthCheckMaterial);
		
		LOG.info("  rainfallControlEnabled: " + rainfallControlEnabled);
		
		LOG.info("  mildFrequency: " + mildFrequency);
		LOG.info("  mildRainfallChance: " + mildRainfallChance);
		LOG.info("  droughtFrequency: " + droughtFrequency);
		LOG.info("  droughtRainfallChance: " + droughtRainfallChance);
		LOG.info("  monsoonFrequency: " + monsoonFrequency);
		LOG.info("  monsoonRainfallChance: " + monsoonRainfallChance);
	}
}

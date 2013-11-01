package com.github.namrufus.harvest_time.crop_growth.environment;

import java.util.logging.Logger;

import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;


public class CropFreshWaterConfiguration {
	// multiplier if this crop is exposed to fresh water
	private FreshWaterConfiguration freshWaterConfiguration;
	private double irrigationMultiplier;
	private double rainfallMultiplier;
	
	public CropFreshWaterConfiguration(ConfigurationSection config, FreshWaterConfiguration freshWaterConfiguration) {
		this.freshWaterConfiguration = freshWaterConfiguration;
		irrigationMultiplier = config.getDouble("irrigation_multiplier");
		rainfallMultiplier = config.getDouble("rainfall_multiplier");
	}
	
	// ============================================================================================
	public double getRainfallMultiplier(Block block) {
		if (freshWaterConfiguration.hasRainfallFreshWater(block))
			return rainfallMultiplier;
		else
			return 1.0;
	}
	
	public double getIrrigationMultiplier(Block block) {
		if (freshWaterConfiguration.hasBiomeFreshWater(block))
			return irrigationMultiplier;
		else
			return 1.0;
	}
	
	// --------------------------------------------------------------------------------------------
	public void dump(Logger log) {
		log.info("  CropFreshWaterConfiguration:");
		log.info("    irrigationMultiplier: " + irrigationMultiplier);
		log.info("    rainfallMultiplier: " + rainfallMultiplier);
	}
}

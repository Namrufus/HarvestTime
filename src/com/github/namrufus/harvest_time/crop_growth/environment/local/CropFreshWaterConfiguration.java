package com.github.namrufus.harvest_time.crop_growth.environment.local;

import java.util.logging.Logger;

import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;

import com.github.namrufus.harvest_time.crop_growth.environment.global.FreshWaterConfiguration;



public class CropFreshWaterConfiguration {
	// multiplier if this crop is exposed to fresh water
	private FreshWaterConfiguration freshWaterConfiguration;
	private double irrigationMultiplier;
	
	public CropFreshWaterConfiguration(ConfigurationSection config, FreshWaterConfiguration freshWaterConfiguration) {
		this.freshWaterConfiguration = freshWaterConfiguration;
		irrigationMultiplier = config.getDouble("irrigation_multiplier");
	}
	
	// ============================================================================================
	
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
	}
}

package com.github.namrufus.harvest_time.crop_growth.environment;

import java.util.logging.Logger;

import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;

public class CropSunlightConfiguration {
	private int minSunlightLevel;
	private double multiplier;
	
	public CropSunlightConfiguration(ConfigurationSection config) {
		minSunlightLevel = config.getInt("min_sunlight_level");
		multiplier = config.getInt("multiplier");
	}
	
	// ================================================================================================================
	public double getMultiplier(Block block) {
		// use the block directly above the crop block (some crop blocks (cactus) are solid and block light themselves)
		Block topBlock = block.getRelative(0, 1, 0);
		if (topBlock == null)
			return multiplier;
		
		if (topBlock.getLightFromSky() >= minSunlightLevel)
			return multiplier;
		else
			return 1.0;
	}
	
	public void dump(Logger log) {
		log.info("  CropSunlightConfiguration:");
		log.info("    minSunlightLevel: " + minSunlightLevel);
		log.info("    multiplier: " + multiplier);
	}
}

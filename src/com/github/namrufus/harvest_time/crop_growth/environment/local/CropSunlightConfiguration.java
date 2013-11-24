package com.github.namrufus.harvest_time.crop_growth.environment.local;

import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;

public class CropSunlightConfiguration {
	private static final int MAX_LIGHT_LEVEL = 15;
	
	private int minSunlightLevel;
	private boolean glowstoneLightingEnabled;
	private double multiplier;
	
	public CropSunlightConfiguration(ConfigurationSection config) {
		minSunlightLevel = config.getInt("min_sunlight_level");
		glowstoneLightingEnabled = config.getBoolean("glowstone_lighting_enabled");
		multiplier = config.getInt("multiplier");
	}
	
	// ================================================================================================================
	public double getMultiplier(Block block) {
		if (sunlightTest(block) || (glowstoneLightingEnabled && glowstoneTest(block)))
			return multiplier;
		else
			return 1.0;
	}
	
	private boolean sunlightTest(Block block) {
		int adjustedSunlightLevel;
		// use the block directly above the crop block (some crop blocks (cactus) are solid and block light themselves)
		Block topBlock = block.getRelative(0, 1, 0);
		
		// if the block above the given block is exposed to the sky, then the level is the highest level
		if (topBlock == null)
			adjustedSunlightLevel = MAX_LIGHT_LEVEL;
		else
			adjustedSunlightLevel = Math.max(block.getLightFromSky(), topBlock.getLightFromSky());
		
		return adjustedSunlightLevel >= minSunlightLevel;
	}
	
	// return true iff the block directly above the given block is a glowstone block
	private boolean glowstoneTest(Block block) {
		Block topBlock = block.getRelative(0, 1, 0);
		
		if (topBlock == null)
			return false;
		
		return topBlock.getType() == Material.GLOWSTONE;
	}
	
	public void dump(Logger log) {
		log.info("  CropSunlightConfiguration:");
		log.info("    minSunlightLevel: " + minSunlightLevel);
		log.info("    glowstoneLightingEnabled: " + glowstoneLightingEnabled);
		log.info("    multiplier: " + multiplier);
	}
}

package com.github.namrufus.harvest_time.configuration.crop.environment;

import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;

import com.github.namrufus.harvest_time.configuration.FreshWaterConfiguration;

public class CropFreshWaterConfiguration {
	// multiplier if this crop is exposed to fresh water
	private FreshWaterConfiguration freshWaterConfiguration;
	private double multiplier;
	
	public CropFreshWaterConfiguration(ConfigurationSection config, FreshWaterConfiguration freshWaterConfiguration) {
		this.freshWaterConfiguration = freshWaterConfiguration;
		multiplier = config.getDouble("multiplier");
	}
	
	// ============================================================================================
	public double getMultiplier(Block block) {
		// if the block is exposed to fresh water, then use the multiplier
		// if not, simply return 100%
		if (freshWaterConfiguration.hasFreshWater(block))
			return multiplier;
		else
			return 1.0;
	}
}

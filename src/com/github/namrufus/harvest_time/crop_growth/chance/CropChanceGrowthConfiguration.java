package com.github.namrufus.harvest_time.crop_growth.chance;

import java.util.logging.Logger;

import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;

import com.github.namrufus.harvest_time.crop_growth.environment.CropEnvironmentConfiguration;
import com.github.namrufus.harvest_time.crop_growth.environment.FreshWaterConfiguration;
import com.github.namrufus.harvest_time.regional.RegionalConfiguration;
import com.github.namrufus.harvest_time.regional.RegionalGenerator;
import com.github.namrufus.harvest_time.util.configuration.BiomeAliasesConfiguration;

public class CropChanceGrowthConfiguration {
	// the base chance that this crop will succeed on a grow, breed, spawn, etc event
	private double baseChance;
	// environment modifiers to the base chance
	private CropEnvironmentConfiguration cropEnvironmentConfiguration;
	
	public CropChanceGrowthConfiguration(ConfigurationSection config, FreshWaterConfiguration freshWaterConfiguration, RegionalConfiguration regionalConfiguration, BiomeAliasesConfiguration biomeAliases, Logger log) {
		baseChance = config.getDouble("base_chance");
		
		cropEnvironmentConfiguration = new CropEnvironmentConfiguration(config, freshWaterConfiguration, regionalConfiguration, biomeAliases, log);
	}
	
	// ================================================================================================================
	public boolean growthSucceeds(Block block, RegionalGenerator regionalGenerator) {
		double chance = baseChance;
		chance *= cropEnvironmentConfiguration.getMultiplier(block, regionalGenerator);
		
		return Math.random() < chance;
	}
	
	// ----------------------------------------------------------------------------------------------------------------
	public void dump(Logger log) {
		log.info("CropChanceGrowthConfiguration:");
		log.info("  baseChance: " + baseChance);
		cropEnvironmentConfiguration.dump(log);
	}
}

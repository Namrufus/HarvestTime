package com.github.namrufus.harvest_time.crop_growth.seasonal_growth;

import java.util.logging.Logger;

import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.github.namrufus.harvest_time.crop_growth.environment.CropEnvironmentConfiguration;
import com.github.namrufus.harvest_time.crop_growth.environment.global.BiomeAliasesConfiguration;
import com.github.namrufus.harvest_time.crop_growth.environment.global.FreshWaterConfiguration;

public class CustomYieldConfiguration {
	// base yield count and environment modifiers
	private double baseYield;
	private CropEnvironmentConfiguration environmentConfiguration;
	
	public CustomYieldConfiguration(ConfigurationSection config, FreshWaterConfiguration baseFreshWaterConfiguration, BiomeAliasesConfiguration biomeAliases, Logger log) {
		baseYield = config.getDouble("base_yield");
		
		environmentConfiguration = new CropEnvironmentConfiguration(config, baseFreshWaterConfiguration, biomeAliases, log);
	}
	
	// ==============================================================================
	public double getYieldCount(Block block) {
		return baseYield * environmentConfiguration.getMultiplier(block);
	}
	
	// ------------------------------------------------------------------------------
	public void dump(Logger log) {
		log.info("CustomYieldConfiguration:");
		log.info("  baseYield: "+baseYield);
		environmentConfiguration.dump(log);
	}
	
	public void displayState(Player player, Block block) {
		player.sendMessage("§7"/*light grey*/ + "[Harvest Time] Base Yield: " + "§8"/*dark grey*/ + String.format("%.2f", baseYield));
		environmentConfiguration.displayState(player, block);
	}
}

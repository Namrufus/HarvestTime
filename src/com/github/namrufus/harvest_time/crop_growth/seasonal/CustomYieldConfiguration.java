package com.github.namrufus.harvest_time.crop_growth.seasonal;

import java.util.logging.Logger;

import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.github.namrufus.harvest_time.crop_growth.environment.CropEnvironmentConfiguration;
import com.github.namrufus.harvest_time.crop_growth.environment.FreshWaterConfiguration;
import com.github.namrufus.harvest_time.regional.RegionalConfiguration;
import com.github.namrufus.harvest_time.regional.RegionalGenerator;
import com.github.namrufus.harvest_time.util.configuration.BiomeAliasesConfiguration;

public class CustomYieldConfiguration {
	// base yield count and environment modifiers
	private double baseYield;
	private CropEnvironmentConfiguration environmentConfiguration;
	
	public CustomYieldConfiguration(ConfigurationSection config, FreshWaterConfiguration baseFreshWaterConfiguration, RegionalConfiguration baseRegionalConfiguration, BiomeAliasesConfiguration biomeAliases, Logger log) {
		baseYield = config.getDouble("base_yield");
		
		environmentConfiguration = new CropEnvironmentConfiguration(config, baseFreshWaterConfiguration, baseRegionalConfiguration, biomeAliases, log);
	}
	
	// ==============================================================================
	public double getYieldCount(Block block, RegionalGenerator regionalGenerator) {
		return baseYield * environmentConfiguration.getMultiplier(block, regionalGenerator);
	}
	
	// ------------------------------------------------------------------------------
	public void dump(Logger log) {
		log.info("CustomYieldConfiguration:");
		log.info("  baseYield: "+baseYield);
		environmentConfiguration.dump(log);
	}
	
	public void displayState(Player player, Block block, RegionalGenerator regionalGenerator) {
		player.sendMessage("�7"/*light grey*/ + "[Harvest Time] Base Yield: " + "�8"/*dark grey*/ + String.format("%.2f", baseYield));
		environmentConfiguration.displayState(player, block, regionalGenerator);
	}
}
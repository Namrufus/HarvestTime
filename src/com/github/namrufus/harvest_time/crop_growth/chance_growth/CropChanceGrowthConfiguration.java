package com.github.namrufus.harvest_time.crop_growth.chance_growth;

import java.util.logging.Logger;

import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.github.namrufus.harvest_time.crop_growth.environment.CropEnvironmentConfiguration;
import com.github.namrufus.harvest_time.crop_growth.environment.global.BiomeAliasesConfiguration;
import com.github.namrufus.harvest_time.crop_growth.environment.global.FreshWaterConfiguration;
import com.github.namrufus.harvest_time.crop_growth.environment.global.region.RegionConfiguration;
import com.github.namrufus.harvest_time.crop_growth.environment.global.region.RegionGenerator;

public class CropChanceGrowthConfiguration {
	// the base chance that this crop will succeed on a grow, breed, spawn, etc event
	private double baseChance;
	// environment modifiers to the base chance
	private CropEnvironmentConfiguration cropEnvironmentConfiguration;
	
	public CropChanceGrowthConfiguration(ConfigurationSection config, FreshWaterConfiguration freshWaterConfiguration, RegionConfiguration regionalConfiguration, BiomeAliasesConfiguration biomeAliases, Logger log) {
		baseChance = config.getDouble("base_chance");
		
		cropEnvironmentConfiguration = new CropEnvironmentConfiguration(config, freshWaterConfiguration, regionalConfiguration, biomeAliases, log);
	}
	
	// ================================================================================================================
	public double getGrowthChance(Block block, RegionGenerator regionalGenerator) {
		return baseChance * cropEnvironmentConfiguration.getMultiplier(block, regionalGenerator);
	}
	
	public boolean growthSucceeds(Block block, RegionGenerator regionalGenerator) {
		return Math.random() < getGrowthChance(block, regionalGenerator);
	}
	
	// ----------------------------------------------------------------------------------------------------------------
	public void dump(Logger log) {
		log.info("CropChanceGrowthConfiguration:");
		log.info("  baseChance: " + baseChance);
		cropEnvironmentConfiguration.dump(log);
	}
	
	public void displayState(Player player, Block block, RegionGenerator regionalGenerator) {
		player.sendMessage("§7[Harvest Time] Base Chance: §8" + String.format("%.2f%%", 100.0 * baseChance));
		cropEnvironmentConfiguration.displayState(player, block, regionalGenerator);
		player.sendMessage("§7" + "[Harvest Time]   §3Total Chance: " + "§b" + String.format("%.2f%%", 100.0 * getGrowthChance(block, regionalGenerator)));
	}
}

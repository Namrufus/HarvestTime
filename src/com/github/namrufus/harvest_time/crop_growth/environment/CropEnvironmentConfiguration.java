package com.github.namrufus.harvest_time.crop_growth.environment;

import java.util.logging.Logger;

import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.github.namrufus.harvest_time.regional.RegionalConfiguration;
import com.github.namrufus.harvest_time.regional.RegionalGenerator;
import com.github.namrufus.harvest_time.util.configuration.BiomeAliasesConfiguration;

public class CropEnvironmentConfiguration {
	private boolean freshWaterEnabled;
	private CropFreshWaterConfiguration freshWaterConfiguration;
	
	private boolean fertilizerBlockEnabled;
	private CropFertilizerBlockConfiguration fertilizerBlockConfiguration;
	
	private boolean regionalEnabled;
	private CropRegionalConfiguration regionalConfiguration;
	
	private boolean biomeEnabled;
	private CropBiomeConfiguration biomeConfiguration;
	
	public CropEnvironmentConfiguration(ConfigurationSection config, FreshWaterConfiguration baseFreshWaterConfiguration, RegionalConfiguration baseRegionalConfiguration, BiomeAliasesConfiguration biomeAliases, Logger log) {
		// each environment modifier section is optional
		freshWaterEnabled = config.isSet("fresh_water");
		if (freshWaterEnabled)
			freshWaterConfiguration = new CropFreshWaterConfiguration(config.getConfigurationSection("fresh_water"), baseFreshWaterConfiguration);
		else
			freshWaterConfiguration = null;
		
		fertilizerBlockEnabled = config.isSet("fertilizer_block");
		if (fertilizerBlockEnabled)
			fertilizerBlockConfiguration = new CropFertilizerBlockConfiguration(config.getConfigurationSection("fertilizer_block"), log);
		else
			fertilizerBlockConfiguration = null;
		
		regionalEnabled = config.isSet("regional");
		if (regionalEnabled)
			regionalConfiguration = new CropRegionalConfiguration(config.getConfigurationSection("regional"), baseRegionalConfiguration, log);
		else
			regionalConfiguration = null;
		
		biomeEnabled = config.isSet("biome");
		if (biomeEnabled)
			biomeConfiguration = new CropBiomeConfiguration(config.getConfigurationSection("biome"), biomeAliases, log);
		else
			biomeConfiguration = null;
	}
	
	// ================================================================================================================
	public double getMultiplier(Block block, RegionalGenerator regionalGenerator) {
		double multiplier = 1.0;
		
		if (freshWaterEnabled)
			multiplier *= freshWaterConfiguration.getMultiplier(block);
		
		if (fertilizerBlockEnabled)
			multiplier *= fertilizerBlockConfiguration.getMultiplier(block);
		
		if (regionalEnabled)
			multiplier *= regionalConfiguration.getMultiplier(block.getLocation(), regionalGenerator);
		
		if (biomeEnabled)
			multiplier *= biomeConfiguration.getMultiplier(block.getBiome());
		
		return multiplier;
	}
	
	// ----------------------------------------------------------------------------------------------------------------
	public void dump(Logger log) {
		if (freshWaterEnabled)
			freshWaterConfiguration.dump(log);
		if (fertilizerBlockEnabled)
			fertilizerBlockConfiguration.dump(log);
		if (regionalEnabled)
			regionalConfiguration.dump(log);
		if (biomeEnabled)
			biomeConfiguration.dump(log);
	}
	
	public void displayState(Player player, Block block, RegionalGenerator regionalGenerator) {
		if (freshWaterEnabled) {
			double multiplier = freshWaterConfiguration.getMultiplier(block);
			if (multiplier != 1.0)
				player.sendMessage("§7"/*light grey*/ + "[Harvest Time]   Fresh Water: " + "§8"/*dark grey*/ + "x" + percentageFormat(multiplier));
		}
		
		if (biomeEnabled) {
			double multiplier = biomeConfiguration.getMultiplier(block.getBiome());
			player.sendMessage("§7"/*light grey*/ + "[Harvest Time]   Biome: " + "§8"/*dark grey*/ + "x" + percentageFormat(multiplier));
		}
		
		if (fertilizerBlockEnabled) {
			double multiplier = fertilizerBlockConfiguration.getMultiplier(block);
			if (multiplier != 1.0)
				player.sendMessage("§7"/*light grey*/ + "[Harvest Time]   Fertilizer Blocks: " + "§8"/*dark grey*/ + "x" + percentageFormat(multiplier));
		}
		
		if (regionalEnabled) {
			double multiplier = regionalConfiguration.getMultiplier(block.getLocation(), regionalGenerator);
			if (multiplier != 1.0)
				player.sendMessage("§7"/*light grey*/ + "[Harvest Time]   Regional Soil State: " + "§8"/*dark grey*/ + "x" + percentageFormat(multiplier));
		}
	}
	
	private static String percentageFormat(double multiplier) {
		return String.format("%.2f%%", (multiplier * 100.0));
	}
}

package com.github.namrufus.harvest_time.configuration.crop.environment;

import java.util.logging.Logger;

import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;

import com.github.namrufus.harvest_time.configuration.BiomeAliasesConfiguration;
import com.github.namrufus.harvest_time.configuration.FreshWaterConfiguration;
import com.github.namrufus.harvest_time.configuration.RegionalConfiguration;
import com.github.namrufus.harvest_time.regional.RegionalGenerator;

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
		
		fertilizerBlockEnabled = config.isSet("fertillizer_block");
		if (fertilizerBlockEnabled)
			fertilizerBlockConfiguration = new CropFertilizerBlockConfiguration(config.getConfigurationSection("fertilizer_block"), log);
		else
			fertilizerBlockConfiguration = null;
		
		regionalEnabled = config.isSet("regional");
		if (regionalEnabled)
			regionalConfiguration = new CropRegionalConfiguration(config.getConfigurationSection("fertilizer_block"), baseRegionalConfiguration, log);
		else
			regionalConfiguration = null;
		
		biomeEnabled = config.isSet("biomes");
		if (biomeEnabled)
			biomeConfiguration = new CropBiomeConfiguration(config.getConfigurationSection("biomes"), biomeAliases, log);
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
}

package com.github.namrufus.harvest_time.crop_growth.environment;

import java.util.logging.Logger;

import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.github.namrufus.harvest_time.crop_growth.environment.global.BiomeAliasesConfiguration;
import com.github.namrufus.harvest_time.crop_growth.environment.global.FreshWaterConfiguration;
import com.github.namrufus.harvest_time.crop_growth.environment.local.CropBiomeConfiguration;
import com.github.namrufus.harvest_time.crop_growth.environment.local.CropFertilizerBlockConfiguration;
import com.github.namrufus.harvest_time.crop_growth.environment.local.CropFreshWaterConfiguration;
import com.github.namrufus.harvest_time.crop_growth.environment.local.CropSunlightConfiguration;
import com.github.namrufus.harvest_time.plugin.global.TextCode;

public class CropEnvironmentConfiguration {
	private boolean sunlightEnabled;
	private CropSunlightConfiguration sunlightConfiguration;
	
	private boolean freshWaterEnabled;
	private CropFreshWaterConfiguration freshWaterConfiguration;
	
	private boolean fertilizerBlockEnabled;
	private CropFertilizerBlockConfiguration fertilizerBlockConfiguration;
	
	private boolean biomeEnabled;
	private CropBiomeConfiguration biomeConfiguration;
	
	public CropEnvironmentConfiguration(ConfigurationSection config, FreshWaterConfiguration baseFreshWaterConfiguration, BiomeAliasesConfiguration biomeAliases, Logger log) {
		// each environment modifier section is optional
		sunlightEnabled = config.isSet("sunlight");
		if (sunlightEnabled)
			sunlightConfiguration = new CropSunlightConfiguration(config.getConfigurationSection("sunlight"));
		else
			sunlightConfiguration = null;
		
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
		
		biomeEnabled = config.isSet("biome");
		if (biomeEnabled)
			biomeConfiguration = new CropBiomeConfiguration(config.getConfigurationSection("biome"), biomeAliases, log);
		else
			biomeConfiguration = null;
	}
	
	// ================================================================================================================
	public double getMultiplier(Block block) {
		double multiplier = 1.0;
		
		if (sunlightEnabled) {
			multiplier *= sunlightConfiguration.getMultiplier(block);
		}
		
		if (freshWaterEnabled) {
			multiplier *= freshWaterConfiguration.getIrrigationMultiplier(block);
			multiplier *= freshWaterConfiguration.getRainfallMultiplier(block);
		}
		
		if (fertilizerBlockEnabled)
			multiplier *= fertilizerBlockConfiguration.getMultiplier(block);
		
		if (biomeEnabled)
			multiplier *= biomeConfiguration.getMultiplier(block.getBiome());
		
		return multiplier;
	}
	
	// ----------------------------------------------------------------------------------------------------------------
	public void dump(Logger log) {
		if (sunlightEnabled)
			sunlightConfiguration.dump(log);
		if (freshWaterEnabled)
			freshWaterConfiguration.dump(log);
		if (fertilizerBlockEnabled)
			fertilizerBlockConfiguration.dump(log);
		if (biomeEnabled)
			biomeConfiguration.dump(log);
	}
	
	public void displayState(Player player, Block block) {
		if (sunlightEnabled) {
			double sunlightMultiplier = sunlightConfiguration.getMultiplier(block);
			if (sunlightMultiplier != 1.0)
				player.sendMessage(TextCode.BASE + TextCode.MESSAGE_PREFIX + "  Sunlight: " + TextCode.VALUE + "x" + percentageFormat(sunlightMultiplier));
		}
		
		if (freshWaterEnabled) {
			double irrigationMultiplier = freshWaterConfiguration.getIrrigationMultiplier(block);
			if (irrigationMultiplier != 1.0)
				player.sendMessage(TextCode.BASE + TextCode.MESSAGE_PREFIX + "   Fresh Water Irrigation: " + TextCode.VALUE + "x" + percentageFormat(irrigationMultiplier));
			
			double rainfallMultiplier = freshWaterConfiguration.getRainfallMultiplier(block);
			if (rainfallMultiplier != 1.0)
				player.sendMessage(TextCode.BASE + TextCode.MESSAGE_PREFIX + "   Rainfall: " + TextCode.VALUE + "x" + percentageFormat(rainfallMultiplier));
		}
		
		if (biomeEnabled) {
			double multiplier = biomeConfiguration.getMultiplier(block.getBiome());
			player.sendMessage(TextCode.BASE + TextCode.MESSAGE_PREFIX + "   Biome: " + TextCode.VALUE + "x" + percentageFormat(multiplier));
		}
		
		if (fertilizerBlockEnabled) {
			double multiplier = fertilizerBlockConfiguration.getMultiplier(block);
			if (multiplier != 1.0)
				player.sendMessage(TextCode.BASE + TextCode.MESSAGE_PREFIX + "   Fertilizer Blocks: " + TextCode.VALUE + "x" + percentageFormat(multiplier));
		}
	}
	
	private static String percentageFormat(double multiplier) {
		return String.format("%.2f%%", (multiplier * 100.0));
	}
}

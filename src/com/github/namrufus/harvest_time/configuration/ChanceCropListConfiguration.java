package com.github.namrufus.harvest_time.configuration;

import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;

import com.github.namrufus.harvest_time.configuration.crop.CropChanceGrowthConfiguration;

public class ChanceCropListConfiguration extends CropDirectory<CropChanceGrowthConfiguration> {
	public ChanceCropListConfiguration(ConfigurationSection config, FreshWaterConfiguration freshWaterConfiguration, RegionalConfiguration regionalConfiguration, BiomeAliasesConfiguration biomeAliases, Logger log) {	
		for (String cropName : config.getKeys(false)) {
			if (!canParse(cropName)) {
				log.warning("Seasonal Crop List Configuration: can't parse crop: "+cropName+". Skipping.");
				continue;
			}
			
			ConfigurationSection cropConfig = config.getConfigurationSection(cropName);
			CropChanceGrowthConfiguration cropConfiguration = new CropChanceGrowthConfiguration(cropConfig, freshWaterConfiguration, regionalConfiguration, biomeAliases, log);
		
			put(cropName, cropConfiguration);
		}
	}
	
	// ================================================================================================================
	public void dump(Logger log) {
		log.info("");
		log.info("Begin SeasonalCropListConfiguration:");
		
		for (CropIdentifier identifier : getCrops().keySet()) {
			log.info("  "+identifier.toString());
		}
	}
	
	public void dumpCrop(String cropName, Logger log) {
		if (!canParse(cropName)) {
			log.info("Can't parse "+cropName);
			return;
		}
		
		CropChanceGrowthConfiguration configuration = get(cropName);
		
		if (configuration == null) {
			log.info("No chance crop record for "+cropName);
			return;
		}
		
		log.info(cropName.toUpperCase() + ":");
		configuration.dump(log);
	}
}

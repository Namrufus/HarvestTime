package com.github.namrufus.harvest_time.crop_growth.seasonal_growth;

import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;

import com.github.namrufus.harvest_time.crop_growth.crop_directory.CropDirectory;
import com.github.namrufus.harvest_time.crop_growth.environment.global.BiomeAliasesConfiguration;
import com.github.namrufus.harvest_time.crop_growth.environment.global.FreshWaterConfiguration;

public class SeasonalCropListConfiguration extends CropDirectory<CropSeasonalGrowthConfiguration> {
	public SeasonalCropListConfiguration(ConfigurationSection config, FreshWaterConfiguration freshWaterConfiguration, BiomeAliasesConfiguration biomeAliases, Logger log) {	
		for (String cropName : config.getKeys(false)) {
			if (!canParse(cropName)) {
				log.warning("Seasonal Crop List Configuration: can't parse crop: "+cropName+". Skipping.");
				continue;
			}
			
			ConfigurationSection cropConfig = config.getConfigurationSection(cropName);
			CropSeasonalGrowthConfiguration cropConfiguration = new CropSeasonalGrowthConfiguration(cropConfig, freshWaterConfiguration, biomeAliases, log);
		
			put(cropName, cropConfiguration);
		}
	}
	
	// ================================================================================================================
	public void dump(Logger log) {
		log.info("");
		log.info("SeasonalCropListConfiguration:");
		
		for (CropIdentifier identifier : getCrops().keySet()) {
			log.info("  " + identifier);
		}
	}
	
	public void dumpCrop(String cropName, Logger log) {
		if (!canParse(cropName)) {
			log.info("Can't parse "+cropName);
			return;
		}
		
		CropSeasonalGrowthConfiguration configuration = get(cropName);
		
		if (configuration == null) {
			log.info("No seasonal crop record for "+cropName);
			return;
		}
		
		log.info(cropName.toUpperCase() + ":");
		configuration.dump(log);
	}
}

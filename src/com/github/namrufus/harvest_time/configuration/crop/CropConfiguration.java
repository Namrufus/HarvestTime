package com.github.namrufus.harvest_time.configuration.crop;

import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;

import com.github.namrufus.harvest_time.configuration.BiomeAliasesConfiguration;
import com.github.namrufus.harvest_time.configuration.FreshWaterConfiguration;
import com.github.namrufus.harvest_time.configuration.RegionalConfiguration;

public class CropConfiguration {
	boolean hasChanceGrowthConfiguration;
	CropChanceGrowthConfiguration chanceGrowthConfiguration;
	boolean hasSeasonalGrowthConfiguration;
	CropSeasonalGrowthConfiguration seasonalGrowthConfiguration;
	boolean hasHarvestingConfiguration;
	CropHarvestingConfiguration harvestingConfiguration;
	
	public CropConfiguration(ConfigurationSection config, FreshWaterConfiguration freshWaterConfiguration, RegionalConfiguration regionalConfiguration, BiomeAliasesConfiguration biomeAliases, Logger log) {
		hasChanceGrowthConfiguration = config.isSet("chance_growth");
		hasSeasonalGrowthConfiguration = config.isSet("seasonal_growth");
		
		if (hasChanceGrowthConfiguration && hasSeasonalGrowthConfiguration) {
			log.warning("Config: crop configuration cannot have both chance_growth and seasonal_growth entries: using seasonal growth entry");
			hasChanceGrowthConfiguration = false;
		}
		
		if (hasChanceGrowthConfiguration)
			chanceGrowthConfiguration = new CropChanceGrowthConfiguration(config.getConfigurationSection("chance_growth"), freshWaterConfiguration, regionalConfiguration, biomeAliases, log);
		else
			chanceGrowthConfiguration = null;
		
		if (hasSeasonalGrowthConfiguration)
			seasonalGrowthConfiguration = new CropSeasonalGrowthConfiguration(config.getConfigurationSection("seasonal_growth"));
		else
			seasonalGrowthConfiguration = null;
		
		hasHarvestingConfiguration = config.isSet("harvesting");
		if (hasHarvestingConfiguration)
			chanceGrowthConfiguration = new CropChanceGrowthConfiguration(config.getConfigurationSection("harvesting"), freshWaterConfiguration, regionalConfiguration, biomeAliases, log);
		else
			chanceGrowthConfiguration = null;
	}
	
	// ================================================================================================================
	public boolean hasChanceGrowthConfiguration() { return hasChanceGrowthConfiguration; }
	public CropChanceGrowthConfiguration getChanceGrowthConfiguration() { return chanceGrowthConfiguration; }
	
	public boolean hasSeasonalGrowthConfiguration() { return hasSeasonalGrowthConfiguration; }
	public CropSeasonalGrowthConfiguration getSeasonalGrowthConfiguration() { return seasonalGrowthConfiguration; }
	
	public boolean hasHarvestingConfiguration() { return hasHarvestingConfiguration; }
	public CropHarvestingConfiguration getHarvestingConfiguration() { return harvestingConfiguration; }
	
	// ----------------------------------------------------------------------------------------------------------------
	public void dump(Logger log) {
		log.info("CropConfiguration: ");
		log.info("  chanceGrowthConfiguration: "+hasChanceGrowthConfiguration);
		log.info("  seasonalGrowthConfiguration: "+hasSeasonalGrowthConfiguration);
		log.info("  harvestingConfiguration: "+hasHarvestingConfiguration);
	}
}

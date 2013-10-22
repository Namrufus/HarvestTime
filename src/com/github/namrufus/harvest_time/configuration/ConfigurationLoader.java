package com.github.namrufus.harvest_time.configuration;

import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;

// loads all configuration data and provides a configuration dump utility

public class ConfigurationLoader {
	private BonemealDisabledConfiguration bonemealDisabledConfiguration;
	private SeasonalConfiguration seasonalConfiguration;
	private FarmlandCreationConfiguration farmlandCreationConfiguration;
	private HarvestingConfiguration harvestingConfiguration;
	private FreshWaterConfiguration freshWaterConfiguration;
	private RegionalConfiguration regionalConfiguration;
	private BiomeAliasesConfiguration biomeAliasesConfiguration;
	private CropListConfiguration cropListConfiguration;
	
	public ConfigurationLoader(ConfigurationSection config, Logger LOG) {
		bonemealDisabledConfiguration = new BonemealDisabledConfiguration(config.getConfigurationSection("bonemeal_disabled"), LOG);
		seasonalConfiguration = new SeasonalConfiguration(config.getConfigurationSection("seasonal"), LOG);
		farmlandCreationConfiguration = new FarmlandCreationConfiguration(config.getConfigurationSection("farmland_creation"), LOG);
		harvestingConfiguration = new HarvestingConfiguration(config.getConfigurationSection("harvesting"), LOG);
		freshWaterConfiguration = new FreshWaterConfiguration(config.getConfigurationSection("fresh_water"), LOG);
		regionalConfiguration = new RegionalConfiguration(config.getConfigurationSection("regional"), LOG);
		biomeAliasesConfiguration = new BiomeAliasesConfiguration(config.getConfigurationSection("biome_aliases"), LOG);
		cropListConfiguration = new CropListConfiguration(config.getConfigurationSection("crop_list"), freshWaterConfiguration, regionalConfiguration, biomeAliasesConfiguration, LOG);
	}
	
	// ================================================================================================================
	public BonemealDisabledConfiguration getBonemealDisabledConfiguration() { return bonemealDisabledConfiguration; }
	public SeasonalConfiguration getSeasonalConfiguration() { return seasonalConfiguration; }
	public FarmlandCreationConfiguration getFarmlandCreationConfiguration() {return farmlandCreationConfiguration; }
	public HarvestingConfiguration getHarvestingConfiguration() { return harvestingConfiguration; }
	public RegionalConfiguration getRegionalConfiguration() { return regionalConfiguration; }
	public FreshWaterConfiguration getFreshWaterConfiguration() { return freshWaterConfiguration; }
	public CropListConfiguration getCropsConfiguration() { return cropListConfiguration; }
	
	// ----------------------------------------------------------------------------------------------------------------
	public void dumpConfiguration(Logger LOG) {
		LOG.info("Start Configuration Dump:");
		LOG.info("");
		bonemealDisabledConfiguration.dump(LOG);
		seasonalConfiguration.dump(LOG);
		farmlandCreationConfiguration.dump(LOG);
		harvestingConfiguration.dump(LOG);
		freshWaterConfiguration.dump(LOG);
		regionalConfiguration.dump(LOG);
		biomeAliasesConfiguration.dump(LOG);
		cropListConfiguration.dump(LOG);
		LOG.info("");
		LOG.info("End Configuration Dump.");
	}
}

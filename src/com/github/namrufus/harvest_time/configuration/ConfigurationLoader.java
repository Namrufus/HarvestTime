package com.github.namrufus.harvest_time.configuration;

import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;

// loads all configuration data and provides a configuration dump utility

public class ConfigurationLoader {
	private BonemealDisabledConfiguration bonemealDisabledConfiguration;
	private SeasonalConfiguration seasonalConfiguration;
	private FarmlandCreationConfiguration farmlandCreationConfiguration;
	private TendingConfiguration tendingConfiguration;
	private FreshWaterConfiguration freshWaterConfiguration;
	private RegionalConfiguration regionalConfiguration;
	private BiomeAliasesConfiguration biomeAliasesConfiguration;
	private SeasonalCropListConfiguration seasonalCropListConfiguration;
	private ChanceCropListConfiguration chanceCropListConfiguration;
	
	public ConfigurationLoader(ConfigurationSection config, Logger log) {
		bonemealDisabledConfiguration = new BonemealDisabledConfiguration(config.getConfigurationSection("bonemeal_disabled"), log);
		seasonalConfiguration = new SeasonalConfiguration(config.getConfigurationSection("seasonal"), log);
		farmlandCreationConfiguration = new FarmlandCreationConfiguration(config.getConfigurationSection("farmland_creation"), log);
		tendingConfiguration = new TendingConfiguration(config.getConfigurationSection("tending"), log);
		freshWaterConfiguration = new FreshWaterConfiguration(config.getConfigurationSection("fresh_water"), log);
		regionalConfiguration = new RegionalConfiguration(config.getConfigurationSection("regional"), log);
		biomeAliasesConfiguration = new BiomeAliasesConfiguration(config.getConfigurationSection("biome_aliases"), log);
		seasonalCropListConfiguration = new SeasonalCropListConfiguration(config.getConfigurationSection("seasonal_crop_list"), freshWaterConfiguration, regionalConfiguration, biomeAliasesConfiguration, log);
		chanceCropListConfiguration = new ChanceCropListConfiguration(config.getConfigurationSection("chance_crop_list"), freshWaterConfiguration, regionalConfiguration, biomeAliasesConfiguration, log);
	}
	
	// ================================================================================================================
	public BonemealDisabledConfiguration getBonemealDisabledConfiguration() { return bonemealDisabledConfiguration; }
	public SeasonalConfiguration getSeasonalConfiguration() { return seasonalConfiguration; }
	public FarmlandCreationConfiguration getFarmlandCreationConfiguration() {return farmlandCreationConfiguration; }
	public TendingConfiguration getHarvestingConfiguration() { return tendingConfiguration; }
	public RegionalConfiguration getRegionalConfiguration() { return regionalConfiguration; }
	public FreshWaterConfiguration getFreshWaterConfiguration() { return freshWaterConfiguration; }
	public SeasonalCropListConfiguration getSeasonalCropListConfiguration() { return seasonalCropListConfiguration; }
	public ChanceCropListConfiguration getChanceCropListConfiguration() { return chanceCropListConfiguration; }
	
	// ----------------------------------------------------------------------------------------------------------------
	public void dumpConfiguration(Logger log) {
		log.info("Start Configuration Dump:");
		log.info("");
		bonemealDisabledConfiguration.dump(log);
		seasonalConfiguration.dump(log);
		farmlandCreationConfiguration.dump(log);
		tendingConfiguration.dump(log);
		freshWaterConfiguration.dump(log);
		regionalConfiguration.dump(log);
		biomeAliasesConfiguration.dump(log);
		seasonalCropListConfiguration.dump(log);
		chanceCropListConfiguration.dump(log);
		log.info("");
		log.info("End Configuration Dump.");
	}
	
	public void dumpCrop(String cropName, Logger log) {
		seasonalCropListConfiguration.dumpCrop(cropName, log);
		chanceCropListConfiguration.dumpCrop(cropName, log);
	}
}
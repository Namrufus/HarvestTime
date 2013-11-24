package com.github.namrufus.harvest_time.plugin;

import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;

import com.github.namrufus.harvest_time.bonemeal.BonemealDisabledConfiguration;
import com.github.namrufus.harvest_time.crop_growth.chance_growth.ChanceCropListConfiguration;
import com.github.namrufus.harvest_time.crop_growth.environment.global.BiomeAliasesConfiguration;
import com.github.namrufus.harvest_time.crop_growth.environment.global.FreshWaterConfiguration;
import com.github.namrufus.harvest_time.crop_growth.environment.global.region.RegionConfiguration;
import com.github.namrufus.harvest_time.crop_growth.seasonal_growth.SeasonalCropListConfiguration;
import com.github.namrufus.harvest_time.crop_growth.seasonal_growth.TendingConfiguration;
import com.github.namrufus.harvest_time.farmland.FarmlandCreationConfiguration;
import com.github.namrufus.harvest_time.seasonal.SeasonalConfiguration;

// loads all configuration data and provides a configuration dump utility

public class ConfigurationLoader {
	private InteractionConfiguration interactionConfiguration;
	private BonemealDisabledConfiguration bonemealDisabledConfiguration;
	private SeasonalConfiguration seasonalConfiguration;
	private FarmlandCreationConfiguration farmlandCreationConfiguration;
	private TendingConfiguration tendingConfiguration;
	private FreshWaterConfiguration freshWaterConfiguration;
	private RegionConfiguration regionConfiguration;
	private BiomeAliasesConfiguration biomeAliasesConfiguration;
	private SeasonalCropListConfiguration seasonalCropListConfiguration;
	private ChanceCropListConfiguration chanceCropListConfiguration;
	
	public ConfigurationLoader(ConfigurationSection config, Logger log) {
		interactionConfiguration = new InteractionConfiguration(config.getConfigurationSection("interaction"), log);
		bonemealDisabledConfiguration = new BonemealDisabledConfiguration(config.getConfigurationSection("bonemeal_disabled"), log);
		seasonalConfiguration = new SeasonalConfiguration(config.getConfigurationSection("seasonal"), log);
		farmlandCreationConfiguration = new FarmlandCreationConfiguration(config.getConfigurationSection("farmland_creation"), log);
		tendingConfiguration = new TendingConfiguration(config.getConfigurationSection("tending"), log);
		freshWaterConfiguration = new FreshWaterConfiguration(config.getConfigurationSection("fresh_water"), log);
		regionConfiguration = new RegionConfiguration(config.getConfigurationSection("regional"), log);
		biomeAliasesConfiguration = new BiomeAliasesConfiguration(config.getConfigurationSection("biome_aliases"), log);
		seasonalCropListConfiguration = new SeasonalCropListConfiguration(config.getConfigurationSection("seasonal_crop_list"), freshWaterConfiguration, regionConfiguration, biomeAliasesConfiguration, log);
		chanceCropListConfiguration = new ChanceCropListConfiguration(config.getConfigurationSection("chance_crop_list"), freshWaterConfiguration, regionConfiguration, biomeAliasesConfiguration, log);
	}
	
	// ================================================================================================================
	public InteractionConfiguration getInteractionConfiguration() { return interactionConfiguration; }
	public BonemealDisabledConfiguration getBonemealDisabledConfiguration() { return bonemealDisabledConfiguration; }
	public SeasonalConfiguration getSeasonalConfiguration() { return seasonalConfiguration; }
	public FarmlandCreationConfiguration getFarmlandCreationConfiguration() {return farmlandCreationConfiguration; }
	public TendingConfiguration getTendingConfiguration() { return tendingConfiguration; }
	public RegionConfiguration getRegionConfiguration() { return regionConfiguration; }
	public FreshWaterConfiguration getFreshWaterConfiguration() { return freshWaterConfiguration; }
	public SeasonalCropListConfiguration getSeasonalCropListConfiguration() { return seasonalCropListConfiguration; }
	public ChanceCropListConfiguration getChanceCropListConfiguration() { return chanceCropListConfiguration; }
	
	// ----------------------------------------------------------------------------------------------------------------
	public void dumpConfiguration(Logger log) {
		log.info("Start Configuration Dump:");
		log.info("");
		interactionConfiguration.dump(log);
		bonemealDisabledConfiguration.dump(log);
		seasonalConfiguration.dump(log);
		farmlandCreationConfiguration.dump(log);
		tendingConfiguration.dump(log);
		freshWaterConfiguration.dump(log);
		regionConfiguration.dump(log);
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

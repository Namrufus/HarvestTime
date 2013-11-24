package com.github.namrufus.harvest_time.crop_growth.environment.global.region;

import java.util.Arrays;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;

import com.github.namrufus.harvest_time.crop_growth.environment.global.region.type.RegionMultiplierConfiguration;
import com.github.namrufus.harvest_time.crop_growth.environment.global.region.type.RegionPreference;
import com.github.namrufus.harvest_time.crop_growth.environment.global.region.type.RegionState;


public class RegionConfiguration {
	private static final RegionPreference[] nutrientPreferences = RegionPreference.Nutrients.class.getEnumConstants();
	private static final RegionState[] nutrientStates = RegionState.Nutrients.class.getEnumConstants();
	
	private static final RegionPreference[] phPreferences = RegionPreference.Ph.class.getEnumConstants();
	private static final RegionState[] phStates = RegionState.Ph.class.getEnumConstants();
	
	private static final RegionPreference[] compactnessPreferences = RegionPreference.Compactness.class.getEnumConstants();
	private static final RegionState[] compactnessStates = RegionState.Compactness.class.getEnumConstants();
	
	// ================================================================================================================
	private boolean enabled;
	private String worldName;
	private String seed;
	private double cellSize;
	private double variationSize;
	
	private RegionMultiplierConfiguration nutrientRegionConfiguration, phRegionConfiguration, compactnessRegionConfiguration;
	
	public RegionConfiguration(ConfigurationSection config, Logger LOG) {
		enabled = config.getBoolean("enabled");
		worldName = config.getString("world_name");
		seed = config.getString("seed");
		cellSize = config.getDouble("cell_size");
		variationSize = config.getDouble("variation_size");
		
		ConfigurationSection nutrientConfig = config.getConfigurationSection("nutrients");
		nutrientRegionConfiguration = new RegionMultiplierConfiguration(Arrays.asList(nutrientPreferences), Arrays.asList(nutrientStates), nutrientConfig, LOG);
		
		ConfigurationSection phConfig = config.getConfigurationSection("ph");
		phRegionConfiguration = new RegionMultiplierConfiguration(Arrays.asList(phPreferences), Arrays.asList(phStates), phConfig, LOG);
		
		ConfigurationSection compactnessConfig = config.getConfigurationSection("compactness");
		compactnessRegionConfiguration = new RegionMultiplierConfiguration(Arrays.asList(compactnessPreferences), Arrays.asList(compactnessStates), compactnessConfig, LOG);
	}
	
	// ================================================================================================================
	public boolean isEnabled() { return enabled; }
	public String getWorldName() { return worldName; }
	public String getSeed() { return seed; }
	public double getCellSize() { return cellSize; }
	public double getVariationSize() { return variationSize; }
	
	public double getMultiplier(RegionPreference.Nutrients nutrientsPreference, RegionPreference.Ph phPreference, RegionPreference.Compactness compactnessPreference,
			                    RegionState.Nutrients nutrientsState, RegionState.Ph phState, RegionState.Compactness compactnessState) {
		double multiplier = 1.0;
		
		multiplier *= nutrientRegionConfiguration.getMultiplier(nutrientsPreference, nutrientsState);
		multiplier *= phRegionConfiguration.getMultiplier(phPreference, phState);
		multiplier *= compactnessRegionConfiguration.getMultiplier(compactnessPreference, compactnessState);
		
		return multiplier;
	}
	
	// ----------------------------------------------------------------------------------------------------------------
	public void dump(Logger log) {
		log.info("RegionConfiguration");
		log.info("  enabled: " + enabled);
		log.info("  worldName: " + worldName);
		log.info("  seed: " + seed);
		log.info("  cellSize: " + cellSize);
		log.info("  variationSize: " + variationSize);
		log.info("  nutrients:");
		nutrientRegionConfiguration.dump(log);
		log.info("  ph:");
		phRegionConfiguration.dump(log);
		log.info("  compactness:");
		compactnessRegionConfiguration.dump(log);
	}
}

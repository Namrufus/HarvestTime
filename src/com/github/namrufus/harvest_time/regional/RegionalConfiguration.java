package com.github.namrufus.harvest_time.regional;

import java.util.Arrays;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;

import com.github.namrufus.harvest_time.regional.region.RegionConfig;
import com.github.namrufus.harvest_time.regional.region.RegionPreference;
import com.github.namrufus.harvest_time.regional.region.RegionState;

public class RegionalConfiguration {
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
	
	private RegionConfig nutrientRegionConfig, phRegionConfig, compactnessRegionConfig;
	
	public RegionalConfiguration(ConfigurationSection config, Logger LOG) {
		enabled = config.getBoolean("enabled");
		worldName = config.getString("world_name");
		seed = config.getString("seed");
		cellSize = config.getDouble("cell_size");
		variationSize = config.getDouble("variation_size");
		
		ConfigurationSection nutrientConfig = config.getConfigurationSection("nutrients");
		nutrientRegionConfig = new RegionConfig(Arrays.asList(nutrientPreferences), Arrays.asList(nutrientStates), nutrientConfig, LOG);
		
		ConfigurationSection phConfig = config.getConfigurationSection("ph");
		phRegionConfig = new RegionConfig(Arrays.asList(phPreferences), Arrays.asList(phStates), phConfig, LOG);
		
		ConfigurationSection compactnessConfig = config.getConfigurationSection("compactness");
		compactnessRegionConfig = new RegionConfig(Arrays.asList(compactnessPreferences), Arrays.asList(compactnessStates), compactnessConfig, LOG);
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
		
		multiplier *= nutrientRegionConfig.getMultiplier(nutrientsPreference, nutrientsState);
		multiplier *= phRegionConfig.getMultiplier(phPreference, phState);
		multiplier *= compactnessRegionConfig.getMultiplier(compactnessPreference, compactnessState);
		
		return multiplier;
	}
	
	// ----------------------------------------------------------------------------------------------------------------
	public void dump(Logger log) {
		log.info("RegionalConfiguration");
		log.info("  enabled: " + enabled);
		log.info("  worldName: " + worldName);
		log.info("  seed: " + seed);
		log.info("  cellSize: " + cellSize);
		log.info("  variationSize: " + variationSize);
		log.info("  nutrients:");
		nutrientRegionConfig.dump(log);
		log.info("  ph:");
		phRegionConfig.dump(log);
		log.info("  compactness:");
		compactnessRegionConfig.dump(log);
	}
}

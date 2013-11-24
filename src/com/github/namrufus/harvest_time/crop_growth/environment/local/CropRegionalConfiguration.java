package com.github.namrufus.harvest_time.crop_growth.environment.local;

import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import com.github.namrufus.harvest_time.crop_growth.environment.global.region.RegionConfiguration;
import com.github.namrufus.harvest_time.crop_growth.environment.global.region.RegionGenerator;
import com.github.namrufus.harvest_time.crop_growth.environment.global.region.type.RegionPreference;
import com.github.namrufus.harvest_time.crop_growth.environment.global.region.type.RegionState;
import com.github.namrufus.harvest_time.util.ConfigUtil;

public class CropRegionalConfiguration {
	RegionConfiguration regionalConfiguration;
	
	private RegionPreference.Nutrients nutrientsPreference;
	private RegionPreference.Ph phPreference;
	private RegionPreference.Compactness compactnessPreference;
	
	public CropRegionalConfiguration(ConfigurationSection config, RegionConfiguration regionalConfiguration, Logger log) {
		this.regionalConfiguration = regionalConfiguration;
		
		String nutrientsPreferenceName = config.getString("nutrients");
		nutrientsPreference = ConfigUtil.enumFromString(RegionPreference.Nutrients.class, nutrientsPreferenceName);
		
		String phPreferenceName = config.getString("ph");
		phPreference = ConfigUtil.enumFromString(RegionPreference.Ph.class, phPreferenceName);
		
		String compactnessPreferenceName = config.getString("compactness");
		compactnessPreference = ConfigUtil.enumFromString(RegionPreference.Compactness.class, compactnessPreferenceName);
	}
	
	// ================================================================================================================
	public double getMultiplier(Location location, RegionGenerator regionalGenerator) {
		// sample region generator for the region's state
		RegionState.Nutrients nutrientsState = regionalGenerator.getNutrientState(location);
		RegionState.Ph phState = regionalGenerator.getPhState(location);
		RegionState.Compactness compactnessState = regionalGenerator.getCompactnessState(location);
		
		// get the combined multiplier from the three region type preferences
		return regionalConfiguration.getMultiplier(nutrientsPreference, phPreference, compactnessPreference, nutrientsState, phState, compactnessState);
	}
	
	// ----------------------------------------------------------------------------------------------------------------
	public void dump(Logger log) {
		log.info("  CropRegionalConfiguration:");
		log.info("    nutrientsPreference: " + nutrientsPreference);
		log.info("    phPreference: " + nutrientsPreference);
		log.info("    compactnessPreference: " + compactnessPreference);
	}
}

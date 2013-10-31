package com.github.namrufus.harvest_time.crop_growth.environment;

import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import com.github.namrufus.harvest_time.regional.RegionalConfiguration;
import com.github.namrufus.harvest_time.regional.RegionalGenerator;
import com.github.namrufus.harvest_time.regional.region.RegionPreference;
import com.github.namrufus.harvest_time.regional.region.RegionState;
import com.github.namrufus.harvest_time.util.configuration.ConfigUtil;

public class CropRegionalConfiguration {
	RegionalConfiguration regionalConfiguration;
	
	private RegionPreference.Nutrients nutrientsPreference;
	private RegionPreference.Ph phPreference;
	private RegionPreference.Compactness compactnessPreference;
	
	public CropRegionalConfiguration(ConfigurationSection config, RegionalConfiguration regionalConfiguration, Logger log) {
		this.regionalConfiguration = regionalConfiguration;
		
		String nutrientsPreferenceName = config.getString("nutrients");
		nutrientsPreference = ConfigUtil.enumFromString(RegionPreference.Nutrients.class, nutrientsPreferenceName);
		
		String phPreferenceName = config.getString("ph");
		phPreference = ConfigUtil.enumFromString(RegionPreference.Ph.class, phPreferenceName);
		
		String compactnessPreferenceName = config.getString("compactness");
		compactnessPreference = ConfigUtil.enumFromString(RegionPreference.Compactness.class, compactnessPreferenceName);
	}
	
	// ================================================================================================================
	public double getMultiplier(Location location, RegionalGenerator regionalGenerator) {
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

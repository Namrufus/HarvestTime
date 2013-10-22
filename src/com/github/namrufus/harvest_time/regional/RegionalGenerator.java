package com.github.namrufus.harvest_time.regional;

import org.bukkit.Location;

import com.github.namrufus.harvest_time.configuration.region.RegionState;

public class RegionalGenerator {
	
	// ================================================================================================================
	public RegionState.Nutrients getNutrientState(Location location) {
		// TODO
		return RegionState.Nutrients.FAIR;
	}
	public RegionState.Ph getPhState(Location location) {
		// TODO
		return RegionState.Ph.NEUTRAL;
	}
	public RegionState.Compactness getCompactnessState(Location location) {
		// TODO
		return RegionState.Compactness.LOOSE;
	}
}

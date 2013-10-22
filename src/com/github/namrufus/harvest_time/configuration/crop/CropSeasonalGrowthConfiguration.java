package com.github.namrufus.harvest_time.configuration.crop;

import org.bukkit.configuration.ConfigurationSection;

// records the growth parameters of a crop type and handles checking for allowed growth

public class CropSeasonalGrowthConfiguration {
	
	private boolean requiresSunlight;

	// the first day on which the plant may be planted and the growth cycles start
	private int startDay;
	
	private int maxStageDifference;
	
	// ================================================================================================================
	public CropSeasonalGrowthConfiguration(ConfigurationSection rootConfig) {
		
		requiresSunlight = rootConfig.getBoolean("requires_sunlight");
		
		startDay = rootConfig.getInt("start_day");
		
		maxStageDifference = rootConfig.getInt("max_stage_difference");
	}
	
	// ================================================================================================================
	public boolean doesRequiresSunlight() { return requiresSunlight; }
	
	// ================================================================================================================
	// -- growth ------------------------------------------------------------------------------------------------------
	
	public int getTargetStage(int seasonalDay) {
		int targetStage = seasonalDay - startDay;
		
		if (targetStage < 0)
			targetStage = 0;
		
		return targetStage;
	}
	
	public boolean isAllowedToGrow(int currentStage, int seasonalDay) {
		int targetStage = getTargetStage(seasonalDay);
		
		int difference = targetStage - currentStage;
		
		return difference > 0 && difference <= maxStageDifference;
	}
	
	public boolean isAtTargetStage(int currentStage, int seasonalDay) {
		int targetStage = getTargetStage(seasonalDay);
		
		return currentStage == targetStage;
	}
}

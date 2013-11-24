package com.github.namrufus.harvest_time.crop_growth.seasonal_growth;

import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;

import com.github.namrufus.harvest_time.crop_growth.environment.global.BiomeAliasesConfiguration;
import com.github.namrufus.harvest_time.crop_growth.environment.global.FreshWaterConfiguration;
import com.github.namrufus.harvest_time.crop_growth.environment.global.region.RegionConfiguration;

// records the growth parameters of a crop type and handles checking for allowed growth

public class CropSeasonalGrowthConfiguration {
	// the seasonal day index on which plant growth starts
	private int startDay;
	// the maximum difference in growth for which a plant will be viable for growth
	private int maxStageDifference;
	
	// custom yield
	private boolean hasCustomYield;
	private CustomYieldConfiguration customYieldConfiguration;
	
	// ================================================================================================================
	public CropSeasonalGrowthConfiguration(ConfigurationSection config, FreshWaterConfiguration freshWaterConfiguration, RegionConfiguration regionalConfiguration, BiomeAliasesConfiguration biomeAliases, Logger log) {
		startDay = config.getInt("start_day");
		maxStageDifference = config.getInt("max_stage_difference");
		
		hasCustomYield = config.isSet("custom_yield");
		if (hasCustomYield)
			customYieldConfiguration = new CustomYieldConfiguration(config.getConfigurationSection("custom_yield"), freshWaterConfiguration, regionalConfiguration, biomeAliases, log);
		else
			customYieldConfiguration = null;
	}
	
	// ================================================================================================================
	public int getStartDay() { return startDay; }
	public int getFinalDay(int growthStageCount) { return startDay + growthStageCount + maxStageDifference; }
	public int getFinalPlantingDay() { return startDay + maxStageDifference; }
	
	public boolean hasCustomYield() { return hasCustomYield; }
	public CustomYieldConfiguration getCustomYieldConfiguration() { return customYieldConfiguration; }
	
	// ----------------------------------------------------------------------------------------------------------------
	public int getUncappedTargetStage(int seasonalDay) {
		int targetStage = seasonalDay - startDay;
		
		if (targetStage < 0)
			targetStage = 0;
		
		return targetStage;
	}
	
	public int getCappedTargetStage(int seasonalDay, int stageCount) {
		int targetStage =  getUncappedTargetStage(seasonalDay);
		
		if (targetStage >= stageCount - 1)
			targetStage = stageCount - 1;
		
		return targetStage;
	}
	
	// get the index of the final seasonal day on which this crop will still be viable to be grown
	public int getFinalViableSeasonalDay(int currentStage) {
		return startDay + currentStage + maxStageDifference;
	}
	
	public boolean isAllowedToGrow(int currentStage, int seasonalDay) {
		int targetStage = getUncappedTargetStage(seasonalDay);
		
		int difference = targetStage - currentStage;
		
		return difference > 0 && difference <= maxStageDifference;
	}
	
	public boolean isAtTargetStage(int currentStage, int seasonalDay, int stageCount) {
		int targetStage = getCappedTargetStage(seasonalDay, stageCount);
		
		return currentStage == targetStage;
	}
	
	// ----------------------------------------------------------------------------------------------------------------
	public void dump(Logger log) {
		log.info("CropSeasonalGrowthConfiguration: ");
		log.info("  startDay: "+startDay);
		log.info("  maxStageDifference: "+maxStageDifference);
		log.info("  hasCustomYield: "+hasCustomYield);
		if (hasCustomYield)
			customYieldConfiguration.dump(log);
	}
}

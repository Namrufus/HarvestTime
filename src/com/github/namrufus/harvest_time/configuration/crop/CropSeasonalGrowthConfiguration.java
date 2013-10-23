package com.github.namrufus.harvest_time.configuration.crop;

import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;

import com.github.namrufus.harvest_time.configuration.BiomeAliasesConfiguration;
import com.github.namrufus.harvest_time.configuration.FreshWaterConfiguration;
import com.github.namrufus.harvest_time.configuration.RegionalConfiguration;

// records the growth parameters of a crop type and handles checking for allowed growth

public class CropSeasonalGrowthConfiguration {
	
	private boolean requiresSunlight;

	// the seasonal day index on which plant growth starts
	private int startDay;
	// the maximum difference in growth for which a plant will be viable for growth
	private int maxStageDifference;
	
	// custom yield
	private boolean hasCustomYield;
	private CustomYieldConfiguration customYieldConfiguration;
	
	// ================================================================================================================
	public CropSeasonalGrowthConfiguration(ConfigurationSection config, FreshWaterConfiguration freshWaterConfiguration, RegionalConfiguration regionalConfiguration, BiomeAliasesConfiguration biomeAliases, Logger log) {
		requiresSunlight = config.getBoolean("requires_sunlight");
		
		startDay = config.getInt("start_day");
		maxStageDifference = config.getInt("max_stage_difference");
		
		hasCustomYield = config.isSet("custom_yield");
		if (hasCustomYield)
			customYieldConfiguration = new CustomYieldConfiguration(config.getConfigurationSection("custom_yield"), freshWaterConfiguration, regionalConfiguration, biomeAliases, log);
		else
			customYieldConfiguration = null;
	}
	
	// ================================================================================================================
	public boolean doesRequiresSunlight() { return requiresSunlight; }
	
	public boolean hasCustomYield() { return hasCustomYield; }
	public CustomYieldConfiguration getCustomYieldConfiguration() { return customYieldConfiguration; }
	
	// ----------------------------------------------------------------------------------------------------------------
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
	
	// ----------------------------------------------------------------------------------------------------------------
	public void dump(Logger log) {
		log.info("CropSeasonalGrowthConfiguration: ");
		log.info("  requiresSunlight: "+requiresSunlight);
		log.info("  startDay: "+startDay);
		log.info("  maxStageDifference: "+maxStageDifference);
		log.info("  hasCustomYield: "+hasCustomYield);
		if (hasCustomYield)
			customYieldConfiguration.dump(log);
	}
}

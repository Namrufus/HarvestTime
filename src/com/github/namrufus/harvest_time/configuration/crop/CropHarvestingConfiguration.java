package com.github.namrufus.harvest_time.configuration.crop;

import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;

import com.github.namrufus.harvest_time.configuration.BiomeAliasesConfiguration;
import com.github.namrufus.harvest_time.configuration.FreshWaterConfiguration;
import com.github.namrufus.harvest_time.configuration.HarvestingConfiguration;
import com.github.namrufus.harvest_time.configuration.RegionalConfiguration;
import com.github.namrufus.harvest_time.regional.RegionalGenerator;

public class CropHarvestingConfiguration {
	private static final int LIGHT_MAX = 15;
	
	// ================================================================================================================
	// if this crop requires sunlight to give a yield
	private boolean requiresSunlight;
	
	// if this crop requires a tool to be harvested
	private HarvestingConfiguration harvestingConfiguration;
	private boolean requiresTool;
	private double baseToolTime;

	private CustomYieldConfiguration customYieldConfiguration;
	
	public CropHarvestingConfiguration(ConfigurationSection config, HarvestingConfiguration harvestingConfiguration, FreshWaterConfiguration freshWaterConfiguration, RegionalConfiguration regionalConfiguration, BiomeAliasesConfiguration biomeAliases, Logger log) {
		requiresSunlight = config.getBoolean("requires_sunlight");
		
		this.harvestingConfiguration = harvestingConfiguration;
		requiresTool = config.getBoolean("requires_tool");
		baseToolTime = config.getDouble("base_tool_time");
		
		if (config.isSet("custom_yield"))
			customYieldConfiguration = new CustomYieldConfiguration(config.getConfigurationSection("custom_yield"), freshWaterConfiguration, regionalConfiguration, biomeAliases, log);
		else
			customYieldConfiguration = null;
	}
	
	// ================================================================================================================
	public boolean doesRequireTool() { return requiresTool; }
	public double getHarvestToolTime(Material toolMaterial) {
		if (!requiresTool)
			return 0.0;
		
		return baseToolTime * harvestingConfiguration.getToolHarvestMultiplier(toolMaterial);
	}
	
	public boolean hasCustomYield() { return customYieldConfiguration != null; }
	public double getCustomYieldCount(Block block, RegionalGenerator regionalGenerator) {
		if (customYieldConfiguration == null)
			return 0.0;
		
		if (requiresSunlight && block.getLightFromSky() < LIGHT_MAX)
			return 0.0;
		
		return customYieldConfiguration.getYieldCount(block, regionalGenerator);
	}
}

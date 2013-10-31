package com.github.namrufus.harvest_time.util.configuration;

import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;


public class InteractionConfiguration {
	private Material cropGrowthCheckMaterial, cropYieldCheckMaterial;
	
	public InteractionConfiguration(ConfigurationSection config, Logger log) {
		String growthString = config.getString("crop_growth_check_material");
		cropGrowthCheckMaterial = ConfigUtil.enumFromString(Material.class, growthString);
		if (cropGrowthCheckMaterial == null) {
			cropGrowthCheckMaterial = Material.WATCH;
			log.warning("Config: can't recognize growth_check_material "+growthString+". using default: " + cropGrowthCheckMaterial);
		}
		
		String yieldString = config.getString("crop_yield_check_material");
		cropYieldCheckMaterial = ConfigUtil.enumFromString(Material.class, yieldString);
		if (cropYieldCheckMaterial == null) {
			cropYieldCheckMaterial = Material.COMPASS;
			log.warning("Config: can't recognize yield_check_material "+yieldString+". using default: " + cropYieldCheckMaterial);
		}
	}
	
	// ================================================================================================================
	public Material getCropGrowthCheckMaterial() { return cropGrowthCheckMaterial; }
	public Material getCropYieldCheckMaterial() { return cropYieldCheckMaterial; }
	
	// ----------------------------------------------------------------------------------------------------------------
	public void dump(Logger log) {
		log.info("InteractionConfiguration");
		log.info("  cropGrowthCheckMaterial: " + cropGrowthCheckMaterial);
		log.info("  cropYieldCheckMaterial: " + cropYieldCheckMaterial);
	}
}

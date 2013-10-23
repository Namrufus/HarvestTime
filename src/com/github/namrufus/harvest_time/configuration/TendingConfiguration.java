package com.github.namrufus.harvest_time.configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import com.github.namrufus.harvest_time.configuration.util.ConfigUtil;

public class TendingConfiguration {
	int radius;
	double baseTime;
	private Map<Material, Double> toolMultipliers;
	
	public TendingConfiguration(ConfigurationSection config, Logger log) {
		radius = config.getInt("radius");
		baseTime = config.getDouble("base_time");
		
		toolMultipliers = new HashMap<Material, Double>();
		
		ConfigurationSection toolSection = config.getConfigurationSection("tool_multipliers");
		for (String toolMaterialName : toolSection.getKeys(false)) {
			Material toolMaterial = ConfigUtil.enumFromString(Material.class, toolMaterialName);
			if (toolMaterial == null) {
				log.warning("Tool Harvest Time Configuration: can't identify "+toolMaterialName+". Skipping.");
				continue;
			}
			toolMultipliers.put(toolMaterial, toolSection.getDouble(toolMaterialName));
		}
	}
	
	// ================================================================================================================
	public int getRadius() { return radius; }
	public double getBaseTime() { return baseTime; }
	
	public boolean isHarvestTool(Material material) {
		return toolMultipliers.containsKey(material);
	}
	public double getToolHarvestMultiplier(Material material) {
		if (!isHarvestTool(material))
			return 1.0;
		
		return toolMultipliers.get(material);
	}
	
	// ----------------------------------------------------------------------------------------------------------------
	public void dump(Logger log) {
		log.info("HarvestingConfiguration:");
		log.info("  toolMultipliers:");
		for (Material tool : toolMultipliers.keySet()) {
			log.info("    " + tool + ": " + toolMultipliers.get(tool));
		}
	}
}

package com.github.namrufus.harvest_time.configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import com.github.namrufus.harvest_time.configuration.util.ConfigUtil;

public class TendingConfiguration {
	int radius;
	private Map<Material, Double> toolTimes;
	
	public TendingConfiguration(ConfigurationSection config, Logger log) {
		radius = config.getInt("radius");
		
		toolTimes = new HashMap<Material, Double>();
		
		ConfigurationSection toolSection = config.getConfigurationSection("tool_times");
		for (String toolMaterialName : toolSection.getKeys(false)) {
			Material toolMaterial = ConfigUtil.enumFromString(Material.class, toolMaterialName);
			if (toolMaterial == null) {
				log.warning("Tool Harvest Time Configuration: can't identify "+toolMaterialName+". Skipping.");
				continue;
			}
			toolTimes.put(toolMaterial, toolSection.getDouble(toolMaterialName));
		}
	}
	
	// ================================================================================================================
	public int getRadius() { return radius; }
	
	public boolean isTendingTool(Material material) {
		return toolTimes.containsKey(material);
	}
	public double getToolTendingTime(Material material) {
		if (!isTendingTool(material))
			return 0.0;
		
		return toolTimes.get(material);
	}
	
	// ----------------------------------------------------------------------------------------------------------------
	public void dump(Logger log) {
		log.info("HarvestingConfiguration:");
		log.info("  toolMultipliers:");
		for (Material tool : toolTimes.keySet()) {
			log.info("    " + tool + ": " + toolTimes.get(tool));
		}
	}
}

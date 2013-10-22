package com.github.namrufus.harvest_time.configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import com.github.namrufus.harvest_time.configuration.util.ConfigUtil;

public class HarvestingConfiguration {
	private Map<Material, Double> toolMultipliers;
	
	public HarvestingConfiguration(ConfigurationSection config, Logger LOG) {
		toolMultipliers = new HashMap<Material, Double>();
		
		ConfigurationSection toolSection = config.getConfigurationSection("tool_harvest_multipliers");
		for (String toolMaterialName : toolSection.getKeys(false)) {
			Material toolMaterial = ConfigUtil.enumFromString(Material.class, toolMaterialName);
			if (toolMaterial == null) {
				LOG.warning("Tool Harvest Time Configuration: can't identify "+toolMaterialName+". Skipping.");
				continue;
			}
			toolMultipliers.put(toolMaterial, toolSection.getDouble(toolMaterialName));
		}
	}
	
	// ================================================================================================================
	public boolean isHarvestTool(Material material) {
		return toolMultipliers.containsKey(material);
	}
	public double getToolHarvestMultiplier(Material material) {
		if (!isHarvestTool(material))
			return 1.0;
		
		return toolMultipliers.get(material);
	}
	
	// ----------------------------------------------------------------------------------------------------------------
	public void dump(Logger LOG) {
		LOG.info("HarvestingConfiguration:");
		LOG.info("  toolMultipliers:");
		for (Material tool : toolMultipliers.keySet()) {
			LOG.info("    " + tool + ": " + toolMultipliers.get(tool));
		}
	}
}

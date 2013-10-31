package com.github.namrufus.harvest_time.farmland;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public class FarmlandCreationConfiguration {
	private boolean enabled;
	
	private Material blockType;
	private int blockCount;
	
	private Map<Material, Double> toolTimes;
	
	// ================================================================================================================
	public FarmlandCreationConfiguration(ConfigurationSection rootConfig, Logger LOG) {
		enabled = rootConfig.getBoolean("enabled");
		
		// ............................................................................................................
		String blockTypeName = rootConfig.getString("block_type");
		try {
			blockType = Material.valueOf(blockTypeName);
		} catch(IllegalArgumentException e) {
			LOG.warning("Farmland Creation Config: Can't identify Material: "+blockTypeName+". Disabling farmland creation service.");
			enabled = false;
		}
		
		blockCount = rootConfig.getInt("block_count");
		
		// ............................................................................................................
		// iterate through the tool_times section in order to get the times associated with each tool
		toolTimes = new HashMap<Material, Double>();
		ConfigurationSection toolTimeConfig = rootConfig.getConfigurationSection("tool_time");
		for (String toolMaterialName : toolTimeConfig.getKeys(false)) {
			Material toolMaterial;
			try {
				toolMaterial = Material.valueOf(toolMaterialName);
			} catch(IllegalArgumentException e) {
				LOG.warning("Farmland Creation Tool Time Config: Can't identify Material: "+toolMaterialName+". Skipping.");
				continue;
			}
			
			double toolTime = toolTimeConfig.getDouble(toolMaterialName);
			
			toolTimes.put(toolMaterial, toolTime);
		}
	}
	
	// ================================================================================================================
	public boolean isEnabled() { return enabled; }
	
	public Material getBlockType() { return blockType; }
	public int getBlockCount() { return blockCount; }
	
	public boolean isFarmlandTool(Material material) {
		return toolTimes.containsKey(material);
	}
	public double getToolTime(Material material) {
		if (!isFarmlandTool(material))
			return 1.0;
		
		return toolTimes.get(material);
	}
	
	// ----------------------------------------------------------------------------------------------------------------
	public void dump(Logger LOG) {
		LOG.info("FarmlandCreationConfiguration:");
		LOG.info("  enabled: " + enabled);
		LOG.info("  blockType: " + blockType);
		LOG.info("  blockCount: " + blockCount);
		LOG.info("  toolTimes:");
		for (Material tool : toolTimes.keySet()) {
			LOG.info("    " + tool + ": " + toolTimes.get(tool));
		}
	}
}

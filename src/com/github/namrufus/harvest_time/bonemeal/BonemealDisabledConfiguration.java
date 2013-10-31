package com.github.namrufus.harvest_time.bonemeal;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import com.github.namrufus.harvest_time.util.configuration.ConfigUtil;

public class BonemealDisabledConfiguration {
	// set of block materials on which bonemeal will not be usable on
	Set<Material> disabledMaterials;
	
	public BonemealDisabledConfiguration(ConfigurationSection config, Logger log) {
		disabledMaterials = new HashSet<Material>();
		
		for (String materialName : config.getStringList("materials")) {
			Material material = ConfigUtil.enumFromString(Material.class, materialName);
			if (material == null) {
				log.warning("Config: bonemeal disabled, can't recognize material: "+materialName+". Skipping.");
				continue;
			}
			
			disabledMaterials.add(material);
		}
	}
	
	// ================================================================================================================
	public boolean isBonemealDisabled(Material blockMaterial) {
		return disabledMaterials.contains(blockMaterial);
	}
	
	// ----------------------------------------------------------------------------------------------------------------
	public void dump(Logger log) {
		log.info("BonemealDisabledConfiguration:");
		log.info("  disabledMaterials:");
		for (Material material : disabledMaterials) {
			log.info("    "+material);
		}
	}
}

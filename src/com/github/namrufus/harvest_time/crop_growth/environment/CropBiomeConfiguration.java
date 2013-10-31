package com.github.namrufus.harvest_time.crop_growth.environment;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;

import com.github.namrufus.harvest_time.util.configuration.BiomeAliasesConfiguration;
import com.github.namrufus.harvest_time.util.configuration.ConfigUtil;

public class CropBiomeConfiguration {
	private double defaultMultiplier;
	
	private Map<Biome, Double> biomeMultipliers;
	
	public CropBiomeConfiguration(ConfigurationSection config, BiomeAliasesConfiguration biomeAliases, Logger log) {
		defaultMultiplier = 0.0;
		biomeMultipliers = new HashMap<Biome, Double>();
		
		for (String biomeName : config.getKeys(false)) {			
			double multiplier = config.getDouble(biomeName);
			
			// match default
			if (biomeName.equals("DEFAULT")) {
				defaultMultiplier = multiplier;
				continue;
			}
			
			// match a biome alias
			if (biomeAliases.contains(biomeName)) {
				for (Biome biome : biomeAliases.getBiomeAliases(biomeName)) {
					biomeMultipliers.put(biome, multiplier);
				}
				continue;
			} 
			
			// match single biome types
			Biome biome = ConfigUtil.enumFromString(Biome.class, biomeName);
			if (biome != null) {
				biomeMultipliers.put(biome, multiplier);
				continue;
			}
			
			// if nothing matches, print warning
			log.warning("Biome Multiplier config: can't match " + biomeName + " to biome or biome alias. Skipping.");
		}
	}
	
	// ================================================================================================================
	public double getMultiplier(Biome biome) {
		if (biomeMultipliers.containsKey(biome)) {
			return biomeMultipliers.get(biome);
		} else {
			return defaultMultiplier;
		}
	}
	
	// ----------------------------------------------------------------------------------------------------------------
	public void dump(Logger log) {
		log.info("  CropBiomeConfiguration:");
		log.info("    DEFAULT: " + defaultMultiplier);
		for (Biome biome : biomeMultipliers.keySet()) {
			log.info("    "+biome+": "+biomeMultipliers.get(biome));
		}
	}
}

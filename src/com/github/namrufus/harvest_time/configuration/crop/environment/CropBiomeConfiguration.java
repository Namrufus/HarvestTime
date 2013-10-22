package com.github.namrufus.harvest_time.configuration.crop.environment;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;

import com.github.namrufus.harvest_time.configuration.BiomeAliasesConfiguration;
import com.github.namrufus.harvest_time.configuration.util.ConfigUtil;

public class CropBiomeConfiguration {
	private static final double defaultBiomeMultiplier = 0.0;
	
	private Map<Biome, Double> biomeMultipliers;
	
	public CropBiomeConfiguration(ConfigurationSection config, BiomeAliasesConfiguration biomeAliases, Logger log) {
		biomeMultipliers = new HashMap<Biome, Double>();
		
		for (String biomeName : config.getKeys(false)) {
			double multiplier = config.getDouble(biomeName);
			
			// first try to match a biome alias
			if (biomeAliases.contains(biomeName)) {
				for (Biome biome : biomeAliases.getBiomeAliases(biomeName)) {
					biomeMultipliers.put(biome, multiplier);
				}
				continue;
			} 
			
			// then just single biome types
			Biome biome = ConfigUtil.enumFromString(Biome.class, biomeName);
			if (biome != null) {
				biomeMultipliers.put(biome, multiplier);
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
			return defaultBiomeMultiplier;
		}
	}
}

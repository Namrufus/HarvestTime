package com.github.namrufus.harvest_time.util.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;



// this class provides a way to give a list of biomes a string "alias" the refers to many biomes at once,
// in order to simplify biome configurations

public class BiomeAliasesConfiguration {
	private Map<String, List<Biome>> biomeAliases;
	
	public BiomeAliasesConfiguration(ConfigurationSection config, Logger LOG) {
		biomeAliases = new HashMap<String, List<Biome>>();
		
		for (String alias : config.getKeys(false)) {
			List<Biome> aliases = new ArrayList<Biome>();
			
			for (String biomeName : config.getStringList(alias)) {
				Biome biome = ConfigUtil.enumFromString(Biome.class, biomeName);
				if (biome == null) {
					LOG.warning("Biome Alias Config: can't recognize biome: " + biomeName + ". Skipping.");
					continue;
				}
				aliases.add(biome);
			}
			
			biomeAliases.put(alias, aliases);
		}
	}
	
	// ================================================================================================================
	// returns true if the given string is an alias
	public boolean contains(String alias) {
		return biomeAliases.containsKey(alias);
	}
	
	// returns the list of biomes attached to the given alias
	public List<Biome> getBiomeAliases(String alias) {
		if (biomeAliases.containsKey(alias)) {
			return biomeAliases.get(alias);
		} else {
			return Collections.emptyList();
		}
	}
	
	// ----------------------------------------------------------------------------------------------------------------
	public void dump(Logger LOG) {
		LOG.info("BiomeAliasConfiguration:");
		
		for (String alias : biomeAliases.keySet()) {
			String biomeString = "";
			
			biomeString += "  "+alias+": [ ";
			for (Biome biome : biomeAliases.get(alias)) {
				biomeString += biome.toString() + " ";
			}
			
			biomeString += "]";
			LOG.info(biomeString);
		}
	}
}

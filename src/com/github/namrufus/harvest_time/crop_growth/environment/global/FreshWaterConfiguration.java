package com.github.namrufus.harvest_time.crop_growth.environment.global;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;

import com.github.namrufus.harvest_time.util.ConfigUtil;

public class FreshWaterConfiguration {
	private static final int MAX_LIGHT = 15;
	
	// ================================================================================================================
	// fresh water from being near open water near a "fresh water" biome (like RIVER)
	private boolean biomesEnabled;
	private int biomeMinY;
	private int biomeMaxY;
	private int biomeRadius;
	private Set<Biome> biomes;
	
	public FreshWaterConfiguration(ConfigurationSection config, Logger LOG) {
		ConfigurationSection biomeConfig = config.getConfigurationSection("biome");
		
		biomesEnabled = biomeConfig.getBoolean("enabled");
		
		biomeMinY = biomeConfig.getInt("min_y");
		biomeMaxY = biomeConfig.getInt("max_y");
		
		biomeRadius = biomeConfig.getInt("radius");
		
		biomes = new HashSet<Biome>();
		for (String biomeName : biomeConfig.getStringList("biomes")) {
			Biome biome = ConfigUtil.enumFromString(Biome.class, biomeName);
			if (biome == null) {
				LOG.warning("Fresh water config: can't recognize biome: " + biomeName + ". Skipping.");
				continue;
			}
			
			biomes.add(biome);
		}
	}
	
	// ================================================================================================================
	public boolean hasFreshWater(Block block) {
		return hasBiomeFreshWater(block);
	}
	
	public boolean hasBiomeFreshWater(Block block) {
		if (!biomesEnabled)
			return false;
		
		int x = block.getX(), y = block.getY(), z = block.getZ();
		World world = block.getWorld();
		
		if (checkBiomeWater(world, x+biomeRadius, y, z))
			return true;
		if (checkBiomeWater(world, x, y, z+biomeRadius))
			return true;
		if (checkBiomeWater(world, x-biomeRadius, y, z))
			return true;
		if (checkBiomeWater(world, x, y, z-biomeRadius))
			return true;
		
		return false;
	}
	
	// determine if the given location is above water at the specified coordinates (in the configured irrigation y range)
	private boolean checkBiomeWater(World world, int blockX, int blockY, int blockZ) {
		for (int y = blockY - 1; y > 0 && y >= biomeMinY && y <= biomeMaxY; y--) {
			Block block = world.getBlockAt(blockX, y, blockZ);
			
			if (block.getType() == Material.STATIONARY_WATER)
				return true;
			
			if (block.getType() != Material.AIR || block.getLightFromSky() != MAX_LIGHT)
				return false;
		}
		
		return false;
	}
	
	// ----------------------------------------------------------------------------------------------------------------
	public void dump(Logger LOG) {
		LOG.info("FreshWaterConfiguration:");
		LOG.info("  biomesEnabled: "+biomesEnabled);
		LOG.info("  biomeMinY: "+biomeMinY);
		LOG.info("  biomeMaxY: "+biomeMaxY);
		LOG.info("  biomeRadius: "+biomeRadius);
	}
}

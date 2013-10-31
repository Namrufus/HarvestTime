package com.github.namrufus.harvest_time.configuration;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;

import com.github.namrufus.harvest_time.configuration.util.ConfigUtil;

public class FreshWaterConfiguration {
	// temperature values in between which rain falls
	// this may change in the future depending on Mojang
	private static final double minTemperature = 0.15, maxTemperature = 1.5;
	private static final int MAX_LIGHT = 15;
	
	// ================================================================================================================
	// fresh water from being near open water near a "fresh water" biome (like RIVER)
	private boolean biomesEnabled;
	private int biomeMinY;
	private int biomeMaxY;
	private int biomeRadius;
	private Set<Biome> biomes;
	
	// fresh water from being open in the rain
	private boolean rainfallEnabled;
	
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
		
		// .................
		
		rainfallEnabled = config.getBoolean("rainfall.enabled");
	}
	
	// ================================================================================================================
	public boolean hasFreshWater(Block block) {
		return hasRainfallFreshWater(block) || hasBiomeFreshWater(block);
	}
	
	public boolean hasBiomeFreshWater(Block block) {
		if (!biomesEnabled)
			return false;
		
		int x = block.getX(), y = block.getY(), z = block.getZ();
		World world = block.getWorld();
		
		System.out.println("what?");
		
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
	
	public boolean hasRainfallFreshWater(Block block) {
		if (!rainfallEnabled)
			return false;
		
		World world = block.getWorld();
		
		// if the world is not in a rain state, the block is not receiving rainfall
		if (!world.hasStorm())
			return false;
		
		// if the biome does not support rain, then the block is not receiving rainfall
		if (block.getTemperature() < minTemperature || block.getTemperature() > maxTemperature)
			return false;
		
		// determine if the block is exposed to the sky
		// world.getHighestBlockAt() and related functions are not usable here because they only include blocks that are not transparent
		// (glass etc is not taken into account, which is pretty much the point of this - to determine if the block is out in the rain)
		
		if (block.getLightFromSky() != MAX_LIGHT) {
			if (block.getY() != world.getMaxHeight() - 1 || block.getRelative(0, 1, 0).getLightFromSky() != MAX_LIGHT) {
				return false; /* blocks without 100% sky lighting cannot be under the sky */
			}
		}
		
		// check all blocks above the given block for blocks that block rain
		int x = block.getX(), z = block.getZ();
		for (int y = block.getY() + 1; y < world.getMaxHeight(); y++) {
			// solid blocks block rain (I think...)
			if (world.getBlockAt(x, y, z).getType().isSolid())
				return false;
		}
		// if the top of the world is reached, the sky is clear to the build limit
		return true;
	}
	
	// ----------------------------------------------------------------------------------------------------------------
	public void dump(Logger LOG) {
		LOG.info("FreshWaterConfiguration:");
		LOG.info("  biomesEnabled: "+biomesEnabled);
		LOG.info("  biomeMinY: "+biomeMinY);
		LOG.info("  biomeMaxY: "+biomeMaxY);
		LOG.info("  biomeRadius: "+biomeRadius);
		LOG.info("  rainfallEnabled: "+rainfallEnabled);
	}
}

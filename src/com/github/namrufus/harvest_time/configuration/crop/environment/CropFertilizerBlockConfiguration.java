package com.github.namrufus.harvest_time.configuration.crop.environment;

import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;

import com.github.namrufus.harvest_time.configuration.util.ConfigUtil;

public class CropFertilizerBlockConfiguration {
	private Material blockType;
	private int blockCount;
	private int blockOffset;
	
	private double multiplier;
	
	public CropFertilizerBlockConfiguration(ConfigurationSection config, Logger LOG) {
		String blockTypeName = config.getString("block_type");
		blockType = ConfigUtil.enumFromString(Material.class, blockTypeName);
		if (blockType == null) {
			blockType = Material.CLAY;
			LOG.warning("Fertilizer Block Config: don't recognize material: " + blockTypeName + "using default: " + blockType.toString());
		}
		
		blockCount = config.getInt("block_count");
		blockOffset = config.getInt("block_offset");
		
		multiplier = config.getInt("multiplier");
	}
	
	// ================================================================================================================
	public double getMultiplier(Block block) {
		// if there are enough fertilizer blocks, then use the multiplier
		// else no chance at 100%
		if (getMatchingBlockCount(block) == blockCount)
			return multiplier;
		else
			return 1.0;
	}
	
	private int getMatchingBlockCount(Block block) {
		int blockX = block.getX(), blockZ = block.getZ();
		World world = block.getWorld();
		
		int startY = block.getY() - blockOffset;
		
		// check if the required number of blocks are underneath the crop block
		for (int i = 0; i<blockCount; i++) {
			int y = startY - i;
			// if the y coordinate is below the world or any of the blocks do not match the
			// correct type, then the multiplier is not applied
			if (y < 0 || world.getBlockAt(blockX, y, blockZ).getType() == blockType)
				return i;
		}
		
		return blockCount;
	}
}

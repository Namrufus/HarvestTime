package com.github.namrufus.harvest_time.crop_growth.seasonal.util;

import org.bukkit.Material;
import org.bukkit.block.Block;

// utilities for handling the growth stages of specific crops

public class GrowthUtil {
	// == growth stages ===============================================================================================
	// get the growth stage of the crop in the block specified
	public static int getStage(Block block) {
		Material material = block.getType();
		int data = block.getData();
		
		if (material == Material.CROPS ||
		    material == Material.PUMPKIN_STEM ||
		    material == Material.MELON_STEM) {
			// standard stage types, just the data value
			return data;
		} else if (material == Material.CARROT ||
				   material == Material.POTATO) {		
			if (data == 6)
				data--;
			
			return data / 2;
		} else if (material == Material.NETHER_WARTS) {
			if (data == 0)
				return 0;
			else if (data == 1 || data == 2)
				return 1;
			else /* data == 3 */
				return 2;
		} else if (material == Material.COCOA) {
			// cocoa must account for the different orientations
			return data / 4;
		} else {
			// no stages
			return 0;
		}
	}
	
	// set the growth stage of the crop in the block specified
	public static void setStage(Block block, int stage) {
		Material material = block.getType();
		
		if (material == Material.CROPS ||
			    material == Material.PUMPKIN_STEM ||
			    material == Material.MELON_STEM) {
			
				block.setData((byte) stage);
			} else if (material == Material.CARROT ||
					   material == Material.POTATO) {
				stage = stage * 2 + 1;
				
				block.setData((byte) stage);
			} else if (material == Material.NETHER_WARTS) {
				if (stage == 0)
					stage = 0;
				else if (stage == 1)
					stage = 1;
				else /* stage == 2 */
					stage = 3;
				
				block.setData((byte) stage);
			} else if (material == Material.COCOA) {
				byte orientation = (byte) (block.getData() % 4);
				block.setData((byte) ((stage * 4) + orientation));
			} else {
				// no stages
			}
	}
	
	// get the number of growth stages of the material specified
	public static int getStageCount(Material material) {
		if (material == Material.CROPS ||
		    material == Material.PUMPKIN_STEM ||
		    material == Material.MELON_STEM) {
			return 8;
		} else if (material == Material.CARROT ||
				   material == Material.POTATO) {
			return 4;
		} else if (material == Material.NETHER_WARTS) {
			return 3;
		} else if (material == Material.COCOA) {
			return 3;
		} else {
			// no stages
			return 0;
		}
	}
}

package com.github.namrufus.harvest_time.seasonal_growth;

import org.bukkit.Material;
import org.bukkit.block.Block;

// utilities for handling the growth stages of specific crops

public class GrowthUtil {
	// == growth stages ===============================================================================================
	// get the growth stage of the crop in the block specified
	public static int getStage(Block block) {
		Material material = block.getType();
		int data = block.getData();
		
		if (material == Material.CROPS || material == Material.CARROT || material == Material.POTATO ||
		  material == Material.PUMPKIN_STEM || material == Material.MELON_STEM) {
			// standard stage types, just the data value
			return data;
		} else if (material == Material.NETHER_STALK) {
			// nether warts have half the images
			return data / 2;
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
		
		if (material == Material.CROPS || material == Material.CARROT || material == Material.POTATO ||
	      material == Material.PUMPKIN_STEM || material == Material.MELON_STEM) {
			// standard stage types, just the data value
			block.setData((byte)stage);
		} else if (material == Material.NETHER_STALK) {
			// nether warts have half the images
			block.setData((byte)(stage*2));
		} else if (material == Material.COCOA) {
			// cocoa must account for the different orientations
			block.setData((byte)((block.getData()%4) + stage*4));
		} else {
			// no stages
		}
	}
	
	// get the number of growth stages of the material specified
	public static int getStageCount(Material material) {
		if (material == Material.CROPS || material == Material.CARROT || material == Material.POTATO ||
		  material == Material.PUMPKIN_STEM || material == Material.MELON_STEM) {
			// standard stage types, just the data value
			return 8;
		} else if (material == Material.NETHER_STALK) {
			// nether warts have half the images
			return 4;
		} else if (material == Material.COCOA) {
			// cocoa must account for the different orientations
			return 4;
		} else {
			// no stages
			return 0;
		}
	}
}

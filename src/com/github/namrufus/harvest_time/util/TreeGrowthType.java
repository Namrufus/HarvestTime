package com.github.namrufus.harvest_time.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;

public enum TreeGrowthType {
	OAK (Material.SAPLING, 0, new TreeType[]{TreeType.BIG_TREE, TreeType.SWAMP, TreeType.TREE}),
	SPRUCE (Material.SAPLING, 1, new TreeType[]{TreeType.REDWOOD, TreeType.TALL_REDWOOD}),
	BIRCH (Material.SAPLING, 2, new TreeType[]{TreeType.BIRCH}),
	JUNGLE (Material.SAPLING, 3, new TreeType[]{TreeType.JUNGLE, TreeType.JUNGLE_BUSH, TreeType.SMALL_JUNGLE}),
	RED_MUSHROOM (Material.RED_MUSHROOM, new TreeType[]{TreeType.RED_MUSHROOM}),
	BROWN_MUSHROOM (Material.BROWN_MUSHROOM, new TreeType[]{TreeType.BROWN_MUSHROOM});
	
	// ================================================================================================================
	// provide mappings from both treetype and material to treegrowthtype
	private static Map<MaterialData, TreeGrowthType> byMaterial = new HashMap<MaterialData, TreeGrowthType>();
	private static Map<TreeType, TreeGrowthType> byTreeType = new HashMap<TreeType, TreeGrowthType>();
	static {
		for (TreeGrowthType treeGrowthType : TreeGrowthType.values()) {
			byMaterial.put(treeGrowthType.materialData, treeGrowthType);
			for (TreeType treeType : treeGrowthType.treeTypes) {
				byTreeType.put(treeType, treeGrowthType);
			}
		}
	}
	
	private static MaterialData getMaterialData(Block block) {
		Material material = block.getType();
		// mask out all but the bottom 3 bits (sapling type)
		int data;
		if (material == Material.SAPLING)
			data = block.getData() & 0x07;
		else
			data = block.getData();
		
		MaterialData materialData = new MaterialData(material);
		materialData.setData((byte) data);
		return materialData;
	}
	public static boolean hasTreeGrowthType(Block block) {
		return byMaterial.containsKey(getMaterialData(block));
	}
	public static TreeGrowthType getTreeGrowthType(Block block) {
		return byMaterial.get(getMaterialData(block));
	}
	
	public static boolean hasTreeGrowthType(TreeType treeType) {
		return byTreeType.containsKey(treeType);
	}
	public static TreeGrowthType getTreeGrowthType(TreeType treeType) {
		return byTreeType.get(treeType);
	}
	
	// ================================================================================================================
	private MaterialData materialData;
	private List<TreeType> treeTypes;
	
	private TreeGrowthType(Material material, int data, TreeType[] treeTypes) {
		this.materialData = new MaterialData(material);
		this.materialData.setData((byte)data);
		this.treeTypes = Arrays.asList(treeTypes);
	}
	private TreeGrowthType(Material material, TreeType[] treeTypes) {
		this(material, 0, treeTypes);
	}
}

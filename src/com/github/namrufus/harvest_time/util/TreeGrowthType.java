package com.github.namrufus.harvest_time.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.TreeType;
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
	
	public static boolean hasTreeGrowthType(Material material, int data) {
		MaterialData materialData = new MaterialData(material);
		materialData.setData((byte) data);
		return byMaterial.containsKey(materialData);
	}
	public static TreeGrowthType getTreeGrowthType(Material material, int data) {
		MaterialData materialData = new MaterialData(material);
		materialData.setData((byte) data);
		return byMaterial.get(materialData);
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

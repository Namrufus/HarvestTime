package com.github.namrufus.harvest_time.harvesting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class CropYeildUtil {
	private static class YeildMaterialData {
		private MaterialData primaryMaterialData, propagationMaterialData;
		private double propagationCount;
		
		public YeildMaterialData(MaterialData primaryMaterialData, MaterialData propagationMaterialData, double propagationCount) {
			this.primaryMaterialData = primaryMaterialData;
			this.propagationMaterialData = propagationMaterialData;
			this.propagationCount = propagationCount;
		}
		
		public YeildMaterialData(MaterialData primaryMaterialData, double propagationCount) {
			this.primaryMaterialData = primaryMaterialData;
			this.propagationMaterialData = primaryMaterialData;
			this.propagationCount = propagationCount;
		}
		
		public MaterialData getPrimaryMaterialData() { return primaryMaterialData; }
		public MaterialData getPropagationMaterialData() { return propagationMaterialData; }
		public double getPropagationCount() { return propagationCount; }
	}
	private static Map<Material, YeildMaterialData> yeildMaterials;
	static {
		yeildMaterials = new HashMap<Material, YeildMaterialData>();
		
		yeildMaterials.put(Material.CROPS, new YeildMaterialData(new MaterialData(Material.WHEAT), new MaterialData(Material.SEEDS), 1));
		
		yeildMaterials.put(Material.POTATO, new YeildMaterialData(new MaterialData(Material.POTATO_ITEM), 1));
		yeildMaterials.put(Material.CARROT, new YeildMaterialData(new MaterialData(Material.CARROT_ITEM), 1));
		
		yeildMaterials.put(Material.PUMPKIN_STEM, new YeildMaterialData(new MaterialData(Material.PUMPKIN), new MaterialData(Material.PUMPKIN_SEEDS), 1));
		yeildMaterials.put(Material.MELON_STEM, new YeildMaterialData(new MaterialData(Material.MELON_BLOCK), new MaterialData(Material.MELON_SEEDS), 1));
		
		yeildMaterials.put(Material.NETHER_STALK, new YeildMaterialData(new MaterialData(Material.NETHER_WARTS), 1));
	}
	
	// ================================================================================================================
	public static List<ItemStack> getYeild(Material material, double averageYeild, double variance, boolean includePropagation) {
		YeildMaterialData yeildMaterialData = yeildMaterials.get(material);
		if (yeildMaterials == null)
			return null;
		
		double variedYeild = averageYeild * (1 + variance * (Math.random() * 2.0 - 1.0));
		int trueYeild = randomRound(variedYeild);
		
		List<ItemStack> items = new ArrayList<ItemStack>();
		
		for (int i = 0; i < trueYeild; i++) {
			MaterialData materialData = yeildMaterialData.getPrimaryMaterialData();
			ItemStack itemStack = new ItemStack(materialData.getItemType());
			itemStack.setData(materialData);
			items.add(itemStack);
		}
		
		if (includePropagation) {
			int propagationCount = randomRound(yeildMaterialData.getPropagationCount());
			for (int i = 0; i < propagationCount; i++) {
				MaterialData materialData = yeildMaterialData.getPropagationMaterialData();
				ItemStack itemStack = new ItemStack(materialData.getItemType());
				itemStack.setData(materialData);
				items.add(itemStack);
			}
		}
		
		return items;
	}
	
	// round up or down from x, with the resulting distribution of numbers having an
	// average of x
	private static int randomRound(double x) {
		int result = (int)Math.floor(x);
		double chance = x - result;
		
		if (Math.random() < chance)
			result++;
		
		return result;
	}
}
